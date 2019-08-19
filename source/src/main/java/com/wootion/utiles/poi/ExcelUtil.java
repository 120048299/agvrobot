package com.wootion.utiles.poi;

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExcelUtil {
    private Workbook workbook;
    private InputStream inputStream;

    private int activeSheetIndex = 0;
    private FormulaEvaluator formulaEvaluator;
    // 日期的格式化字符串
    private String datePattern = "yyyy/MM/dd";

    public void setDatePattern(String datePattern) {
        this.datePattern = datePattern;
    }

    private Map<String, CellStyle> mapCellStyle = new HashMap<>();

    /**
     * 取得cell的格式化样式
     *
     * @param format
     *            这个format是Excel单元格支持的格式字符串 例如： 整数：#，##0 浮点数：#，##0.00 日期：yyyy/mm/dd
     *            yyyy"年"m"月"d"日"
     * @return CellStyle
     */
    private CellStyle getFormatCellStyle(String format) {
        if (!mapCellStyle.containsKey(format)) {
            CellStyle style = this.workbook.createCellStyle();
            DataFormat df = this.workbook.createDataFormat();
            style.setDataFormat(df.getFormat(format));
            mapCellStyle.put(format, style);
        }
        return mapCellStyle.get(format);
    }

    // 序号从0开始
    /**
     * @param file
     *            excel文件的路径
     * @throws Exception
     */
    public ExcelUtil(File file) throws Exception {

        String fileName = file.getName();

        String prefix = fileName.substring(fileName.lastIndexOf('.') + 1);
        inputStream = new FileInputStream(file);
        Throwable localThrowable3 = null;
        try {
            if ("xls".equals(prefix)) {
                workbook = new HSSFWorkbook(inputStream);
                formulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) workbook);
            } else if ("xlsx".equals(prefix)) {
                workbook = new XSSFWorkbook(inputStream);
                formulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) workbook);
            }
        } catch (Throwable localThrowable1) {
            localThrowable3 = localThrowable1;
            throw localThrowable1;
        } finally {
            if (inputStream != null) {
                if (localThrowable3 != null) {
                    try {
                        inputStream.close();
                    } catch (Throwable localThrowable2) {
                        localThrowable3.addSuppressed(localThrowable2);
                    }
                } else {
                    inputStream.close();
                }
            }
        }
        if (workbook == null) {
            throw new RuntimeException("input file error");
        }
    }

    /**
     * 保存excel到指定文件
     *
     * @throws IOException
     */
    public void save(File file) throws IOException {
        FileOutputStream fs = new FileOutputStream(file);
        workbook.write(fs);
    }

    /**
     * 关闭excel
     *
     * @throws IOException
     */
    public void close() throws IOException {
        if (inputStream != null) {
            inputStream.close();
        }
        //workbook.close();//to do
    }

    /**
     * 指定 当前活动的Sheet （默认是0）
     *
     * @param sheetIndex
     *            Sheet 页，从 0 开始
     */
    public void setActiveSheet(int sheetIndex) {
        this.activeSheetIndex = sheetIndex;
        this.workbook.setActiveSheet(sheetIndex);
    }

    /**
     * 设定Sheet名
     *
     * @param sheetIndex
     *            Sheet 页，从 0 开始
     * @param sheetName
     *            Sheet名
     */
    public void setSheetName(int sheetIndex, String sheetName) {
        this.workbook.setSheetName(sheetIndex, sheetName);
    }

    /**
     *
     * 设置 cell 位置的单元格值
     *
     * @param cell
     *            单元格的位置，如：A1,B2
     * @param value
     *            值
     */
    public void setValueAt(String cell, Object value) {
        CellAddress address = new CellAddress(cell);
        setValueAt(address.getRow(), address.getCol(), value);
    }

    /**
     *
     * 设置row 和 column 位置的单元格值
     *
     * @param rowIndex
     *            指定行，从0开始
     * @param colIndex
     *            指定列，从0开始
     * @param value
     *            值
     */
    public void setValueAt(int rowIndex, int colIndex, Object value) {
        Sheet sheet = workbook.getSheetAt(this.activeSheetIndex);
        Cell cell = getOrCreateCell(sheet, rowIndex, colIndex);
        if (value == null) {
            cell.setCellValue("");
        } else {
            if (value instanceof Integer) {
                cell.setCellStyle(this.getFormatCellStyle("#,##0"));
                cell.setCellValue((Integer) value);

            } else if (value instanceof Long) {
                cell.setCellStyle(this.getFormatCellStyle("#,##0"));
                cell.setCellValue((Long) value);
            } else if (value instanceof Float) {
                cell.setCellStyle(this.getFormatCellStyle("#,##0.00"));
                cell.setCellValue((Float) value);
            } else if (value instanceof Double) {
                cell.setCellStyle(this.getFormatCellStyle("#,##0.00"));
                cell.setCellValue((Double) value);
            } else if (value instanceof Boolean) {
                cell.setCellValue((Boolean) value);
            } else if (value instanceof Date) {
                cell.setCellStyle(this.getFormatCellStyle("yyyy/mm/dd"));
                cell.setCellValue((Date) value);
            } else {
                // 其他的，直接输出为字符串
                cell.setCellValue(value.toString());
            }
        }
    }

    /**
     * 设定指定单元格的公式
     *
     * @param cell
     * @param formula
     */
    public void setFormulaAt(String cell, String formula) {
        CellAddress address = new CellAddress(cell);
        setFormulaAt(address.getRow(), address.getCol(), formula);
    }

    /**
     * 设定指定单元格的公式
     *
     * @param rowIndex
     * @param colIndex
     * @param formula
     */
    public void setFormulaAt(int rowIndex, int colIndex, String formula) {
        Sheet sheet = workbook.getSheetAt(this.activeSheetIndex);
        Cell cell = getOrCreateCell(sheet, rowIndex, colIndex);
        cell.setCellType(Cell.CELL_TYPE_FORMULA);
        cell.setCellFormula(formula);
    }

    /**
     * 取得指定位置的Cell（没有就创建新的Cell）
     *
     * @param sheet
     * @param rowIndex
     * @param colIndex
     * @return
     */
    private Cell getOrCreateCell(Sheet sheet, int rowIndex, int colIndex) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            cell = row.createCell(colIndex);
        }
        return cell;
    }

    /**
     *
     * 返回 cell 位置的单元格值(String类型)
     *
     * @param cell
     *            单元格的位置，如：A1,B2
     * @return
     *
     */
    public String getValueAt(String cell) {
        if (cell == null || "".equals(cell)) {
            return "";
        }
        CellAddress address = new CellAddress(cell);
        return this.getValueAt(address.getRow(), address.getCol());
    }

    /**
     *
     * 返回 row 和 column 位置的单元格值
     *
     * @param rowIndex
     *            指定行，从0开始
     * @param colIndex
     *            指定列，从0开始
     * @return
     *
     */
    public String getValueAt(int rowIndex, int colIndex) {
        Sheet sheet = workbook.getSheetAt(this.activeSheetIndex);
        Row row = sheet.getRow(rowIndex);
        if (row != null) {
            return getCellValueToString(row.getCell(colIndex));
        }
        return "";
    }

    /**
     *
     * 转换单元格的类型为String 默认的 <br>
     * 默认的数据类型：CELL_TYPE_BLANK(3), CELL_TYPE_BOOLEAN(4),
     * CELL_TYPE_ERROR(5),CELL_TYPE_FORMULA(2), CELL_TYPE_NUMERIC(0),
     * CELL_TYPE_STRING(1)
     *
     * @param cell
     * @return
     *
     */
    private String getCellValueToString(Cell cell) {
        String strCell = "";
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BOOLEAN:
                strCell = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
                    strCell = sdf.format(cell.getDateCellValue());
                } else {
                    strCell = String.valueOf(cell.getNumericCellValue());
                }
                break;
            case Cell.CELL_TYPE_STRING:
                strCell = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_FORMULA:
                strCell = String.valueOf(formulaEvaluator.evaluate(cell).getNumberValue());
                break;
            default:
                break;
        }
        return strCell;
    }

    /**
     * 获取当前操作的excel的workbook
     * 注意：
     *   一般的简单操作都可以通过ExcelUtil来完成。
     *   一些复杂的特殊的要求，ExcelUtil暂时没有提供，故开放workbook接口，
     *   由外部调用来自己操作poi完成。
     * @return Workbook
     */
    public Workbook getWorkbook() {
        return this.workbook;
    }

    private void insertRow(int starRow, int rows) {
        Sheet sheet = this.workbook.getSheetAt(this.activeSheetIndex);
        sheet.shiftRows(starRow + 1, sheet.getLastRowNum(), rows, true, false);
        // Parameters:
        // startRow - the row to start shifting
        // endRow - the row to end shifting
        // n - the number of rows to shift
        // copyRowHeight - whether to copy the row height during the shift
        // resetOriginalRowHeight - whether to set the original row's height to the
        // default
    }

    /**
     * 拷贝行，并将拷贝的行插入到拷贝行的下面
     *
     * @param startRow
     *            拷贝开始行 从0开始
     * @param endRow
     *            拷贝结束行 从0开始
     */
    public void copyAndInsertRows(int startRow, int endRow) {
        int rowCount = endRow - startRow + 1;
        this.insertRow(endRow, rowCount);
        this.copyRows(startRow, endRow, startRow + rowCount);
    }

    /**
     * 拷贝行，并将拷贝的行粘贴到指定的位置
     *
     * @param startRow
     *            开始行 从0开始
     * @param endRow
     *            结束行 从0开始
     * @param pPosition
     *            粘贴目标行的起始位置 从0开始
     */
    public void copyRows(int startRow, int endRow, int pPosition) {
        Sheet sheet = this.workbook.getSheetAt(this.activeSheetIndex);
        int pStartRow = startRow;
        int pEndRow = endRow;
        int targetRowFrom;
        int targetRowTo;
        int columnCount;
        CellRangeAddress region = null;
        int i;
        int j;
        if (pStartRow == -1 || pEndRow == -1) {
            return;
        }
        // 拷贝合并的单元格
        for (i = 0; i < sheet.getNumMergedRegions(); i++) {
            region = sheet.getMergedRegion(i);
            if ((region.getFirstRow() >= pStartRow) && (region.getLastRow() <= pEndRow)) {
                targetRowFrom = region.getFirstRow() - pStartRow + pPosition;
                targetRowTo = region.getLastRow() - pStartRow + pPosition;
                CellRangeAddress newRegion = region.copy();
                newRegion.setFirstRow(targetRowFrom);
                newRegion.setFirstColumn(region.getFirstColumn());
                newRegion.setLastRow(targetRowTo);
                newRegion.setLastColumn(region.getLastColumn());
                sheet.addMergedRegion(newRegion);
            }
        }
        // 设置列宽
        for (i = pStartRow; i <= pEndRow; i++) {
            Row sourceRow = sheet.getRow(i);
            columnCount = sourceRow.getLastCellNum();
            if (sourceRow != null) {
                Row newRow = sheet.createRow(pPosition - pStartRow + i);
                newRow.setHeight(sourceRow.getHeight());
                for (j = 0; j < columnCount; j++) {
                    Cell templateCell = sourceRow.getCell(j);
                    if (templateCell != null) {
                        Cell newCell = newRow.createCell(j);
                        copyCell(templateCell, newCell);
                    }
                }
            }
        }
    }

    private void copyCell(Cell srcCell, Cell distCell) {
        distCell.setCellStyle(srcCell.getCellStyle());
        if (srcCell.getCellComment() != null) {
            distCell.setCellComment(srcCell.getCellComment());
        }
        int srcCellType = srcCell.getCellType();
        distCell.setCellType(srcCellType);
        if (srcCellType == Cell.CELL_TYPE_NUMERIC) {
            if (DateUtil.isCellDateFormatted(srcCell)) {
                distCell.setCellValue(srcCell.getDateCellValue());
            } else {
                distCell.setCellValue(srcCell.getNumericCellValue());
            }
        } else if (srcCellType == Cell.CELL_TYPE_STRING) {
            distCell.setCellValue(srcCell.getRichStringCellValue());
        } else if (srcCellType == Cell.CELL_TYPE_BLANK) {
            // nothing21
        } else if (srcCellType == Cell.CELL_TYPE_BOOLEAN) {
            distCell.setCellValue(srcCell.getBooleanCellValue());
        } else if (srcCellType == Cell.CELL_TYPE_ERROR) {
            distCell.setCellErrorValue(srcCell.getErrorCellValue());
        } else if (srcCellType == Cell.CELL_TYPE_FORMULA) {
            distCell.setCellFormula(srcCell.getCellFormula());
        } else { // nothing29

        }
    }

    /**
     * Common conversion functions between Excel style A1, C27 style cell
     * references, and POI usermodel style row=0, column=0 style references. eg "A1"
     * and "BA2"
     */
    public static final class CellAddress {

        private int _rowIndex;
        private int _colIndex;

        /**
         * @param cellAddr
         *            Excel style A1, C27
         */
        public CellAddress(String cellAddr) {
            String strColumn = "";
            String strRow = "";
            for (int i = 0; i < cellAddr.length(); i++) {
                char c = cellAddr.charAt(i);
                if (!Character.isDigit(c)) {
                    strColumn += c;
                } else {
                    strRow += c;
                }
            }
            this._rowIndex = Integer.parseInt(strRow) - 1;
            this._colIndex = convertColStringToIndex(strColumn);
        }

        /**
         * @param rowIndex
         *            0-based rowIndex
         * @param colIndex
         *            0-based colIndex
         */
        public CellAddress(int rowIndex, int colIndex) {
            this._rowIndex = rowIndex;
            this._colIndex = colIndex;
        }

        /**
         * @return 0-based rowIndex
         */
        public int getRow() {
            return this._rowIndex;
        }

        /**
         * @return 0-based colIndex
         */
        public int getCol() {
            return this._colIndex;
        }

        /**
         * @return Excel style A1, C27
         */
        public String formatAsString() {
            StringBuffer sb = new StringBuffer();
            sb.append(convertNumToColString(this._colIndex));
            sb.append((this._rowIndex + 1));
            return sb.toString();
        }

        /**
         * Converts an Excel column name like "C" to a zero-based index.
         *
         * @param name
         * @return Index corresponding to the specified name
         */
        private static int convertColStringToIndex(String name) {
            int column = 0;
            for (int i = 0; i < name.length(); ++i) {
                int c = name.charAt(i);
                column = (column * 26) + (c - 'A' + 1);
            }
            return column - 1;
        }

        /**
         * Takes in a 0-based base-10 column and returns a ALPHA-26 representation. eg
         * column #3 -> D
         */
        public static String convertNumToColString(int col) {
            // Excel counts column Quadrangle as the 1st column, we
            // treat it as the 0th one
            int excelColNum = col + 1;

            StringBuilder colRef = new StringBuilder(2);
            int colRemain = excelColNum;

            while (colRemain > 0) {
                int thisPart = colRemain % 26;
                if (thisPart == 0) {
                    thisPart = 26;
                }
                colRemain = (colRemain - thisPart) / 26;

                // The letter Quadrangle is at 65
                char colChar = (char) (thisPart + 64);
                colRef.insert(0, colChar);
            }

            return colRef.toString();
        }
    }
}
package com.wootion.utiles.poi;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XlsxExportSvcImpl implements ExportSvc {
    private XSSFWorkbook workbook;// 工作簿
    private Map<Integer,XSSFSheet> sheetMap; // sheet页
    private Map<Integer,Integer> rowIdxMap; // sheet也对应的当前操作行
    private XSSFCellStyle style; // excel cell 表头单元格样式
    private XSSFCellStyle dataStyle; // excel 数据单元格样式
    private XSSFCellStyle picStyle; // 图片单元格式
    private String title;

    //以条件控制输出行的颜色
    private RowStyle rowStyles[];
    public void setRowStyles(RowStyle[] rowStyles) {
        this.rowStyles=rowStyles;
    }

    public XlsxExportSvcImpl(String title) {
        this.title = title;
        initworkBook();
    }
    private void initworkBook() {
        workbook = new XSSFWorkbook();
// 表头单元格样式
        style = workbook.createCellStyle();

        style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        style.setBorderRight(XSSFCellStyle.BORDER_THIN);
        style.setBorderTop(XSSFCellStyle.BORDER_THIN);
        style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        Font font = workbook.createFont();
        font.setColor(IndexedColors.BLACK.index);
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
        style.setFont(font);
// 数据单元格样式
        dataStyle = workbook.createCellStyle();
        dataStyle.setFillForegroundColor(IndexedColors.WHITE.index);
        dataStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        dataStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        dataStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        dataStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
        dataStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
        dataStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        dataStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        dataStyle.setWrapText(true);
        Font dataFont = workbook.createFont();
        dataFont.setColor(IndexedColors.BLACK.index);
        dataFont.setFontHeightInPoints((short) 10);
        dataFont.setBoldweight(XSSFFont.BOLDWEIGHT_NORMAL);
// 把字体应用到当前的样式
        dataStyle.setFont(dataFont);
        sheetMap = new HashMap ();
        rowIdxMap = new HashMap ();
    }
    /**
     * 获取sheet对象进行读写操作
     *
     * @param sheetIndex
     * @return
     */
    private XSSFSheet getSheet(Integer sheetIndex) {
        XSSFSheet sheet = sheetMap.get(sheetIndex);
        if (sheet == null) {
            sheet = workbook.createSheet(title +"#"+ sheetIndex);
            sheet.setDefaultColumnWidth(15);
            sheetMap.put(sheetIndex, sheet);
            rowIdxMap.put(sheetIndex, 0);
        }
        return sheet;
    }


    /**
     *
     * @param color  #FF00DD
     * @return
     */
    private XSSFCellStyle createStyle(String color){
        int r=Integer.valueOf(color.substring(1,3),16);
        int g=Integer.valueOf(color.substring(3,5),16);
        int b=Integer.valueOf(color.substring(5,7),16);
        XSSFColor xssfColor = new XSSFColor(new java.awt.Color(r,g, b));

        XSSFCellStyle dataStyle;
        dataStyle = workbook.createCellStyle();
        dataStyle.setFillForegroundColor(IndexedColors.WHITE.index);
        dataStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        dataStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        dataStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        dataStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
        dataStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
        dataStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        dataStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        dataStyle.setWrapText(true);

        XSSFFont dataFont = workbook.createFont();
        dataFont.setColor(xssfColor);
        dataFont.setFontHeightInPoints((short) 10);
        dataFont.setBoldweight(XSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        dataStyle.setFont(dataFont);
        return dataStyle;
    }

    @Override
    public void writeMapList(List<Map> list,String[] keys, Integer sheetIndex) {
        writeMapList(list, keys, sheetIndex, 0);
    }

    @Override
    public void writeMapList(List<Map> list,String[] keys, Integer sheetIndex, int height) {
        for (int i = 0; i < list.size(); i++) {
            //对行的颜色处理
            String color=null;
            if(rowStyles!=null){
                for(int c=0;c<rowStyles.length;c++){
                    //首次命中为准
                    RowStyle rowStyle=rowStyles[c];
                    Object colValue = list.get(i).get(rowStyle.col);
                    if(colValue!=null){
                        if (Double.class.isAssignableFrom(colValue.getClass())) {
                            Double dValue=(Double)colValue;
                            Double conditionValue=Double.valueOf(rowStyle.value);
                            if(rowStyle.compare.equals("==") ){
                                if(Math.abs(dValue-conditionValue)<0.00001 ){
                                    color=rowStyle.color;
                                    break;
                                }
                            }
                        } else {
                            String strValue = String.valueOf(colValue);
                            if(rowStyle.compare.equals("==") ){
                                if(strValue.equals(rowStyle.value)){
                                    color=rowStyle.color;
                                    break;
                                }
                            }
                        }
                    }

                }
            }
            writeMapData(list.get(i),keys, sheetIndex,color, height);
        }
    }



    /**
     *
     * @param rowData
     * @param keys
     * @param sheetIndex
     * @param color  #FF00FF 指定行的颜色，为空时 用默认颜色
     *
     */
    private void writeMapData(Map rowData,String[] keys, Integer sheetIndex,String color, int height) {
        XSSFSheet sheet = getSheet(sheetIndex);
        Integer rowindex = rowIdxMap.get(sheetIndex);
        XSSFRow row = sheet.createRow(rowindex);
        if (height>0) {
            row.setHeight((short) (height*20));
        }
        CellStyle cellStyle=null;
        if(color!=null){
            cellStyle= createStyle(color);
        }

        for (int i = 0; i < keys.length; i++) {
            XSSFCell cell = row.createCell(i);
            if(cellStyle==null){
                cell.setCellStyle(dataStyle);
            }else{
                row.setRowStyle(cellStyle);//特殊颜色
            }
            Object value=rowData.get(keys[i]);
            if(value!=null){
                if (Double.class.isAssignableFrom(value.getClass())) {
                    cell.setCellValue((Double) value);
                } else {
                    String str = String.valueOf(value);
                    cell.setCellValue(str);
                }
            }
        }
        rowIdxMap.put(sheetIndex, rowindex + 1);
    }


    /**
     * 暂时不用多线程处理0--后期如有必要 添加
     */
    @Override
    public void writeData(List dataSet, Integer sheetIndex) {
        for (int i = 0; i < dataSet.size(); i++) {
            writeData((Object[])dataSet.get(i), sheetIndex);
        }
    }
    @Override
    public void writeData(Object[] objs, Integer sheetIndex) {
        XSSFSheet sheet = getSheet(sheetIndex);
        Integer rowindex = rowIdxMap.get(sheetIndex);
        XSSFRow row = sheet.createRow(rowindex);
// 表头处理
        for (int i = 0; i < objs.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellStyle(dataStyle);
            if (Double.class.isAssignableFrom(objs[i].getClass())) {
                cell.setCellValue((Double) objs[i]);
            } else {
                String str = String.valueOf(objs[i]);
                cell.setCellValue(str);
            }
        }
        rowIdxMap.put(sheetIndex, rowindex + 1);
    }

    @Override
    public void writeTblHead(List [] heads, Integer sheetIndex) {
        int height=25;
        writeTblHead(heads, sheetIndex, height);
    }

    @Override
    public void writeTblHead(List [] heads, Integer sheetIndex, int height) {
// 创建sheet
        XSSFSheet sheet = getSheet(sheetIndex);
// 创建表头行
        List<XSSFRow> rows = new ArrayList ();
        for (int i = 0; i < heads.length; i++) {
            rows.add(sheet.createRow(i));
        }
// 创建单元格并进行合合并同类项
        for (int i = 0; i < heads.length; i++) {
            List list = heads[i];
            int colindex = 0;
            for (int j = 0; j < list.size(); j++) {
                XSSFRow rowi = rows.get(i);
                rowi.setHeight((short) (height*20));
                XSSFCell cl = rowi.getCell(colindex);
                while (cl != null) {
                    cl = rowi.getCell(++colindex);
                }
                TblCell tblCell = (TblCell)list.get(j);
                int colSpan = tblCell.getColspan();
                int rowSpan = tblCell.getRowspan();
                for (int x = 0; x < colSpan; x++) {
                    for (int y = 0; y < rowSpan; y++) {
                        int cellx = colindex + x;
                        int celly = i + y;
                        XSSFRow row = rows.get(celly);
                        XSSFCell cell = row.createCell(cellx);
                        cell.setCellStyle(style);
                        cell.setCellValue(tblCell.getAlias());
                    }
                }
// 合并单元格
                sheet.addMergedRegion(new CellRangeAddress(i, i + rowSpan - 1, colindex, colindex + colSpan - 1));
            }
// 设置sheet页 行 索引
            rowIdxMap.put(sheetIndex, heads.length);
        }
    }




    public XSSFRow writeRow(int rowPos,Object []rowData, int[] colSpans,Integer sheetIndex){
        int height=25;
        return writeRow(rowPos,rowData, colSpans, sheetIndex, height);
    }

    /**
     * 直接定位到行的输出显示 不能多行,列位置从0开始
     *
     * @param rowPos
     * @param rowData
     * @param colSpans 每列数据占用列数
     * @param sheetIndex
     * @return
     */
    public XSSFRow writeRow(int rowPos,Object []rowData, int[] colSpans,Integer sheetIndex,int height){
        if(rowData.length!=colSpans.length){
            System.out.println("数据和列宽度 数组长度不一致");
            return null;
        }
        XSSFSheet sheet = getSheet(sheetIndex);

        int colPos=0;
        XSSFRow row=sheet.createRow(rowPos);
        row.setHeight((short) (height*20));

        for (int i=0;i<rowData.length;i++){
            XSSFCell cell = row.createCell((short) colPos);
            cell.setCellType(XSSFCell.CELL_TYPE_STRING);
            cell.setCellValue((String)rowData[i]);
            //cell.setCellStyle(this.createCellStyleCenter());
            cell.setCellStyle(dataStyle);
            if(colSpans[i]>1){
                //cell.setCellStyle(creat);
                for (int j=1;j<colSpans[i];j++){
                    cell = row.createCell((short) (colPos+j));
                    cell.setCellValue("");
                    cell.setCellStyle(dataStyle);
                }
            }
            colPos=colPos+colSpans[i];
        }

        colPos=0;
        for (int i=0;i<rowData.length;i++){
            // 指定合并区域
            if(colSpans[i]>1){
                CellRangeAddress cellRangeAddress= new CellRangeAddress(rowPos,rowPos,colPos,colPos+colSpans[i]-1);
                sheet.addMergedRegion(cellRangeAddress);
            }
            colPos += colSpans[i];
        }

        return row;
    }

    @Override
    public void writeFile(File file) {
        FileOutputStream fileout = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fileout = new FileOutputStream(file);
            workbook.write(fileout);
        } catch (Exception e) {
            throw new RuntimeException("xls写入错误。。。", e);
        } finally {
            if (fileout != null) {
                try {
                    fileout.close();
                } catch (IOException e) {
                    throw new RuntimeException("xls写入错误。。。", e);
                }
            }
        }
    }



    public static int export(){
        List [] heads = new List[2];
        List list1 = new ArrayList ();
        List list2 = new ArrayList ();
        list1.add(new TblCell("21","维度1", 1, 2));
        list1.add(new TblCell("12","父指标", 2, 1));
        list2.add(new TblCell("11","指标1", 1, 1));
        list2.add(new TblCell("11","指标2", 1, 1));
        list2.add(new TblCell("11","指标3", 1, 1));
        list2.add(new TblCell("11","指标4", 1, 1));
        heads[0] = list1;
        heads[1] = list2;
        ExportSvc exportSvc = new XlsxExportSvcImpl("测试");
        File file = new File("/home/zhoufei/temp/333.xlsx");
        exportSvc.writeTblHead(heads, 1);
        Object[] objs = new Object[5];
        objs[0] ="合肥";
        objs[1] = 0.01;
        objs[2] = 100;
        objs[3] = "中文中文测试";
        objs[4] = "中文中文测试说不听";
        Object[] obj1 = new Object[5];
        obj1[0] ="合肥";
        obj1[1] = 0.01;
        obj1[2] = 100;
        obj1[3] = "中文中文测试";
        obj1[4] = "中文中文测试说不听";
        List list = new ArrayList();
        list.add(objs);
        list.add(obj1);
        exportSvc.writeData(list, 1);
        exportSvc.writeFile(file);
        return 0;
    }

    public int export2(){


        XSSFSheet sheet = getSheet(1);

        int colPos=0;
        XSSFRow row=sheet.createRow(0);

        XSSFCell cell = row.createCell(0);
        cell.setCellType(XSSFCell.CELL_TYPE_STRING);
        cell.setCellValue("abc");
        cell.setCellStyle(dataStyle);

        XSSFCell cell2 = row.createCell(1);
        cell2.setCellType(XSSFCell.CELL_TYPE_STRING);
        cell2.setCellValue("abc");
        cell2.setCellStyle(dataStyle);

        CellRangeAddress cellRangeAddress= new CellRangeAddress(0,0,0,1);
        sheet.addMergedRegion(cellRangeAddress);


        return 0;
    }


    public int exportPic(String fileName,int row1,int col1,int row2,int col2){
        return exportPic(fileName, row1, col1, row2, col2,0);
    }
    /**
     * 导出一个图片.
     * width 为字符个数，宽度对sheet整个有效。
     * @return
     */
    public int exportPic(String fileName,int row1,int col1,int row2,int col2,int width){
        try{
            XSSFSheet sheet = getSheet(1);
            if(width!=0){
                sheet.setColumnWidth(col1,width*256);
            }

            FileInputStream fis = new FileInputStream(fileName);
            byte[] bytes = IOUtils.toByteArray(fis);
            int pictureIdx = workbook.addPicture(bytes, workbook.PICTURE_TYPE_JPEG);
            fis.close();

            Drawing drawing = sheet.createDrawingPatriarch();
            CreationHelper helper = workbook.getCreationHelper();
            ClientAnchor anchor = helper.createClientAnchor();
            anchor.setCol1(col1);
            anchor.setRow1(row1);
            anchor.setCol2(col2);
            anchor.setRow2(row2);
            Picture pict = drawing.createPicture(anchor, pictureIdx);
            //auto-size picture relative to its top-left corner
            //pict.resize();//该方法只支持JPEG 和 PNG后缀文件

        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
        return 0;

    }

   /* private CellStyle createRegionBorderStyle(){
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        return cellStyle;
    }*/

    @Override
    public void setRegionStyle(int sheetIndex) {
        Sheet sheet=workbook.getSheetAt(sheetIndex);
        for(int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress merge = sheet.getMergedRegion(i);
            System.out.println("Number: " + i);
            System.out.println("first column: " + merge.getFirstColumn() + " last column " + merge.getLastColumn());
            System.out.println("first row: " + merge.getFirstRow() + " last row " + merge.getLastRow());
            System.out.println("==============");
            System.out.println();
            for (int j= merge.getFirstRow(); j <= merge.getLastRow(); j ++) {
                Row row=sheet.getRow(j);
                for (int k = merge.getFirstColumn(); k <= merge.getLastColumn(); k++) {
                    Cell cell = row.getCell(k);
                    if(cell!=null){
                        cell.setCellStyle(this.dataStyle);
                    }
                }
            }
        }


    }



    public static void main(String[] args) {
        XlsxExportSvcImpl exportSvc = new XlsxExportSvcImpl("测试");
        File file = new File("/home/zhoufei/web/report/666.xlsx");
        exportSvc.export2();
        exportSvc.exportPic("/home/zhoufei/web/report/taskRoute.png",5,1,20,6);
        exportSvc.writeFile(file);

    }
}
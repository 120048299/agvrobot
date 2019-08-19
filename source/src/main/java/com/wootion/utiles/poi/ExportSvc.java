package com.wootion.utiles.poi;


import org.apache.poi.xssf.usermodel.XSSFRow;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface ExportSvc {
    void setRowStyles(RowStyle[] rowStyles);
    void writeData(List dataSet, Integer index);
    void writeData(Object[] t, Integer index);
    void writeMapList(List<Map> list, String[] keys, Integer index);
    void writeMapList(List<Map> list, String[] keys, Integer index, int height);

    void writeTblHead(List[] heads, Integer index);
    void writeTblHead(List [] heads, Integer index, int height);
    XSSFRow writeRow(int rowPos, Object []rowData, int[] colSpans, Integer sheetIndex);
    XSSFRow writeRow(int rowPos,Object []rowData, int[] colSpans,Integer sheetIndex,int height);
    void setRegionStyle(int sheetIndex);
    void writeFile(File file);
    int exportPic(String fileName,int row1,int col1,int row2,int col2);
    int exportPic(String fileName,int row1,int col1,int row2,int col2,int width);

}
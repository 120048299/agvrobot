package com.wootion.utiles.poi;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;

public class Util {
    public static void main(String args[]){
        writeExcel("d:/temp/a.xls");
    }

    public static void writeExcel(String outputFile){

        //1. 创建新的Excel工作薄
        XSSFWorkbook workbook = new XSSFWorkbook();

        // 在Excel工作簿中建一工作表，其名为缺省值。POI中还提供了其他的一些workbook 构造方法。

        //2.创建一个工作表。新建一名为"工资表"的工作表：　　


        XSSFSheet sheet = workbook.createSheet("工资表");

        //3.创建行。在索引0的位置创建行（最顶端的行）：　　

        XSSFRow row = sheet.createRow(0);

        //4.创建单元格。在索引0的位置创建单元格（左上端）：　　

        XSSFCell cell = row.createCell(0);

        // 定义单元格为字符串类型（也可在创建单元格里面设置）：

        cell.setCellType(HSSFCell.CELL_TYPE_STRING);

        //在单元格中输入一些内容：　

        cell.setCellValue("增加值");

        //5.新建一输出文件流，把相应的Excel工作簿输出到本地
        try {
            FileOutputStream fOut = new FileOutputStream(outputFile);

            workbook.write(fOut);

            fOut.flush();

            //操作结束，关闭文件

            fOut.close();
        }catch (Exception e){
            e.printStackTrace();
        }


    }

}

package com.wootion.utiles.poi;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExportExcel {

    /**
     * 输出excel文件：单行表头,带行颜色设置。
     * @param fileName
     * @param title sheet名称
     * @param heads
     * @param data
     * @return
     */
    public static int export(String fileName,String title,String[] heads,String[] fields,List<Map> data,RowStyle rowStyles[]){
        int ret=0;
        ExportSvc exportSvc = new XlsxExportSvcImpl(title);
        exportSvc.setRowStyles(rowStyles);
        File file = new File(fileName);
        List [] headList = new List[1];
        List list1 = new ArrayList();
        for(String head : heads){
            list1.add(new TblCell("11",head, 1, 1));
        }
        headList[0] = list1;
        exportSvc.writeTblHead(headList, 1);
        exportSvc.writeMapList(data,fields,1);
        exportSvc.writeFile(file);
        return 0;
    }
    /**
     * 输出excel文件：单行表头
     * @param fileName
     * @param title sheet名称
     * @param heads
     * @param data
     * @return
     */
    public static int export(String fileName,String title,String[] heads,String[] fields,List<Map> data){
        int ret=0;
        ExportSvc exportSvc = new XlsxExportSvcImpl(title);
        File file = new File(fileName);

        List [] headList = new List[1];
        List list1 = new ArrayList();
        for(String head : heads){
            list1.add(new TblCell("11",head, 1, 1));
        }
        headList[0] = list1;
        exportSvc.writeTblHead(headList, 1);
        exportSvc.writeMapList(data,fields,1);
        exportSvc.writeFile(file);
        return 0;
    }

    public static void test(){
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

    }
    public static void main(String[] args) {
        String fileName="/home/zhoufei/temp/222.xlsx";
        String keys[]={"id","name"};
        String heads[]= {"序号","名称"};
        List<Map> list=new ArrayList<>();
        Map map=new HashMap();
        map.put("id","1");
        map.put("name","zhangsan");
        list.add(map);

        map=new HashMap();
        map.put("id","2");
        map.put("name","李四");
        list.add(map);

        export(fileName,"sheet名称",heads,keys,list);
    }
}


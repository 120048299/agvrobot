package com.wootion.agvrobot.utils;

import java.util.List;
import java.util.Map;

public class ListUtil {


    /**
     * 合并追加
     * @param listAll
     * @param list
     * @return
     */
    public static  List<Map> addListItem(List<Map> listAll, List<Map> list) {
        if(listAll==null ){
            return null;
        }
        if (list == null || list.size() == 0) {
            return listAll;
        }
        for (Map item : list) {
            boolean found=false;
            for (Map data : listAll) {
                if (data.get("uid").equals(item.get("uid"))) {
                    found=true;
                }
            }
            if(!found) {
                listAll.add(item);
            }
        }
        return listAll;
    }


}

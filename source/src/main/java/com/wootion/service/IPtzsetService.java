package com.wootion.service;


import com.github.pagehelper.PageInfo;
import com.wootion.commons.Result;

import java.util.List;
import java.util.Map;

public interface IPtzsetService {
    Result addPtzSet(String siteId, String robotId,String areaId,String description, int status, int orderNum);
    int updatePtzSet(String uid,String siteId,String robotId,String areaId,String description,int status,int orderNum);

    List<Map> getPtzListForTree(Map params);
    List<Map> queryPtzList(Map params);
    int deletePtzSet(List<String> ptzsetIds);
    int setPtzSetStatus(List<String> ptzsetIds,int status);
}

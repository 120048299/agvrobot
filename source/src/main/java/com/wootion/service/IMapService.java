package com.wootion.service;

import com.wootion.commons.Result;
import com.wootion.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface IMapService {


    /**
     * 获取所有坐标点
     *
     * @return the mark list
     */
    List<RunMark> getRunMarkListBySiteId(String siteId);

    boolean addMapRunLine(ArrayList<String> markIdList, String siteId);
    boolean addMapRunLine(String mark_id1, String mark_id2, String siteId);
    int deleteMapRunLine(List<Map> lineList, String siteId);
    boolean deleteMapRunLine(String mark_id1, String mark_id2, String siteId);
    int batchAppRunMark(String mark_id1, String mark_id2, int n, int ptType, String siteId);
    int deleteMapRunMark(List<String> idList);
    int addMaintainArea(MaintainArea maintainArea,String robotId);
    int addChargeRoom(ChargeRoom chargeRoom);

    List<Map> getMaintainOrobstacle(String siteId);
    List<Map> getChargeRoom(String siteId);

    Result sendReloadCommandToNav(String robotId);
    int deleteMaintainOrobstacle(String uid,String robotId);
    boolean checkMaintainName(String name);
    boolean checkChargeRoomName(String name);
    void alignRunMarks( String siteId,ArrayList<String> runMarkIdList, int type);
    Result addRunMark(String siteId, double x, double y, String name,int ptzType,float robotAngle,int moveStyle);
    int deleteChargeRoom(String uid);
}

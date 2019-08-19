package com.wootion.service;

import com.wootion.agvrobot.session.SessionRobot;
import com.wootion.model.Dev;
import com.wootion.model.DevType;

import java.util.List;
import java.util.Map;

public interface DevService {
//    List<Site> findAllSite();

    Dev getDev(String devId) ;

    Dev getDevByCode(String code) ;

    /**
     * 获取所有SesssionRobot
     * @return
     */
    List<SessionRobot> findAllSessionRobot(String siteId);


    /**
     * 通过机器人Ip获取SessionRobot
     * @param robotId
     * @return
     */
    SessionRobot findSessionRobot(String siteId,String robotId);

//    List<Robot>findAllRobot(String siteId);
//
//    Robot findRobot(String robotId);

    //List<Map> selectSmallDev(Map params);

    //String saveOrUpdate(Map params);

    Dev addDev (Dev dev);
    int updateDev(Dev dev);
    int deleteDev(String uid);


}

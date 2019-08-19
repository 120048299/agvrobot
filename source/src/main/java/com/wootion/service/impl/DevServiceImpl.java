package com.wootion.service.impl;

import com.wootion.agvrobot.session.SessionRobot;
import com.wootion.dao.IDao;
import com.wootion.mapper.*;
import com.wootion.model.Dev;
import com.wootion.model.Robot;
import com.wootion.service.DevService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class DevServiceImpl implements DevService {
    @Autowired
    private IDao iDao;

    @Autowired
    private RobotMapper robotMapper;
    @Autowired
    private TaskPtzMapper taskPtzMapper;
    @Autowired
    private PtzSetMapper ptzSetMapper;
    @Autowired
    private DevMapper devMapper;
    @Autowired
    TaskLogMapper taskLogMapper;
    @Override
    public Dev getDev(String devId) {
        Dev dev = devMapper.select(devId);
        return dev;
    }

    @Override
    public Dev getDevByCode(String code) {
        Dev dev = devMapper.getDevByCode(code);
        return dev;
    }

    @Override
    public List<SessionRobot> findAllSessionRobot(String siteId) {
        List<SessionRobot> sessionRobotList = new ArrayList<>();
        List<Robot> Robots = robotMapper.findListBySiteId(siteId);
        for (Robot robot : Robots) {
            SessionRobot sessionRobot = new SessionRobot();
            sessionRobot.setSiteId(siteId);
            sessionRobot.setUid(robot.getUid());
            sessionRobot.setRobot(robot);
            sessionRobotList.add(sessionRobot);
        }
        return sessionRobotList;
    }

    @Override
    public SessionRobot findSessionRobot(String siteId,String robotId) {
        List<SessionRobot> sessionRobotList = this.findAllSessionRobot(siteId);
        if (!CollectionUtils.isEmpty(sessionRobotList)) {
            for (SessionRobot sessionRobot : sessionRobotList) {
                if (sessionRobot.getUid().equals(robotId)) {
                    return sessionRobot;
                }
            }
        }
        return null;
    }

  /*  @Override
    public List<Dev> getAreaList(String siteId) {
        return  devMapper.selectAreaList(siteId);
    }
    @Override
    public List<Dev> getBayAreaList(String siteId) {
        return devMapper.selectBayAreaList(siteId);
    }*/

    /*@Override
    public List<Map> selectSmallDev(Map params){
        return  (List<Map>) iDao.selectList("Dev.selectSmallDev",params);
    }*/
/*

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String saveOrUpdate(Map params) {
        Dev dev = new Dev();
        String uid =  StringUtils.isEmpty(params.get("uid")) ? null : (String) params.get("uid");
        String name =  StringUtils.isEmpty(params.get("name")) ? null : (String) params.get("name");
        String parentId =  StringUtils.isEmpty(params.get("parentId")) ? null : (String) params.get("parentId");
        String devTypeId =  StringUtils.isEmpty(params.get("devTypeId")) ? null : (String) params.get("devTypeId");
        String devParams =  StringUtils.isEmpty(params.get("params")) ? null : (String) params.get("params");
        Integer status =  StringUtils.isEmpty(params.get("status")) ? null : (Integer) params.get("status");

        dev.setName(name);
        dev.setParentId(parentId);
        dev.setDevTypeId(devTypeId);
        dev.setParams(devParams);
        dev.setStatus(status);
        dev.setIsSystem(0);
        dev.setUid(uid);
        int ret = this.saveOrUpdate(dev);
        if(ret<0){
            return  null;
        }
        String newDevId = dev.getUid();
        return newDevId;
    }
*/


/*
    @Transactional
    public int addDev(Dev dev) {
        return iDao.insert("Dev.insertSelective",dev);
    }
*/
/*

    @Transactional(propagation = Propagation.REQUIRED)
    public int saveOrUpdate(Dev dev) {
        int ret=-1;
        if (StringUtils.isEmpty(dev.getUid())){
            ret = this.addDev(dev);
        }else {
            ret = this.updateDev(dev);
        }
        return ret;
    }*/

    @Override
    public Dev addDev (Dev dev){
        int ret;

        //读取最大的排序
        int orderNumber=getMaxOrder(dev.getParentId());
        orderNumber = orderNumber+1;
        dev.setOrderNumber(orderNumber);
        ret=devMapper.insert(dev);
        if(ret<=0){
            return null;
        }
        return dev;
    }

    /**
     * 查找最大排序值
     * @param parentId
     * @return
     */
    private int getMaxOrder(String parentId){
        int orderNumber=0;
        List<Dev> list =devMapper.selectDevByParentId(parentId);
        if (list!=null && list.size()!=0) {
            Dev lastDev = list.get(list.size() - 1);
            orderNumber = lastDev.getOrderNumber();
        }
        return orderNumber;
    }

    /**
     * 在删除一个dev之后 ，更新所有的排序值，把缺掉的补上
     * @param parentId
     * @return
     */
    public void reOrderAll(String parentId){
        List<Dev> list =devMapper.selectDevByParentId(parentId);
        if (list==null || list.size()==0){
            return ;
        }
        for (int i=0;i<list.size();i++){
            Dev dev=list.get(i);
            dev.setOrderNumber(i+1);
            devMapper.update(dev);
        }
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public int updateDev(Dev dev) {
        return devMapper.update(dev);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int deleteDev(String uid) {
        taskLogMapper.deleteByDevId(uid);
        devMapper.delete(uid);
        return 0;
    }

}

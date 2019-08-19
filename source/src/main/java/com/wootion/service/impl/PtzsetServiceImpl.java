package com.wootion.service.impl;

import com.wootion.Debug;
import com.wootion.commons.Result;
import com.wootion.dao.IDao;
import com.wootion.mapper.PtzSetMapper;
import com.wootion.mapper.RunMarkMapper;
import com.wootion.mapper.TaskLogMapper;
import com.wootion.model.PtzSet;
import com.wootion.model.RunMark;
import com.wootion.protocols.robot.msg.RobotInfo;
import com.wootion.robot.MemRobot;
import com.wootion.robot.MemUtil;
import com.wootion.service.IPtzsetService;
import com.wootion.utiles.ResultUtil;
import com.wootion.utiles.RunMarkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class PtzsetServiceImpl implements IPtzsetService {
    @Autowired
    IDao iDao;
    @Autowired
    RunMarkMapper runMarkMapper;
    @Autowired
    PtzSetMapper ptzSetMapper;
    @Autowired
    TaskLogMapper taskLogMapper;
    Logger logger = LoggerFactory.getLogger(PtzsetServiceImpl.class);

    @Override
    @Transactional
    public Result addPtzSet(String siteId, String robotId,String areaId,String description, int scan, int status){
        int ret=0;
        //检查名称应该不同
        List<PtzSet> ptzSetList=ptzSetMapper.selectListBySite(siteId);
        if(ptzSetList!=null && ptzSetList.size()>0){
            for(PtzSet ptzSet:ptzSetList){
                if(ptzSet.getDescription().equals(description)){
                    return ResultUtil.build(-2,"",null);
                }
            }
        }

        //读取机器人位置方向 和驱鸟云台方向
        float x=1;
        float y=1;
        float angle=1;
        int pan=1;//角度*100
        int tilt=1;//角度*100
        MemRobot memRobot= MemUtil.queryRobotById(robotId);
        if(memRobot==null){
            return ResultUtil.build(-3,"机器人不存在",null);
        }
      /*  if(!memRobot.isOnline()){
            return ResultUtil.build(-3,"机器人不在线",null);
        }*/
        Float[] position=memRobot.getRobotInfo().getPosition();
        if(position==null){
            return ResultUtil.build(-3,"机器人坐标异常",null);
        }
        x=position[0];
        y=position[1];
        angle=memRobot.getRobotInfo().getOrientation();

        //查询是否有接近的地标点，如果有则使用，如果没有新建地标点
        RunMark runMark=RunMarkUtil.findTooCloseRunMark(x,y,siteId,"");
        if(runMark==null){
            runMark = new RunMark();
            runMark.setLat(Double.valueOf(x));
            runMark.setLon(Double.valueOf(y));
            runMark.setMarkName(description);
            runMark.setSiteId(memRobot.getSiteId());
            runMark.setStatus(0);
            runMark.setMoveStyle(0);
            runMarkMapper.insert(runMark);
        }
        PtzSet ptzSet = new PtzSet();
        ptzSet.setSiteId(siteId);
        ptzSet.setPtzType(4);
        ptzSet.setRobotAngle(angle);
        ptzSet.setPtzPan(pan);
        ptzSet.setPtzTilt(tilt);
        ptzSet.setDescription(description);
        ptzSet.setScan(scan);
        ptzSet.setStatus(status);
        ptzSet.setSetted(0);
        ptzSet.setMarkId(runMark.getUid());
        ptzSet.setAreaId(areaId);
        ret=ptzSetMapper.insert(ptzSet);
        if(ret<=0){
            logger.error("add ptzset to db error");
            return ResultUtil.build(-1,"写入数据库失败",null);
        }
        return ResultUtil.build(1,"",ptzSet);
    }

    @Override
    @Transactional
    public int updatePtzSet(String uid,String siteId,String robotId,String areaId,String description,int scan,int status) {
        PtzSet ptzSet = ptzSetMapper.select(uid);
        if(ptzSet==null){
            return -2;
        }
        //检查同一设备下的巡检点名称应该不同
        List<PtzSet> ptzSetList=ptzSetMapper.selectListBySite(siteId);
        if(ptzSetList!=null && ptzSetList.size()>0){
            for(PtzSet item:ptzSetList){
                if(!item.getUid().equals(uid) && item.getDescription().equals(description)){
                    return -2;
                }
            }
        }
        ptzSet.setDescription(description);
        ptzSet.setScan(scan);
        ptzSet.setStatus(status);
        int ret = ptzSetMapper.update(ptzSet);
        if(ret<0){
            logger.error("modify ptzSet error");
        }
        return ret;
    }



    /**
     * 删除ptzset
     *
     * @param ptzsetIds
     * @return
     */
    @Override
    public int deletePtzSet(List<String> ptzsetIds) {
        for (String id : ptzsetIds) {
            PtzSet ptzSet = ptzSetMapper.select(id);
            if (ptzSet == null) {
                continue;
            }
            ptzSetMapper.delete(id);
        }
        return 0;
    }

    @Override
    public List<Map> getPtzListForTree(Map params) {
    /*    if (params.get("taskPlanId") == null) {
            return ptzSetMapper.selectPtzListForTree(params);
        } else {//按任务计划查
//            List<Map> map=(List<Map>) iDao.selectList("PtzSet.selectPtzListForTreeByTaskPlanId", params);
            String siteId = (String) params.get("siteId");
            String taskPlanId = (String) params.get("taskPlanId");
            String searchText = (String) params.get("searchText");
            List<Map> map = ptzSetMapper.selectPtzListForTreeByTaskPlanId(siteId,taskPlanId,searchText);
            //是否已经审核，显示在树树上
            if(map!=null || map.size()<=0){
                return map;
            }
            return map;
        }*/
        return null;

    }


    //todo wait
    @Override
    public List<Map> queryPtzList(Map params) {
       /* String siteId=(String)params.get("siteId");
        if ((params.get("taskId") == null || "".equals(params.get("taskId")))
                && (params.get("alarmLevel") == null || "".equals(params.get("alarmLevel")))
                && (params.get("extId") == null || "".equals(params.get("extId"))))  {
            return ptzSetMapper.selectPtzListForTree(params);
        } else if (params.get("taskId") != null && !"".equals(params.get("taskId"))) {
            String taskId=(String)params.get("taskId");
            return ptzSetMapper.selectPtzListForTreeByTaskId(siteId,taskId);
        } */
       return null;
    }

    @Override
    public int setPtzSetStatus(List<String> ptzsetIds,int status) {
        for (String id : ptzsetIds) {
            PtzSet ptzSet = ptzSetMapper.select(id);
            if (ptzSet == null) {
                continue;
            }
            ptzSet.setStatus(status);
            ptzSetMapper.update(ptzSet);
        }
        return 0;
    }
}
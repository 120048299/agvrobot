package com.wootion.robot;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wootion.commons.Result;
import com.wootion.dao.IDao;
import com.wootion.mapper.*;
import com.wootion.model.*;
import com.wootion.protocols.robot.GeneralService;
import com.wootion.protocols.robot.msg.*;
import com.wootion.service.ITaskService;
import com.wootion.task.event.SentDataSyncEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;


@Component
public class DataSynThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(DataSynThread.class);

    private static LinkedBlockingQueue<Object> DataSynEvent = new LinkedBlockingQueue<>();

    public static void addEvent(Object evt) {
        DataSynEvent.add(evt);
    }
    private boolean sync = false;

    public static Object takeEvent() {
        try {
            return DataSynEvent.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Autowired
    private SysParamMapper sysParamMapper;

    @Autowired
    private RobotParamMapper robotParamMapper;

    @Autowired
    IDao iDao;

    @Autowired
    TaskMapper taskMapper;

    @Autowired
    TaskPtzMapper taskPtzMapper;

    @Autowired
    TaskPeriodMapper taskPeriodMapper;
    @Autowired
    private RunMarkMapper runMarkMapper;
    @Autowired
    RunLineMapper runLineMapper;

    @Autowired
    MaintainAreaMapper maintainAreaMapper;

    @Autowired
    ChargeRoomMapper chargeRoomMapper;
    @Autowired
    ITaskService taskService;

    /**
     　　* @description:
     创建线程，检查机器人是否在线，在线以后同步sysparm,runmark,runline,robotparm,area等表消息
     　　* @author Marico
     　　*
     　　*/
    @Override
    public void run() {
        logger.info("DataSynThread started!");
//        RosBridgeClient client = null;

        while (true){
            try{
                Thread.sleep(100);
                Object evt = takeEvent();
                if(sync){
                    continue;
                }
                sync=true;
                if(evt instanceof SentDataSyncEvent){
                    SentDataSyncEvent event=(SentDataSyncEvent) evt;
                    String robotId = event.getRobotId();
                    MemRobot memRobot=MemUtil.queryRobotById(robotId);
                    if(memRobot==null){
                        return ;
                    }
                    boolean sentSentParamsDataResult = taskService.syncParamsData(memRobot);
                    if(!sentSentParamsDataResult){
                        Thread.sleep(1000);
                        taskService.syncParamsData(memRobot);
                    }
                    Thread.sleep(1000);
                    boolean sentMapsDataResult = taskService.syncMapData(memRobot);
                    if(!sentMapsDataResult){
                        Thread.sleep(1000);
                        taskService.syncMapData(memRobot);
                    }
                    Thread.sleep(1000);
                    boolean sentTasksResult = taskService.syncFullTask(memRobot);
                    if(!sentTasksResult){
                        Thread.sleep(1000);
                        taskService.syncFullTask(memRobot);
                    }
                }

            }catch (Exception e){
                logger.warn("DataSynThread had error" + e.getMessage());
            }
            sync=false;
        }
    }


   /* public boolean handSentMapsDataSynAck(MemRobot memRobot){
        String siteId= memRobot.getSiteId();
        JSONObject mapsSynJson = new JSONObject();

        List<RunMark> runMarkList = runMarkMapper.getRunMarkBySiteId(siteId);
        if(runMarkList!=null && runMarkList.size()>0){
            List<Map> runMarksTemplist = new ArrayList<>();
            for(RunMark item:runMarkList){
                Map runMarksTempMap = new HashMap();
                runMarksTempMap.put("name",item.getMarkName());
                runMarksTempMap.put("lon",item.getLon());
                runMarksTempMap.put("lat",item.getLat());
                runMarksTempMap.put("move_style",item.getMoveStyle());
                runMarksTempMap.put("id",item.getUid());
                runMarksTemplist.add(runMarksTempMap);
            }
            JSONArray  runMarksJsonArray =JSONArray.parseArray(JSON.toJSONString(runMarksTemplist,SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.WriteMapNullValue));
            JSONObject runMarksSynJson = new JSONObject();
            runMarksSynJson.put("sync_style","full");
            runMarksSynJson.put("full",runMarksJsonArray);
            mapsSynJson.put("run_marks",runMarksSynJson);

        }

        List<RunLine> runLineList = runLineMapper.getRunLineBySiteId(siteId);
        if(runLineList!=null && runLineList.size()>0){
            List<Map> runLineTemplist = new ArrayList<>();
            for(RunLine item:runLineList){
                Map runLineTempMap = new HashMap();
                runLineTempMap.put("mark_id1",item.getMarkId1());
                runLineTempMap.put("mark_id2",item.getMarkId2());
                runLineTempMap.put("id",item.getUid());
                runLineTemplist.add(runLineTempMap);
            }
            JSONArray  runLineJsonArray =JSONArray.parseArray(JSON.toJSONString(runLineTemplist,SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.WriteMapNullValue));
            JSONObject runLineSynJson = new JSONObject();
            runLineSynJson.put("sync_style","full");
            runLineSynJson.put("full",runLineJsonArray);

            mapsSynJson.put("run_lines",runLineSynJson);
        }
        List<MaintainArea> maintainAreaList = maintainAreaMapper.getAll(siteId);
        if(maintainAreaList!=null && maintainAreaList.size()>0) {
            List<Map> maintainAreaTemplist = new ArrayList<>();
            for (MaintainArea maintainArea : maintainAreaList) {
                Map maintainAreaTempMap = new HashMap();
                List<Map> pointList = new ArrayList<>();
                maintainAreaTempMap.put("id", maintainArea.getUid());
                maintainAreaTempMap.put("name", maintainArea.getName());
                pointList.add(transformPoint(maintainArea.getPoint1()));
                pointList.add(transformPoint(maintainArea.getPoint2()));
                pointList.add(transformPoint(maintainArea.getPoint3()));
                pointList.add(transformPoint(maintainArea.getPoint4()));
//                pointList.add(transformPoint(maintainArea.getPoint5()));
                maintainAreaTempMap.put("points", pointList);
                maintainAreaTemplist.add(maintainAreaTempMap);
            }
            JSONArray maintainAreaJsonArray = JSONArray.parseArray(JSON.toJSONString(maintainAreaTemplist, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue));
            JSONObject maintainAreaSynJson = new JSONObject();
            maintainAreaSynJson.put("sync_style", "full");
            maintainAreaSynJson.put("full", maintainAreaJsonArray);
            mapsSynJson.put("obstacles", maintainAreaSynJson);
        }

        List<Map> chargeRoomList = chargeRoomMapper.findSynListBySiteId(siteId);
        if(chargeRoomList!=null && chargeRoomList.size()>0) {
            List<Map> chargeRoomTemplist = new ArrayList<>();
            for (Map map : chargeRoomList) {
                Map chargeRoomTempMap = new HashMap();
                List<Map> pointList = new ArrayList<>();
                chargeRoomTempMap.put("id", map.get("id"));
                chargeRoomTempMap.put("name", map.get("name"));
                String chargeRoomPoints=Float.toString((Float) map.get("x"))+","+Float.toString((Float) map.get("y"));
                pointList.add(transformPoint(chargeRoomPoints));
                chargeRoomTempMap.put("points", pointList);
                chargeRoomTemplist.add(chargeRoomTempMap);
            }
            JSONArray robotParamJsonArray = JSONArray.parseArray(JSON.toJSONString(chargeRoomTemplist, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue));
            JSONObject robotParamSynJson = new JSONObject();
            robotParamSynJson.put("sync_style", "full");
            robotParamSynJson.put("full", robotParamJsonArray);
            mapsSynJson.put("charge_room",robotParamSynJson);

        }
        String mapsSynDataString =mapsSynJson.toJSONString();

        String type="maps";
        Result  result= GeneralService.call(memRobot.getCh(),MsgNames.node_task_manage,MsgNames.service_data_syn,type,mapsSynDataString,5);
        if(result.getCode()==1){
            GeneralServiceAckMsg ackMsg=(GeneralServiceAckMsg)result.getData();
            if(ackMsg.getRet_code().equals("true")){
                return true;
            }
        }
        return false;
    }*/

    /*public boolean handleSendTasksDataSynAck(MemRobot memRobot){

        String siteId= memRobot.getSiteId();
        String robotId=memRobot.getRobotId();

        List<Task> tasksList = taskMapper.syncTasks(siteId,robotId) ;
        List<Map> tasksSynList = new ArrayList<>();
        for(Task task:tasksList){
            Map map = new HashMap();
            String taskUid = task.getUid();
            List<String> ptzSetList = taskPtzMapper.selectListStringByTaskUid(taskUid);
            List<Map> taskPeriod = taskPeriodMapper.selectByTaskUid(taskUid);

            map.put("id",task.getUid());
            map.put("name",task.getName());
            map.put("type",task.getEmergency());
            map.put("status",task.getStatus());
            map.put("ptz_sets",ptzSetList);
            map.put("plan",taskPeriod);
            tasksSynList.add(map);

        }
        JSONArray  tasksJsonArray =JSONArray.parseArray(JSON.toJSONString(tasksSynList,SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.WriteMapNullValue));
        JSONObject tasksJson = new JSONObject();
        tasksJson.put("sync_style","full");
        tasksJson.put("full",tasksJsonArray);

        String tasksSynDataString =tasksJson.toJSONString();

        String type="tasks";
        Result  result= GeneralService.call(memRobot.getCh(),MsgNames.node_task_manage,MsgNames.service_data_syn,type,tasksSynDataString,5);
        if(result.getCode()==1){
            GeneralServiceAckMsg ackMsg=(GeneralServiceAckMsg)result.getData();
            if(ackMsg.getRet_code().equals("true")){
                List<String> taskUidList = new ArrayList<>();
                for(Task task:tasksList){
                    String  taskUid = task.getUid();
                    taskUidList.add(taskUid);
                }
                taskMapper.updateTaskDataSynSuccess(taskUidList);
                return true;
            }
        }
        return false;

    }*/

    public static SentDataSynAckMsg toResultSentDataSynAck(JSONObject jsonObject) {
        SentDataSynAckMsg msg = new SentDataSynAckMsg();
        msg.setHeader(jsonObject.getJSONObject("header"));
        msg.setSender(jsonObject.getString("sender"));
        msg.setReceiver(jsonObject.getString("receiver"));
        msg.setTrans_id(jsonObject.getIntValue("trans_id"));
        msg.setTrans_id(jsonObject.getIntValue("robot_id"));
        msg.setRet_code(jsonObject.getString("ret_code"));
        msg.setRet_msg(jsonObject.getString("ret_msg"));
        msg.setData(jsonObject.getString("type"));
        msg.setData(jsonObject.getString("data"));
        return msg;
    }

    private Map<String,String> transformPoint(String point){
        Map map = new HashMap();
        String[] tempPoint =point.split(",");
        map.put("lon",Float.parseFloat(tempPoint[0]));
        map.put("lat",Float.parseFloat(tempPoint[1]));
        return map;
    }


}

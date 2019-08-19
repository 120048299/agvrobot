package com.wootion.taskmanager;


import com.alibaba.fastjson.JSONObject;
import com.wootion.agvrobot.utils.DateUtil;
import com.wootion.mapper.EventLogMapper;
import com.wootion.mapper.RobotParamMapper;
import com.wootion.mapper.SysParamMapper;
import com.wootion.model.EventLog;
import com.wootion.protocols.robot.msg.GeneralTopicMsg;
import com.wootion.protocols.robot.msg.MsgNames;
import com.wootion.protocols.robot.msg.TaskEventMsg;
import com.wootion.robot.MemRobot;
import com.wootion.robot.MemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 事件处理
 */
@Component
public class TaskEventThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(TaskEventThread.class);
    private static final String dateFormat="yyyy-MM-dd HH:mm:ss";
    private static LinkedBlockingQueue<Object> queque = new LinkedBlockingQueue<>();

    public static void addEvent(Object evt) {
        queque.add(evt);
    }

    @Autowired
    EventLogMapper eventLogMapper;

    public  Object takeEvent() {
        try {
            return queque.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Autowired
    private SysParamMapper sysParamMapper;

    @Autowired
    private RobotParamMapper robotParamMapper;



    @Override
    public void run() {
        logger.info("ResultSynThread started!");
//        RosBridgeClient client = null;

        while (true){
            try{
                Thread.sleep(50);
                JSONObject jobj =(JSONObject) takeEvent();
                String id =jobj.getString("id");
                String op = jobj.getString("op");
                String topic = jobj.getString("topic");
                String robot_ip = jobj.getString("robot_ip");
                if (MsgNames.topic_task_event.equals(topic)) {
                    GeneralTopicMsg eventMsg = toGeneralTopicMsg(jobj.getJSONObject("msg"));
                    eventMsg.setRobot_ip(robot_ip);
                    handleTaskEvent(eventMsg);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void handleTaskEvent(GeneralTopicMsg msg) {
        logger.debug(String.format("********************************handle task event msg = %s", msg.toString()));
        // 如果对应操作机器人不存在，丢弃消息
        MemRobot memRobot = MemUtil.queryRobot(msg.getRobot_ip());
        if (memRobot == null) {
            return;
        }
        try {
            JSONObject jobj = JSONObject.parseObject(msg.getData());
            String eventType = jobj.getString("event_type");
            Integer eventLevel = jobj.getIntValue("event_level");
            Date eventTime = DateUtil.stringToDate(jobj.getString("event_time"),dateFormat);
            String eventDesc = jobj.getString("event_desc");
            EventLog eventLog=new EventLog();
            eventLog.setSiteId(memRobot.getSiteId());
            eventLog.setRobotId(memRobot.getRobotId());
            eventLog.setEventType(eventType);
            eventLog.setEventLevel(eventLevel);
            eventLog.setEventTime(eventTime);
            eventLog.setEventDesc(eventDesc);

            logger.info(eventLog.toString());

            eventLogMapper.insert(eventLog);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msgtype",20004);
            jsonObject.put("eventLog",eventLog);
            memRobot.push(jsonObject);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private GeneralTopicMsg toGeneralTopicMsg(JSONObject jsonObject) {
        GeneralTopicMsg msg = new GeneralTopicMsg();
        msg.setHeader(jsonObject.getJSONObject("header"));
        msg.setSender(jsonObject.getString("sender"));
        msg.setReceiver(jsonObject.getString("receiver"));
        msg.setTrans_id(jsonObject.getIntValue("trans_id"));
        msg.setData(jsonObject.getString("data"));
        return msg;
    }

}

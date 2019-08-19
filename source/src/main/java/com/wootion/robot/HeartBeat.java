package com.wootion.robot;

import com.alibaba.fastjson.JSONObject;
import com.wootion.agvrobot.utils.HeartBeatLog;
import com.wootion.commons.Constans;
import com.wootion.model.SysParam;
import com.wootion.protocols.robot.RosBridgeClient;
import com.wootion.protocols.robot.msg.MsgNames;
import com.wootion.protocols.robot.msg.RobotInfo;
import com.wootion.task.EventQueue;
import com.wootion.task.alarm.RobotWarnParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;

@Component
public class HeartBeat extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(HeartBeat.class);

    //private final Integer MAX_CATCH_TASKS = 5;
    private boolean running = true;
    RobotWarnParser robotWarnParser;
    private static LinkedBlockingQueue<Object> heartBeatEvent = new LinkedBlockingQueue<>();
    //private PathPlanner pathPlanner;

   /* public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }*/

    public static void addTask(Object evt) {
        heartBeatEvent.add(evt);
    }

    public void close() {
        running = false;
    }

    @Override
    public void run() {
        logger.info("Heartbeat started!");
        RosBridgeClient client = null;

        robotWarnParser = new RobotWarnParser();

        while (running) {
            try {
                Object obj=heartBeatEvent.take();
                JSONObject jobj =(JSONObject) obj;
                //心跳消息相应，更新状态，获得各种数据和告警
                String id =jobj.getString("id");
                String op = jobj.getString("op");
                String topic = jobj.getString("topic");
                String robot_ip = jobj.getString("robot_ip");
                if (MsgNames.topic_robot_status.equals(topic)) {
                    RobotInfo robotInfo = toRobotInfo(jobj.getJSONObject("msg"));
                    robotInfo.setRobot_ip(robot_ip);
                    updateRobotInfo(robotInfo);
                }
            } catch (Exception e) {
                logger.warn("Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }
        logger.info("HeartBeat stopped!");
        try {
            client.close();
        } catch (Exception e) {
            logger.warn("close rosBridgeClient failed" + e.getMessage());
        }
    }



    /**
     * 根据心跳消息更新memoRobot的信息，更新web端状态
     *
     * @param
     */
    //private void updateRobotInfo(HeartBeatAckEvent heartBeatEvent) {
    private void updateRobotInfo(RobotInfo robotInfo) {
        //RobotInfo robotInfo= heartBeatEvent.getMsg();
        //HeartBeatLog.info(String.format("HeartBeatAckEvent msg = %s",robotInfo.toStringShortMsg()));
        //HeartBeatLog.debug(String.format("HeartBeatAckEvent msg = %s",robotInfo.content()));
        //更新机器人状态
        //System.out.println(System.currentTimeMillis()+":get heartbeat");
        int seqId = robotInfo.getTrans_id();
        // 如果对应操作机器人不存在，丢弃消息
        MemRobot memRobot = MemUtil.queryRobot(robotInfo.getRobot_ip());
        if (memRobot == null) {
            return;
        }
        memRobot.updateRobotInfo(robotInfo);
       // 告警信息有变化，则通知前台
        if (robotWarnParser.parseAlarm(robotInfo,memRobot.getRobotId(),memRobot.getSiteId())) {
            //memRobot.setWaring(true);  todo 有的告警要报但是不作为判断执行依据
            memRobot.pushSystemAlarm();
        }
        EventQueue.popMsgSession(seqId);
        //System.out.println(System.currentTimeMillis()+":over ");
    }


    private RobotInfo toRobotInfo(JSONObject jobj) {
        RobotInfo robotInfo = new RobotInfo();
        robotInfo.setSender(jobj.getString("sender"));
        robotInfo.setReceiver(jobj.getString("receiver"));
        robotInfo.setTrans_id(jobj.getIntValue("trans_id"));

        robotInfo.setOffline_warn(jobj.getShortValue("offline_warn"));
        robotInfo.setMode(jobj.getShortValue("mode"));
        robotInfo.setVelocity_x((float) jobj.getDoubleValue("velocity_x"));
        robotInfo.setVelocity_yaw((float) jobj.getDoubleValue("velocity_yaw"));
        robotInfo.setPoint(jobj.getJSONArray("position"));
        robotInfo.setOrientation((float) jobj.getDoubleValue("orientation"));

        robotInfo.setWheel_status((byte) jobj.getIntValue("wheel_status"));
        robotInfo.setLight_status((byte) jobj.getIntValue("light_status"));
        robotInfo.setStop_status((byte) jobj.getIntValue("stop_status"));
        robotInfo.setBattery_voltage(jobj.getShortValue("battery_voltage"));
        robotInfo.setBattery_current(jobj.getShortValue("battery_current"));
        robotInfo.setBattery_quantity((byte) jobj.getIntValue("battery_quantity"));
        robotInfo.setBattery_status((byte) jobj.getIntValue("battery_status"));

        robotInfo.setPump_status((byte) jobj.getIntValue("pump_status"));
        robotInfo.setMotor_status((byte) jobj.getIntValue("motor_status"));
        robotInfo.setDisable_status(jobj.getByte("disable_status"));
        robotInfo.setTemperature(jobj.getJSONArray("temperature"));
        robotInfo.setNav_status((byte) jobj.getIntValue("nav_status"));
        robotInfo.setSensor_status(jobj.getIntValue("sensor_status"));
        return robotInfo;
    }

}

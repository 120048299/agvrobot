package com.wootion.robot;


import com.alibaba.fastjson.JSONObject;
import com.wootion.agvrobot.utils.HeartBeatLog;
import com.wootion.mapper.RobotMapper;
import com.wootion.mapper.SiteMapper;
import com.wootion.model.Robot;
import com.wootion.model.Site;
import com.wootion.protocols.robot.msg.*;
import com.wootion.protocols.robot.operation.ServerStatusOp;
import com.wootion.task.EventQueue;
import com.wootion.task.event.AdvertiseRobotEvent;
import com.wootion.task.event.StartRosBridgeEvent;
import com.wootion.task.event.SubscribeRobotEvent;
import com.wootion.utiles.DataCache;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Robot Status that no need sync with DB
 */
public class MemUtil {
    private static final Logger logger = LoggerFactory.getLogger(MemUtil.class);

//    public static String ros_bridge_url = DataCache.getSysParamStr("ros.bridgeUrl");
 //	static long startConnectTime =0L;
 

    private static final List<MemRobot> memRobotList = new ArrayList<>();
    private static final AtomicInteger opId = new AtomicInteger();
    //private static Map<String,Channel> channelUserInfoMap = new Hashtable<>();
    // robotIp --> channels
    private static ConcurrentHashMap<String, Channel> monitorChannels = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Channel> controlChannels = new ConcurrentHashMap<>();

    private MemUtil() {

    }



    public static List<MemRobot> getMemRobotList() {
        return memRobotList;
    }

    public static Channel getControlChannel(String userId) {
        return controlChannels.get(userId);
    }

    public static void setControlChannel(String userId,Channel channel) {
        //controlChannels.put()
    }

    public static boolean syncMemRobot(RobotMapper robotMapper, SiteMapper siteMapper) {
        if (robotMapper == null) {
            return false;
        }
        List<Robot> robots = robotMapper.findAll();
        for (Robot r : robots) {
            if (r.getRobotIp().equals("0.0.0.0")) {
                continue;
            }
            //added by btrmg for robot status 2019.05.07
            if(r.getStatus()!=1)
            {
                continue;
            }
            //added end
            if (MemUtil.queryRobot(r.getRobotIp()) != null) {
                logger.info("syncMemRobot: robot("+r.getRobotIp()+") already exists, ignore it");
                continue;
            }
            Site s = siteMapper.findByUid(r.getSiteId());
            MemRobot memRobot = new MemRobot(r,s);
            memRobot.init();
            logger.info("syncMemRobot: add MemRobot " + r.getUid() + " " + r.getRobotIp());
            memRobotList.add(memRobot);
  //          return true;
        }

       /* //单独启动一个MemRobot 作为服务器上的读表接口
        Site site=new Site();
        site.setUid("0");
        Robot robot =new Robot();
        robot.setUid("read");
        robot.setRobotIp("192.168.100.140");
        robot.setRobotPort(9090);
        robot.setSiteId("0");
        MemRobot memRobot=new MemRobot(robot,site);
        memRobot.init();
        memRobotList.add(memRobot);*/
        return true;
    }


    public static boolean syncMemRobot(Robot r,Site s) {
        for (MemRobot memRobot:memRobotList){
            if(memRobot.getRobotId().equals(r.getRobotIp())){
                return false;
            }
        }
        MemRobot memRobot = new MemRobot(r,s);
        memRobot.init();
        logger.info("syncMemRobot: add MemRobot " + r.getUid() + " " + r.getRobotIp());
        memRobotList.add(memRobot);
        return true;
    }


    public static void subscribeRobot(SubscribeRobotEvent subscribeRobotEvent) {
        String robotIp = subscribeRobotEvent.getRobotIp();
        if (robotIp == null || robotIp.length() == 0) {
            for (MemRobot memRobot: memRobotList) {
                memRobot.connectRosLink(new Subscribe(MsgNames.topic_robot_status, MsgNames.topic_robot_status_type));
                memRobot.connectRosLink(new Subscribe(MsgNames.topic_read_scale_ack, MsgNames.topic_read_scale_ack_type));

                memRobot.connectRosLink(new Subscribe(MsgNames.topic_addptzset_status, MsgNames.topic_addptzset_status_type));
                memRobot.connectRosLink(new Subscribe(MsgNames.topic_infrared_ack, MsgNames.topic_infrared_ack_type));
                memRobot.connectRosLink(new Subscribe(MsgNames.topic_virtual_obstacle_ack, MsgNames.topic_virtual_obstacle_ack_type));
                memRobot.connectRosLink(new Subscribe(MsgNames.topic_terrace_ack, MsgNames.topic_terrace_ack_type));
                memRobot.connectRosLink(new Subscribe(MsgNames.topic_camera_ack, MsgNames.topic_camera_ack_type));

                //task_manage接口
                memRobot.connectRosLink(new Subscribe(MsgNames.topic_task_status, MsgNames.topic_general_type));
                memRobot.connectRosLink(new Subscribe(MsgNames.topic_ptz_status, MsgNames.topic_general_type));
                memRobot.connectRosLink(new Subscribe(MsgNames.topic_task_event, MsgNames.topic_task_event_type));

                //FOR TEST
                /*memRobot.connectRosLink(new Subscribe(Constans.FIBONACCI_FEEDBACK_NAME, Constans.FIBONACCI_FEEDBACK_TYPE));
                memRobot.connectRosLink(new Subscribe(Constans.FIBONACCI_RESULT_NAME, Constans.FIBONACCI_RESULT_TYPE));*/

            }
        } else {
            MemRobot memRobot = queryRobot(robotIp);
            if (memRobot != null) {
                // TODO: 如果重复注册Topic会怎样
                memRobot.connectRosLink(new Subscribe(subscribeRobotEvent));
            }
        }
    }

    public static void advertiseRobot(AdvertiseRobotEvent advertiseRobotEvent) {
        String robotIp = advertiseRobotEvent.getRobotIp();
        if (robotIp == null || robotIp.length() == 0) {
            for (MemRobot memRobot : memRobotList) {
                memRobot.connectRosLink(new Advertise(MsgNames.topic_server_status, MsgNames.topic_server_status_type));
                memRobot.connectRosLink(new Advertise(MsgNames.topic_mode_command, MsgNames.topic_mode_command_type));
                memRobot.connectRosLink(new Advertise(MsgNames.topic_move_command, MsgNames.topic_move_command_type));
                memRobot.connectRosLink(new Advertise(MsgNames.topic_control_command, MsgNames.topic_control_command_type));
                memRobot.connectRosLink(new Advertise(MsgNames.topic_read_scale_command, MsgNames.topic_read_scale_command_type));
                memRobot.connectRosLink(new Advertise(MsgNames.topic_addptzset_command, MsgNames.topic_addptzset_command_type));

                memRobot.connectRosLink(new Advertise(MsgNames.topic_infrared_command, MsgNames.topic_infrared_command_type));
                memRobot.connectRosLink(new Advertise(MsgNames.topic_virtual_obstacle_command, MsgNames.topic_virtual_obstacle_command_type));
                memRobot.connectRosLink(new Advertise(MsgNames.topic_terrace_command, MsgNames.topic_terrace_command_type));
                memRobot.connectRosLink(new Advertise(MsgNames.topic_camera_command,MsgNames.topic_camera_command_type));
                memRobot.connectRosLink(new Advertise(MsgNames.topic_preset_rotate_command, MsgNames.topic_preset_rotate_command_type));

                //FOR TEST
                /*memRobot.connectRosLink(new Advertise(Constans.FIBONACCI_GOAL_NAME, Constans.FIBONACCI_GOAL_TYPE));*/
            }
        } else {
            MemRobot memRobot = queryRobot(robotIp);
            if (memRobot != null) {
                // TODO: 如果重复注册Topic会怎样

                // 根据事件重连topic
                memRobot.connectRosLink(new Advertise(advertiseRobotEvent));
            }
        }
    }

    public static MemRobot queryRobot(String ip) {
        if (memRobotList.isEmpty()) {
            return null;
        }
        for (MemRobot r : memRobotList) {
            if (r.getRobotIp().equals(ip)) {
                return r;
            }
        }
        return null;
    }

    public static MemRobot queryRobotById(String robotId) {
        if (memRobotList.isEmpty()) {
            return null;
        }
        for (MemRobot r : memRobotList) {
            if (r.getRobotId().equals(robotId)) {
                return r;
            }
        }
        return null;
    }

    public static void  checkRobotStatus() {

        for(MemRobot memRobot: memRobotList) {
           /* Channel ch = memRobot.getCh();
            if (ch == null || !ch.isOpen()) {
                logger.info("--robot:"+ memRobot.getRobotIp() +" channel is null or not open ---");
                reconnectRos(memRobot);
                continue;
            }*/
            memRobot.checkOffline();
            if(!memRobot.isOnline()){
                logger.info("---robot:"+ memRobot.getRobotIp() + " is offline ----");
                reconnectRos(memRobot);
            }
            memRobot.checkMode();
        }
        //pushTaskRunningStatus();
    }

    public static void reconnectRos(MemRobot memRobot){
        long now=System.currentTimeMillis();
        int reConnectRosTime=DataCache.getSysParamInt("ros.reconnectRosTime",10);
        if((now-memRobot.getStartConnectTime())/1000>reConnectRosTime){
            logger.info("-------- reconnect ros --------");
            EventQueue.addTask(new StartRosBridgeEvent(memRobot));
            memRobot.setStartConnectTime(System.currentTimeMillis());
            return ;
        }
    }

    public static void initRosMessage(String robotIp){
        logger.info("------- initRosMessage subscribe and advertise -------");

        EventQueue.addTask(new SubscribeRobotEvent(robotIp,MsgNames.topic_robot_status, MsgNames.topic_robot_status_type));
        EventQueue.addTask(new SubscribeRobotEvent(robotIp,MsgNames.topic_read_scale_ack, MsgNames.topic_read_scale_ack_type));
        EventQueue.addTask(new SubscribeRobotEvent(robotIp,MsgNames.topic_addptzset_status, MsgNames.topic_addptzset_status_type));
        EventQueue.addTask(new SubscribeRobotEvent(robotIp,MsgNames.topic_infrared_ack, MsgNames.topic_infrared_ack_type));
        EventQueue.addTask(new SubscribeRobotEvent(robotIp,MsgNames.topic_virtual_obstacle_ack, MsgNames.topic_virtual_obstacle_ack_type));
        EventQueue.addTask(new SubscribeRobotEvent(robotIp,MsgNames.topic_terrace_ack, MsgNames.topic_terrace_ack_type));
        EventQueue.addTask(new SubscribeRobotEvent(robotIp,MsgNames.topic_camera_ack, MsgNames.topic_camera_ack_type));
        EventQueue.addTask(new SubscribeRobotEvent(robotIp,MsgNames.topic_task_status, MsgNames.topic_general_type));
        EventQueue.addTask(new SubscribeRobotEvent(robotIp,MsgNames.topic_ptz_status, MsgNames.topic_general_type));
        EventQueue.addTask(new SubscribeRobotEvent(robotIp,MsgNames.topic_task_event, MsgNames.topic_task_event_type));

        EventQueue.addTask(new AdvertiseRobotEvent(robotIp, MsgNames.topic_server_status, MsgNames.topic_server_status_type));
        EventQueue.addTask(new AdvertiseRobotEvent(robotIp, MsgNames.topic_mode_command, MsgNames.topic_mode_command_type));
        EventQueue.addTask(new AdvertiseRobotEvent(robotIp, MsgNames.topic_move_command, MsgNames.topic_move_command_type));
        EventQueue.addTask(new AdvertiseRobotEvent(robotIp, MsgNames.topic_control_command, MsgNames.topic_control_command_type));
        EventQueue.addTask(new AdvertiseRobotEvent(robotIp, MsgNames.topic_read_scale_command, MsgNames.topic_read_scale_command_type));
        EventQueue.addTask(new AdvertiseRobotEvent(robotIp,MsgNames.topic_addptzset_command, MsgNames.topic_addptzset_command_type));
        EventQueue.addTask(new AdvertiseRobotEvent(robotIp,MsgNames.topic_infrared_command, MsgNames.topic_infrared_command_type));
        EventQueue.addTask(new AdvertiseRobotEvent(robotIp,MsgNames.topic_virtual_obstacle_command, MsgNames.topic_virtual_obstacle_command_type));
        EventQueue.addTask(new AdvertiseRobotEvent(robotIp,MsgNames.topic_terrace_command, MsgNames.topic_terrace_command_type));
        EventQueue.addTask(new AdvertiseRobotEvent(robotIp,MsgNames.topic_camera_command,MsgNames.topic_camera_command_type));
        EventQueue.addTask(new AdvertiseRobotEvent(robotIp,MsgNames.topic_preset_rotate_command, MsgNames.topic_preset_rotate_command_type));


    }

/*

    private static void startSyn(MemRobot memRobot){
        //可以发起任务
        logger.debug("SyncTaskEvent");
        SyncTaskEvent event= new SyncTaskEvent();
        event.setRobotId(memRobot.getRobotId());
        EventQueue.addTask(event);
    }
*/



    public static boolean deleteMemRobotByIp(String ip) {
        MemRobot memRobot = queryRobot(ip);
        if (memRobot != null) {
            logger.info("deleteMemRobotByIp: delete robot " + ip);
            memRobotList.remove(memRobot);
            return true;
        }
        return false;
    }

    public static Integer newOpId() {
        return opId.incrementAndGet();
    }

    /*public static void updateRobotDefaultMark(String siteId, RunMark runMark) {
        for(MemRobot memRobot: memRobotList) {
            if (memRobot.getSiteId().equals(siteId) && memRobot.getLastRunMark() == null) {
                if (memRobot.getSiteId().equals(runMark.getSiteId())) {
                    logger.info("updateRobotDefaultMark: set robot "+ memRobot.getRobotIp() + " default RunMark to " + runMark.getUid());
                    memRobot.setLastRunMark(runMark);
                }
            }
        }
    }*/

    public static MemRobot queryRobot(Channel channel) {
        if (channel == null) {
            logger.info("queryRobot with Channel: channel is null!");
            return null;
        }
        for (MemRobot memRobot : memRobotList) {
            if (memRobot.isMonitorby(channel)) {
                return memRobot;
            }
        }
        return null;
    }


    /**
     * 网络连接成功
     * @param ch
     */
    public static void setRobotCh(MemRobot memRobot,Channel ch) {
        if(memRobot == null){
            return;
        }
        Channel oldCh = memRobot.getCh();
        if (oldCh != null && oldCh.isOpen()) {
            try {
				oldCh.close().sync();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
            }
        }

        if (ch == null) {
            //只要链路出问题即设置离线和停止任务
            logger.error(" *****web svr  ROS channel  IS NULL, set to OFFLINE.*****");
            memRobot.setOnline(false);
            logger.info(memRobot.getRobotIp()+"writeCmd: ch is null, reconnect RosBridgeServer!");
            memRobot.setCh(null);
            return ;
        }

        //首次注册Ros消息
        memRobot.setCh(ch);
        initRosMessage(memRobot.getRobotIp());
    }


    public static Channel getRobotChannel(MemRobot memRobot) {
        return memRobot.getCh();
    }

	public static void writeAllCmd() {
        for (MemRobot memRobot:memRobotList) {
            writeHeartBeat(memRobot);
        }
    }

    /*
        永远在发起
    */
    private static void writeHeartBeat(MemRobot memRobot) {
        Channel ch=memRobot.getCh();
        if(ch==null)
        {
            return ;
        }
        int opId = MemUtil.newOpId();
        GeneralTopicMsg msg = new GeneralTopicMsg();
        msg.setTrans_id(opId);
        Header header=new Header();
        header.setSeq(opId);
        ServerStatusOp serverStatusOp = new ServerStatusOp(opId, msg);
        if (ch != null) {
            ChannelFuture f = ch.writeAndFlush(serverStatusOp);
            f.addListener(( future) -> {
                    if (!future.isSuccess()) {
                        HeartBeatLog.error("writeHeartBeat failed seqId: " + opId + " " + future.cause());
                        future.cause().printStackTrace();
                        logger.error("机器人接口异常：端口异常");
                    }else {
                        HeartBeatLog.debug("writeHB: seqId "+opId+" success");
                    }
                });
            HeartBeatLog.debug("writeHeartBeat: " + JSONObject.toJSON(serverStatusOp).toString());
        } else {
            HeartBeatLog.error("write cmd failed, ch is null!");
            HeartBeatLog.error("cmd: " + JSONObject.toJSON(serverStatusOp).toString());
        }
    }


    public static void pushRobotStatus() {
        for (MemRobot memRobot: memRobotList) {
            memRobot.pushRobotStatus(false);
        }
    }


    /*public static void pushTaskRunningStatus() {
        ConcurrentHashMap<String,TaskRunner>  taskRunnerMap= MemUtil.getTaskRunnerMap();
        for(TaskRunner taskRunner: taskRunnerMap.values()) {
            taskRunner.pushTaskRunningStatus(false);
        }
    }
*/

    /*public static void sendTimeOutMsg() {
        long now = (long) (System.currentTimeMillis()/1000);
        for (MemRobot memRobot: memRobotList) {
            Publish<?> cmd = memRobot.curStep();
            if (cmd == null) {
                continue;
            }

            if (cmd.resendTimes() > Constans.ROBOT_TIMEOUT_TIMES) {
                //memRobot.setRobotStatusDirectly(ROBOT_STATUS.OFFLINE);
                EventQueue.addTask(new StopTaskEvent(null,memRobot.getRobotIp()));
                MsgTraceUtil.writeMsg(memRobot.getRobotIp()+"cmd timeout set to OFFLINE!");
                continue;
            }

            if (cmd.opStatus() == OP_STATUS.OP_SEND_FAIL && cmd.sendTime() + 3 < now) {
                memRobot.writeCmd();
                continue;
            } 
            if (cmd.opStatus() == OP_STATUS.OP_SEND_SUCC && cmd.sendTime() + Constans.TIMEOUT_SECONDS*cmd.resendTimes() < now) {
                memRobot.writeCmd();
                continue;
            }
            if (cmd.opStatus() == OP_STATUS.OP_SENDING && cmd.sendTime() + Constans.TIMEOUT_SECONDS < now) {
                memRobot.writeCmd();
                continue;
            }
            if (cmd.opStatus() == OP_STATUS.OP_SEND_TIMEOUT && cmd.sendTime() + Constans.TIMEOUT_SECONDS * cmd.resendTimes() < now) {
                memRobot.writeCmd();
                continue;
            }
            if (cmd.opStatus() == OP_STATUS.OP_RESPONDED && cmd.sendTime() + Constans.TIMEOUT_SECONDS * cmd.resendTimes() < now) {
                memRobot.writeCmd();
                continue;
            }
        }
	}
*/
    /*public static synchronized ConcurrentHashMap<String,TaskRunner> getTaskRunnerMap() {
        return taskRunnerMap;
    }

    public static synchronized TaskRunner getTaskRunner(String taskPlanId) {
        return taskRunnerMap.get(taskPlanId);
    }

    public static synchronized void addTaskRunner(TaskRunner taskRunner) {
        taskRunnerMap.put(taskRunner.getMemTask().getJob().getUid(),taskRunner);
    }
    public static synchronized void removeTaskRunner(TaskRunner taskRunner) {
        taskRunnerMap.remove(taskRunner.getMemTask().getJob().getUid(),taskRunner);
    }

    public static synchronized ConcurrentHashMap<String,IChargeRunner> getChargeRunnerMap() {
        return chargeRunnerMap;
    }

    public static synchronized IChargeRunner getChargeRunner(String robotId) {
        return chargeRunnerMap.get(robotId);
    }
    public static synchronized void addChargeRunner(IChargeRunner chargeRunner) {
        chargeRunnerMap.put(chargeRunner.getRobotId(),chargeRunner);
    }
    public static synchronized void removeChargeRunner(IChargeRunner chargeRunner) {
        chargeRunnerMap.remove(chargeRunner,chargeRunner);
    }*/
}


package com.wootion.robot;


import com.alibaba.fastjson.JSONObject;
import com.wootion.agvrobot.dto.robotTask.TaskDetail;
import com.wootion.agvrobot.dto.robotTask.TaskResultInfo;
import com.wootion.agvrobot.utils.HeartBeatLog;
import com.wootion.agvrobot.utils.MemRobotPushLog;
import com.wootion.agvrobot.utils.NumberUtil;
import com.wootion.alg.SimpleFIFO;
import com.wootion.commons.MSG_TYPE;
import com.wootion.model.Robot;
import com.wootion.model.Site;
import com.wootion.model.UserInfo;
import com.wootion.protocols.robot.msg.RobotInfo;
import com.wootion.task.EventQueue;
import com.wootion.task.ModeAndMoveControl;
import com.wootion.task.RobotAuthManage;
import com.wootion.task.RobotTaskStatus;
import com.wootion.task.alarm.SysAlarmHelper;
import com.wootion.task.event.*;
import com.wootion.task.map2.Coordinate;
import com.wootion.taskmanager.TaskControl;
import com.wootion.utiles.DataCache;
import com.wootion.vo.RobotStatusInfo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.wootion.commons.Constans.MSG_TYPE_RESP;

@Data
public class MemRobot {
    private RobotAuthManage robotAuthManage = new RobotAuthManage(this);
    private static final Logger logger = LoggerFactory.getLogger(MemRobot.class);

    private Robot robot;
    private String robotId;
    public String getRobotIp() {
        return robot == null? "": robot.getRobotIp();
    }

    private String siteId;
    private Site site;

    private String robotRosUrl;
    // 机器人消息接口
    private Channel ch;

    public MemRobot(Robot r, Site s){
        this.setRobot(r);
        this.setRobotId(r.getUid());
        this.setSiteId(r.getSiteId());
        this.setSite(s);
        this.setRobotRosUrl("ws://"+ r.getRobotIp()+":"+r.getRobotPort().toString());
    }

    public void init() {
        rosHeartBeatTimeout=DataCache.getSysParamInt("ros.heartBeatTimeout");
        RobotInfo robotInfo=new RobotInfo();
        robotInfo.setOffline_warn((short)1);
        robotInfo.setRobot_ip(this.robot.getRobotIp());
        robotInfo.setOrientation(0f);
        Float position[]=new Float[3];
        position[0]=0f;
        position[1]=0f;
        position[2]=0f;
        robotInfo.setPosition(position);
        robotInfo.setBattery_quantity((byte)0);
        robotInfo.setBattery_voltage((short )0);
        robotInfo.setBattery_status((byte)0);
        Byte temperature[]=new Byte[7];
        for(int i=0;i<7;i++){
            temperature[i]=(byte)0;
        }
        robotInfo.setTemperatureArray(temperature);
        this.robotInfo=robotInfo;
    }

    // 从机器人上报状态中更新
    RobotInfo robotInfo;
    private boolean online =false;
    private boolean waring =false;
    private boolean stopped =false;//急停中
    private boolean navLost =true;
    private int robotMode = -1;//-1 无,0 任务模式，1后台遥控，2 手柄遥控
    public double getCurVel() {
        return robotInfo==null?0:robotInfo.getVelocity_x();
    }
    private int chargeFlowStatus=0; //没有充电流程，充电流程中
    private int charging=0;//0 没有充电 1充电中

    private int emergency =0; //0 普通模式  1 紧急定位模式

    int rosHeartBeatTimeout = 10;
    long startConnectTime = 0;
    private long lastHeartBeatTime =0L;      // last time when recv heartbeatack message
    private long warnJoyRemoteResumeTime=0L;
    private long lastCanSendSynTaskTime=0;   //最后一次可以发起同步任务的时间，目的减少频率，防止重复

    private long lastRemoteControlTime=0L; // last time when do remote control

    private long  idleStartTime = 0;
    //added by btrmg for notify front the task status is timeout 2018.12.18
    private long timeoutnotifytime = 0;
    //added end

    //网页客户端websocket连接，每次网页登录会初始化一个
    private Set<Channel> monitorChannelList = new HashSet<>();
    //远程控制网页客户端websocket连接
    private Channel controller = null;
    private UserInfo userController=null;

    //当前机器人位置
    //private RunMark lastRunMark; // last RunMark Point

    //added by btrmg for task pause ,stop 2018.12.19
    // 0无意义,isTask为false; ：1 运行(istasking true),  2 原地暂停(istasking true), 3 原地终止(此时tasking=false),  4 临时暂停(istasking true)
    //protected int taskStatus = 0;
    int stopInPlace = 0; //1 原地暂停, 2 原地终止
    //added end

    private RobotTaskStatus robotTaskStatus;

    private List<TaskResultInfo> taskResultInfoList;
    private List<TaskDetail> taskDetailList;

    // 机器人链路状态topic
    private ConcurrentHashMap<String,Boolean> robotLinkStatus = new ConcurrentHashMap();

    public void changeToOffline() {
        this.robotMode=-1;
        this.charging=0;
        logger.error(" *****web svr  ROS channel  IS NULL, set to OFFLINE.*****");
        this.setOnline(false);
        if (SysAlarmHelper.dealSysAlarm(siteId, robotId, "0002", 1, this.robot.getName()+"离线")) {
            pushSystemAlarm();
        }
    }

    /*
        处理范围：从机器人传来的消息更新状态，以机器人为准。只更新 WARNING CHARGING HANDLE
        根据 RobotInfo 中的work_mode更新状态
        0 故障 10 自检 20 充电中
        100 IDLE
        110 远程遥控
        120 后台任务
        200 手柄控制
        300 返航

     */
    public void updateRobotInfo(RobotInfo info) {
        if (info == null) {
            return ;
        }
        if (!this.getRobotIp().equals(info.getRobot_ip()) ) {
            return ;
        }
        long now=(new Date()).getTime();
        lastHeartBeatTime =now;
        if(checkOnline(info)==0){
            //仍然没有上线,确认上线后才更新状态到内存中
            return ;
        }
        RobotInfo oldInfo=this.robotInfo;
        this.robotInfo = info;
        this.robotMode  = info.getMode();
        this.stopped  = parseStatus(robotInfo.getStop_status(),5);
        if(parseStatus(robotInfo.getBattery_status(),4)){
            this.charging =1;
        }else{
            this.charging =0;
        }
        this.navLost  = parseStatus(robotInfo.getNav_status(),1);
        if(this.robotMode==1 || (this.robotMode==0 && this.emergency==1)){
            if( (now-lastRemoteControlTime)/1000>DataCache.getSysParamInt("ros.remoteControlIdleTime")*60){
                EventQueue.addTask(new RemoteControlTimeOutEvent(getRobotIp()));
            }
        }

        boolean hasOperation=hasOperation(oldInfo,info);
        if(hasOperation){
            idleStartTime=now;
        }else
        {
            if(this.stopInPlace == 1 ) {
                //1分钟过后还没有，则说明客户端已经不存在，让任务继续执行
                if((now-idleStartTime)/1000>DataCache.getSysParamInt("task.pauseInPlace")*60+70){
                    EventQueue.addTask(new ResumeRobotTaskPlanEvent(null,this.getRobotIp()));
                }else if((now-idleStartTime)/1000>DataCache.getSysParamInt("task.pauseInPlace")*60) {
                    //超过时间后通知客户端
                    pushTimeOutTaskSTatus(info);
                }
            }
            if(this.stopInPlace == 2){
                //
                if((now-idleStartTime)/1000>DataCache.getSysParamInt("task.stopInPlace")*60+70){
                    logger.info("stopInPlace over time ,add OneKeyBackEvent");
                    EventQueue.addTask(new ChargeEvent(this.getRobotIp()));
                }else if((now-idleStartTime)/1000>DataCache.getSysParamInt("task.stopInPlace")*60) {
                    //超过时间后通知客户端
                    pushTimeOutTaskSTatus(info);
                }
            }
        }
    }

    ////如果有任务命令动作，mode,status会切换,位置，角度，云台俯仰等会变化
    private boolean hasOperation(RobotInfo oldInfo,RobotInfo info){
        boolean hasOperation=false;
        if(oldInfo==null){
            return true;
        }
        if(info.getMode()!=oldInfo.getMode()){
            return true;
        }
        //位置是否有变化
        Float oldPosition[]=oldInfo.getPosition();
        Float newPosition[]=info.getPosition();
        double dif = 0.01;//1cm,或者角度
        if(Math.abs(oldPosition[0]- newPosition[0]) > dif)
        {
            return true;
        }
        if(Math.abs(oldPosition[1]- newPosition[1]) > dif)
        {
            return true;
        }
        //机器人角度是否有变化
        Float oldOrient=oldInfo.getOrientation();
        Float newOrient=info.getOrientation();
        if(Math.abs(oldOrient- newOrient) > dif)
        {
            return true;
        }
        return hasOperation;
    }

    private boolean parseStatus(byte statusByte,int bitPos){
        int []bits=NumberUtil.byteToBitArray(statusByte);
        if(bits[bitPos]==1){
            return true;
        }else{
            return false;
        }
    }

    private void sendSynTaskEvent(){
        //可以发起任务
        logger.debug("memRobot sendSynTaskEvent");
        SyncTaskEvent event= new SyncTaskEvent();
        event.setRobotId(this.robotId);
        EventQueue.addTask(event);
    }

    public void pushTimeOutTaskSTatus(RobotInfo info){
        JSONObject retjson = new JSONObject();
        retjson.put("msgtype", 10025);
        retjson.put("state", "TimeOut");
        logger.info("原地暂停/终止 等待超时");
        push(retjson);
    }

    public <T> void connectRosLink(T t) {
        // init ros
        Channel ch = this.getCh();

        if (ch == null || !ch.isOpen() || !ch.isActive()) {
            logger.error("SyncRouteEvent: advertise robot_command msg faild! channel is not active");
            this.online=false;
            // set link status to false
            return;
        }

        // need clear link status when reconnected
//        if (robotLinkStatus.containsKey(t.toString()) && robotLinkStatus.get(t.toString())) {
//            logger.info("link {} already up", t.toString());
//            return;
//        }

        String msg = JSONObject.toJSON(t).toString();
        ChannelFuture channelFuture = ch.writeAndFlush(new TextWebSocketFrame(msg));
        logger.warn("robot init: " + msg);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    logger.info("write subscribe robot status success");
                    robotLinkStatus.put(t.toString(), true);
                    //setRobotStatus(ROBOT_STATUS.ONLINE);
                } else {
                    logger.warn("write subscribe robot status failed!");
                }
            }
        });
    }


    public void addMonitorChannel(Channel channel) {
        if (channel == null) {
            logger.info("addMonitorChannel: channel is null!");
            return ;
        }

        monitorChannelList.add(channel);
    }

    public void removeMonitorChannel(Channel channel) {
        if (channel == null) {
            logger.info("removeMonitorChannel: channel is null");
            return ;
        }
        monitorChannelList.remove(channel);
    }

    public void pushRobotStatus(boolean isDone) {
        JSONObject retjson = new JSONObject();
        RobotStatusInfo rsInfo=new RobotStatusInfo();
        retjson.put("msgtype", MSG_TYPE.MT_STATUS.getValue() + MSG_TYPE_RESP);//10000
        retjson.put("status", 1);
        retjson.put("result", "success");
        retjson.put("isDone", isDone);
        if(DataCache.getSysParamInt("ros.hasWeatherStation")==1){
            //todo wait
          /*  WeatherUtil util = WeatherStationMonitor.getWeatherUtil(this.getSiteId());
            if (util.weatherStatus != null) {
                rsInfo.setWeatherTemperature(util.weatherStatus.getTemp());
                rsInfo.setHumidity(util.weatherStatus.getHum());
                rsInfo.setRainFall(util.weatherStatus.getRainfall());
                rsInfo.setWindSpeed(util.weatherStatus.getWindSpeed());
                rsInfo.setIsRain(util.weatherStatus.getIsRain());
            }*/
        }
        if (this.getRobotInfo() != null) {
            rsInfo.setRobotInfo(this.getRobotInfo());
            double[] pt = Coordinate.nav2Web(this.getRobotInfo().getPosition()[0],
                    this.getRobotInfo().getPosition()[1],this.getSite().getScale());
            retjson.put("navPoint", this.getRobotInfo().getPosition());//导航坐标
            retjson.put("curPoint", pt);//地图坐标
            retjson.put("angle", this.getRobotInfo().getOrientation());
        }
        rsInfo.setStatuses(this);
        retjson.put("robotStatusInfo", rsInfo);
        retjson.put("robotTaskStatus", this.robotTaskStatus);
        //logger.debug(retjson.toString());
        push(retjson);
    }

    public void pushTaskLogFindFinised() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msgtype", 20006);
        jsonObject.put("result", "success");
        for (Channel ch : monitorChannelList) {
            push(jsonObject, ch);
        }
    }

    public void pushTaskLogAllFinised() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msgtype", 20001);
        jsonObject.put("result", "success");
        for (Channel ch : monitorChannelList) {
            push(jsonObject, ch);
        }
    }

    public void pushTaskAlarm(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msgtype",20002);
        jsonObject.put("result","success");
        for (Channel ch : monitorChannelList) {
            push(jsonObject, ch);
        }
    }


    /**
     * 发送告警变化通知
     */
    public void pushSystemAlarm(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msgtype",20003);
        jsonObject.put("result","success");
        System.out.println(jsonObject.toString());
        for (Channel ch : monitorChannelList) {
            push(jsonObject, ch);
        }
    }

    /**
     * 发送给所有正在监视机器人的客户端
     * @param jsonObject
     */
    public void push(JSONObject jsonObject) {
        for (Channel ch : monitorChannelList) {
            push(jsonObject, ch);
        }
    }


    public void push(JSONObject jsonObject,Channel  ch){

            ChannelFuture f = ch.writeAndFlush(new TextWebSocketFrame(jsonObject.toString()));
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (!channelFuture.isSuccess()){
                        MemRobotPushLog.debug("发送消息到" + ch.localAddress() + "未成功" + channelFuture.cause()+jsonObject);
                    }
                }
            });
    }

    public boolean isMonitorby(Channel channel) {
        if (channel == null) {
            return false;
        }
        return monitorChannelList.contains(channel);
    }

    /**
     * 远程遥控客户端连接
     * @param channel
     */
    public synchronized void setController(UserInfo userInfo,Channel channel) {
        if (channel != null) {
            controller = channel;
            userController = userInfo;
        } else { // release control
            controller = null;
            userController = null;
        }
    }

    public synchronized Channel getController() {
        return controller;
    }


    /**
     * 根据最近收到的消息 距离现在的时间 来判断离线,
     * 默认rosHeartBeatTimeout (秒)没有收到心跳,设置OFFLINE,发起重连，和注册
     * 注册后10秒内没有收到心跳，再次发起重连和注册
     *
     * ret 0 正常不变  1需要重连
     */
    public  int checkOffline(){
        if(!this.isOnline()){
            return 0;
        }
        long span=System.currentTimeMillis()- lastHeartBeatTime;
        if(( lastHeartBeatTime!=0L && span>rosHeartBeatTimeout*1000 )  || this.robotInfo.getOffline_warn()==1){
            changeToOffline();
            logger.info(" ********************************************************");
            logger.info(" ***************** SET TO OFFLINE ***********************");
            logger.info(" ********************************************************");
            return 1;
        }
        return 0;
    }

    /**
     * 检查上线：收到机器人状态 在线标志==0
     * ret :1 上线 ;0 离线
     */
    public  int checkOnline(RobotInfo info){
        if(!online){
            if(info.getOffline_warn()==0){
                setOnline(true);
                logger.info(" ********************************************************");
                logger.info(" ******************SET ROBOT ONLINE *********************");
                logger.info(" ********************************************************");
                SentDataSyncEvent sentDataSyncEvent= new SentDataSyncEvent(robotId);
                DataSynThread.addEvent(sentDataSyncEvent);
                return 1;
            }else{
                return 0;
            }
        }
        return 1;
    }

    int checkModeTimes=0;
    public void checkMode(){
        if(userController==null && (robotMode==1  || (robotMode==0 && emergency==1) )  ){
            checkModeTimes++;
            if(checkModeTimes==10) {
                if(robotMode==1){
                    logger.info("没有人后台控制机器人，而机器人状态是后台遥控，校正");
                    ModeAndMoveControl.releaseControl(this);
                }
                if(getEmergency()==1){
                    logger.info("没有人后台紧急模式控制机器人，而机器人任务状态是紧急控制，校正");
                    TaskControl.setTaskMode(this,0);
                }
                setController(null,null);
                checkModeTimes=0;
            }
        }else{
            checkModeTimes=0;
        }
    }

    /**
     * 是否可以执行业务步骤(不含充电相关)
     *
     * @return
     */
    public boolean canDoService(){
        //告警状态不允许做任务
        if(!online || waring || stopped){
            return false;
        }
        if(this.robotMode !=0){
            return false;
        }
        if(navLost){
            return false;
        }
        return true;
    }


    /**
     * 可以启动新任务(例如插入立即执行任务和地图任务).供前端启动任务时判断调用
     * @return
     */
    public boolean canStartTask(){
        if(!online || waring || stopped){
            return false;
        }
        if(robotMode==1  ||  robotMode==2){
            return false;
        }
        if(charging==1){
            int atLeastBattery=DataCache.getSysParamInt("robot.atLeastBattery");
            if(robotInfo.getBattery_quantity()>=atLeastBattery){
                return true;
            }
            else{
                return false;
            }
        }
        if(chargeFlowStatus==1 ){
            return false;
        }
        if(navLost){
            return false;
        }
        return true;
    }
}

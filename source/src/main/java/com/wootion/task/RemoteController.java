package com.wootion.task;

import com.alibaba.fastjson.JSONObject;
import com.wootion.agvrobot.utils.NumberUtil;
import com.wootion.commons.MSG_TYPE;
import com.wootion.commons.Result;
import com.wootion.model.UserInfo;
import com.wootion.robot.MemRobot;
import com.wootion.robot.MemUtil;
import com.wootion.task.event.RemoteControlTimeOutEvent;
import com.wootion.task.event.RobotControlEvent;
import com.wootion.taskmanager.TaskControl;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.wootion.commons.Constans.MSG_TYPE_RESP;

public class RemoteController {
    private static final Logger logger = LoggerFactory.getLogger(RemoteController.class);
    private  boolean cancel=false;
    /**
     * 手柄相关消息处理
     * 总体过程：1. 后台主动获得机器人控制权，即被哪个客户端网页获得控制权; 2：后台获得远程遥控权限。3. 开始远程遥控。
     * 紧急定位模式也要设置 controller，保证当前客户端在控制
     * 前端页面刷新，即释放监控和遥控
     */
    protected void robotControlTask(Object evt) {
        if (evt instanceof RobotControlEvent) {
            RobotControlEvent robotControlEvent = (RobotControlEvent) evt;
            //added by btrmg for clear monitor chanel
            if (robotControlEvent.getMsgtype() == MSG_TYPE.MT_RELEASE_MONITOR ) { // release monitor
                MemRobot memRobot1 = MemUtil.queryRobot(robotControlEvent.getChannel());
                releaseMonitorRobot(robotControlEvent,memRobot1);
                return;
            }
            MemRobot memRobot = MemUtil.queryRobot(robotControlEvent.getRobotIp());
            if (memRobot == null) {
                logger.info("robotControlEvent failed, memRobot is null");
                return;
            }
            memRobot.setLastRemoteControlTime( (new java.util.Date()).getTime());
            if (robotControlEvent.getMsgtype() == MSG_TYPE.MT_MONITOR) { // monitor
                monitorRobot(robotControlEvent,memRobot);
                return;
            }
            if (robotControlEvent.getMsgtype() == MSG_TYPE.MT_RELEASE_MONITOR) { // release monitor
                releaseMonitorRobot(robotControlEvent,memRobot);
                return;
            }
            if (robotControlEvent.getMsgtype() == MSG_TYPE.MT_GET_CONTROL) {
                getRemoteControl(robotControlEvent,memRobot);
                return ;
            }
            if(robotControlEvent.getMsgtype() == MSG_TYPE.MT_GET_EMERGENCY){
                emergencyRobot(robotControlEvent,memRobot);
                return;
            }
            if (robotControlEvent.getMsgtype() == MSG_TYPE.MT_GET_TASK_MODE) {
                enterTaskMode(robotControlEvent,memRobot);
                return;
            }
            if(robotControlEvent.getMsgtype() == MSG_TYPE.MT_RELEASE_ALL){
                releaseAll(robotControlEvent,memRobot);
                return;
            }

            //如果当前状态不是远程遥控，则不允许发送机器人移动和云台控制请求
            if(memRobot.getRobotMode()!=1){
                return ;
            }
            if (robotControlEvent.getMsgtype() == MSG_TYPE.MT_CONTROL_FORWARD) {
                speedUp(memRobot,MSG_TYPE.MT_CONTROL_FORWARD.getValue() + MSG_TYPE_RESP,1,
                        robotControlEvent.getSpeedValue().floatValue(),0.0f,robotControlEvent.getChannel());
            }else if (robotControlEvent.getMsgtype() == MSG_TYPE.MT_CONTROL_BACKWARD) {
                speedUp(memRobot,MSG_TYPE.MT_CONTROL_BACKWARD.getValue() + MSG_TYPE_RESP,-1,
                        robotControlEvent.getSpeedValue().floatValue(),0.0f,robotControlEvent.getChannel());
            }else if (robotControlEvent.getMsgtype() == MSG_TYPE.MT_CONTROL_LEFT) {
                rotate(memRobot, MSG_TYPE.MT_CONTROL_LEFT.getValue() + MSG_TYPE_RESP,-1,robotControlEvent.getChannel());
            }else if (robotControlEvent.getMsgtype() == MSG_TYPE.MT_CONTROL_RIGHT) {
                rotate(memRobot, MSG_TYPE.MT_CONTROL_LEFT.getValue() + MSG_TYPE_RESP,1,robotControlEvent.getChannel());
            }else if (robotControlEvent.getMsgtype() == MSG_TYPE.MT_CONTROL_STOP) {
                logger.debug("send stop move cmd");
                stop(memRobot, MSG_TYPE.MT_CONTROL_STOP.getValue() + MSG_TYPE_RESP,robotControlEvent.getChannel());
            }
        }
    }

    /**
     * 从0加速
     * @param memRobot
     * @param msgType
     * @param foreward
     * @param speed
     * @param yaw
     * @param clientCh
     */
    private void speedUp(MemRobot memRobot,int msgType,int foreward,float speed,float yaw,Channel clientCh){
        JSONObject json = new JSONObject();
        json.put("msgtype", msgType);
        json.put("speed", speed);
        double curSpeed=memRobot.getRobotInfo().getVelocity_x();
        logger.info("curSpeed"+curSpeed);
        ModeAndMoveControl.sendMoveCmd(memRobot,speed*foreward,yaw);
        json.put("result", "success");
        clientCh.writeAndFlush(new TextWebSocketFrame(json.toString()));
    }


    private void stop(MemRobot memRobot,int msgType,Channel clientCh){
        if(!NumberUtil.doubleEquals(memRobot.getRobotInfo().getVelocity_x(),0.0) ){
            this.stopRun(memRobot,msgType,clientCh);
        }else{
            this.stopRotate(memRobot,msgType,clientCh);
        }
    }
    private void stopRun(MemRobot memRobot,int msgType,Channel clientCh){
        JSONObject json = new JSONObject();
        json.put("msgtype", msgType);
        json.put("speed", 0);
        float curSpeed=memRobot.getRobotInfo().getVelocity_x();
        logger.debug("stopRun curSpeed="+curSpeed);
        ModeAndMoveControl.sendMoveCmd(memRobot,(float)0.0,0);
        json.put("result", "success");
        clientCh.writeAndFlush(new TextWebSocketFrame(json.toString()));
    }



    private void stopRotate(MemRobot memRobot,int msgType,Channel clientCh){
        JSONObject json = new JSONObject();
        json.put("msgtype", msgType);
        json.put("speed", 0);
        ModeAndMoveControl.sendMoveCmd(memRobot,0,0.0f);
        json.put("result", "success");
        clientCh.writeAndFlush(new TextWebSocketFrame(json.toString()));
    }

    private void sleep(int millis){
        try{
            Thread.sleep(millis);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void rotate(MemRobot memRobot,int msgType,int right,Channel clientCh){
        JSONObject json = new JSONObject();
        json.put("msgtype", msgType);
        json.put("speed", 0);
        float maxSpeed=(float)Math.PI/10;
        ModeAndMoveControl.sendMoveCmd(memRobot,0,(float)maxSpeed*right);
        json.put("result", "success");
        clientCh.writeAndFlush(new TextWebSocketFrame(json.toString()));
    }

    private void monitorRobot(RobotControlEvent robotControlEvent,MemRobot memRobot)
    {
        JSONObject json = new JSONObject();
        json.put("msgtype", MSG_TYPE.MT_MONITOR.getValue() + MSG_TYPE_RESP);
        json.put("result", "success");
        MemRobot oldRobot = MemUtil.queryRobot(robotControlEvent.getChannel());
        if (oldRobot != null) {
            //释放当前客户端监控的机器人
            logger.info("add task remove monitor for " + oldRobot.getRobotIp());
            EventQueue.addTask(new RobotControlEvent(MSG_TYPE.MT_RELEASE_MONITOR,
                    robotControlEvent.getChannel(), 0.0, oldRobot.getRobotIp()));
            EventQueue.addTask(new RobotControlEvent(MSG_TYPE.MT_RELEASE_ALL,
                    robotControlEvent.getChannel(), 0.0, oldRobot.getRobotIp()));

        }
        memRobot.addMonitorChannel(robotControlEvent.getChannel());
        robotControlEvent.getChannel().writeAndFlush(new TextWebSocketFrame(json.toString()));
    }

    /**
     * 注意：此函数是监控，不是后台遥控
     * @param event
     * @param memRobot
     */
    private void releaseMonitorRobot(RobotControlEvent event,MemRobot memRobot){
        JSONObject json = new JSONObject();
        json.put("msgtype", MSG_TYPE.MT_RELEASE_MONITOR.getValue() + MSG_TYPE_RESP);
        json.put("result", "success");
        memRobot.removeMonitorChannel(event.getChannel());
        event.getChannel().writeAndFlush(new TextWebSocketFrame(json.toString()));
    }

    private boolean isSameClient(Channel oldChannel,Channel newChannel){
        if(oldChannel==null ||newChannel==null){
            return false;
        }
        if(oldChannel.remoteAddress()==newChannel.remoteAddress()) {
           return true;
        }
        return false;
    }

    /**
     * 不能避免同一个ip上多个窗口登录
     * @param oldChannel
     * @param newChannel
     * @return
     */
    private boolean isSameIp(Channel oldChannel,Channel newChannel){
        if(oldChannel==null ||newChannel==null){
            return false;
        }
        String  oldIp=oldChannel.remoteAddress().toString();
        oldIp=oldIp.substring(1,oldIp.indexOf(":"));
        String  newIp=newChannel.remoteAddress().toString();
        newIp=newIp.substring(1,newIp.indexOf(":"));
        if(oldIp.equals(newIp)) {
            return true;
        }
        return false;
    }

    /**
     *  任务模式，紧急模式===》后台遥控模式
     * @param event
     * @param memRobot
     */
    private  void getRemoteControl(RobotControlEvent event,MemRobot memRobot){
        JSONObject json = new JSONObject();
        json.put("msgtype", MSG_TYPE.MT_GET_CONTROL.getValue() + MSG_TYPE_RESP);
        if(memRobot.getRobotMode()==2){
            json.put("result", "failed");
            json.put("msg", "机器人为手柄遥控状态");
            json.put("code", "-2");
            event.getChannel().writeAndFlush(new TextWebSocketFrame(json.toString()));
            return ;
        }
        UserInfo oldUserInfo=memRobot.getUserController();
        if(oldUserInfo!=null && memRobot.getRobotMode()==1){
            //机器人处于后台遥控模式
            json.put("result", "failed");
            if(oldUserInfo.getUid().equals(event.getUserInfo().getUid())){
                json.put("msg", "你正在远程遥控该机器人，不用重复发起控制请求!");
                json.put("code", "0");
                event.getChannel().writeAndFlush(new TextWebSocketFrame(json.toString()));
                return ;
            }else{
                if(event.getIsForced()==1 ){
                    //强制释放其他客户端
                    //通知对方端，被强制释放了
                    if(memRobot.getController()!=null){
                        json.put("result", "success");
                        json.put("msg", "你的远程遥控权被其他客户端抢占。");
                        json.put("code", "1");
                        memRobot.getController().writeAndFlush(new TextWebSocketFrame(json.toString()));
                        try{
                            Thread.sleep(500);
                            memRobot.setController(null,null);
                        }catch (Exception e){
                        }
                    }
                }else{
                    json.put("msg", "其他客户端正在远程遥控该机器人!");
                    json.put("code", "-1");
                    event.getChannel().writeAndFlush(new TextWebSocketFrame(json.toString()));
                    return ;
                }
            }
        }

        if(oldUserInfo!=null && memRobot.getEmergency()==1){
            if(oldUserInfo.getUid().equals(event.getUserInfo().getUid())){
                //通知对方端，被强制释放了
                //memRobot.setEmergency(0);
                json.put("result", "success");
                json.put("msg", "其他客户端申请远程遥控，你的客户端紧急模式已经被强制退出。");
                json.put("code", "1");
                memRobot.getController().writeAndFlush(new TextWebSocketFrame(json.toString()));
                try{
                    Thread.sleep(500);
                    memRobot.setController(null,null);
                }catch (Exception e){
                }
            }
        }
        try{
            //memRobot.setEmergency(0);
            TaskControl.setTaskMode(memRobot,0);
            int ret=ModeAndMoveControl.getRemoteControl(memRobot);
            json.put("code", ret);
            if(ret==1){
                json = new JSONObject();
                json.put("speed", memRobot.getCurVel());
                json.put("result", "success");
                json.put("msg", "获得远程遥控成功。");
                memRobot.setController(event.getUserInfo(),event.getChannel());
                json.put("msgtype", MSG_TYPE.MT_GET_CONTROL.getValue() + MSG_TYPE_RESP);
                event.getChannel().writeAndFlush(new TextWebSocketFrame(json.toString()));
                return ;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }
        json = new JSONObject();
        json.put("speed", memRobot.getCurVel());
        json.put("result", "faireleaseRemoteControlled");
        json.put("msg", "获得远程遥控失败。");
        memRobot.setController(null,null);
        json.put("msgtype", MSG_TYPE.MT_GET_CONTROL.getValue() + MSG_TYPE_RESP);
        event.getChannel().writeAndFlush(new TextWebSocketFrame(json.toString()));
    }

    //进入任务模式
    private void enterTaskMode(RobotControlEvent event, MemRobot memRobot) {
        JSONObject json = new JSONObject();
        json.put("msgtype", MSG_TYPE.MT_GET_TASK_MODE.getValue() + MSG_TYPE_RESP);
        json.put("speed", memRobot.getCurVel());
        UserInfo userInfo = memRobot.getUserController();
        if (memRobot.getRobotMode() == 2) {
            json.put("result", "failed");
            json.put("msg", "机器人为手柄遥控状态");
            json.put("code", "-2");
            event.getChannel().writeAndFlush(new TextWebSocketFrame(json.toString()));
            return;
        }
        if (memRobot.getRobotMode() == 0 && memRobot.getEmergency() == 0) {
            json.put("result", "false");
            json.put("msg", "当前是任务模式，不需要释放。");
            json.put("code", "0");
            event.getChannel().writeAndFlush(new TextWebSocketFrame(json.toString()));
            return;
        }
        if (!userInfo.getUid().equals(event.getUserInfo().getUid())) {
            json.put("result", "false");
            json.put("msg", "其他客户端正在远程遥控机器人。");
            json.put("code", "-1");
            event.getChannel().writeAndFlush(new TextWebSocketFrame(json.toString()));
            return;
        }
        //如果为本人
        if (memRobot.getRobotMode() == 1) {
            int result = ModeAndMoveControl.releaseControl(memRobot);
            if (result == 1) {
                json.put("result", "success");
                json.put("msg", "释放远程遥控成功，进入任务模式。");
                memRobot.setController(null, null);
                json.put("code", "1");
            } else {
                json.put("result", "failed");
                json.put("msg", "释放远程遥控失败。");
                json.put("code", "-1");
            }
        }else{
            Result result= TaskControl.setTaskMode(memRobot,0);
            if (result.getCode() == 0) {
                json.put("result", "success");
                json.put("msg", "释放紧急模式成功，进入任务模式。");
                memRobot.setController(null, null);
                json.put("code", "1");
            } else {
                json.put("result", "failed");
                json.put("msg", "释放紧急模式失败。");
                json.put("code", "-1");
            }
        }
        event.getChannel().writeAndFlush(new TextWebSocketFrame(json.toString()));

        return;
    }

    protected synchronized void remoteControlTimeOut(Object evt) {
        RemoteControlTimeOutEvent event=(RemoteControlTimeOutEvent)evt;
        MemRobot memRobot = MemUtil.queryRobot(event.getRobotIp());
        if (memRobot == null) {
            logger.info("remoteControlTimeOut failed, memRobot is null");
            return;
        }
        if( memRobot.getRobotMode()!=1) {
            return ;
        }
        JSONObject json = new JSONObject();
        json.put("msgtype", MSG_TYPE.MT_RELEASE_ALL.getValue() + MSG_TYPE_RESP);
        json.put("speed", memRobot.getCurVel());
        if(memRobot.getEmergency()==1){
            TaskControl.setTaskMode(memRobot,0);
        }
        if(memRobot.getRobotMode()==1){
            ModeAndMoveControl.releaseControl(memRobot);
        }
        memRobot.setController(null,null);
        json.put("result", "success");
        json.put("msg", "由于长时间无操作，被服务器释放远程控制权限.进入任务模式。");
        json.put("code", "1");
        memRobot.setController(null);
        memRobot.push(json);
    }

    // 进入紧急模式：从任务模式到紧急模式
    private void emergencyRobot(RobotControlEvent event,MemRobot memRobot) {
        JSONObject json = new JSONObject();
        json.put("msgtype", MSG_TYPE.MT_GET_EMERGENCY.getValue() + MSG_TYPE_RESP);
        if(memRobot.getRobotMode()==2 ){
            json.put("msgtype", MSG_TYPE.MT_GET_EMERGENCY.getValue() + MSG_TYPE_RESP);
            json.put("result", "failed");
            json.put("msg", "机器人当前为手柄控制，不能切换到紧急定位模式!");
            json.put("code", "-2");
            event.getChannel().writeAndFlush(new TextWebSocketFrame(json.toString()));
            return ;
        }
        UserInfo userInfo=memRobot.getUserController();
        if(memRobot.getRobotMode()==1){
            if(!userInfo.getUid().equals(event.getUserInfo().getUid())){
                json.put("msgtype", MSG_TYPE.MT_GET_EMERGENCY.getValue() + MSG_TYPE_RESP);
                json.put("result", "failed");
                json.put("msg", "机器人处于他人后台遥控模式，不能切换到紧急定位模式!");
                json.put("code", "-1");
                event.getChannel().writeAndFlush(new TextWebSocketFrame(json.toString()));
                return ;
            }
        }
        if (memRobot.getEmergency() == 1) {
            json.put("msgtype", MSG_TYPE.MT_GET_EMERGENCY.getValue() + MSG_TYPE_RESP);
            json.put("result", "failed");
            if(!userInfo.getUid().equals(event.getUserInfo().getUid())){
                json.put("msg", "你正在紧急控制该机器人，不用重复发起控制请求!");
                json.put("code", "0");
                event.getChannel().writeAndFlush(new TextWebSocketFrame(json.toString()));
                return ;
            }else{
                if(event.getIsForced()==1 ){
                    //强制释放其他客户端
                    //通知对方端，被强制释放了
                    if(memRobot.getController()!=null){
                        Result result= TaskControl.setTaskMode(memRobot,0);
                        if(result.getCode()==0){
                            json.put("result", "success");
                            json.put("msg", "你的紧急控制权被其他客户端抢占。");
                            json.put("code", "1");
                            memRobot.getController().writeAndFlush(new TextWebSocketFrame(json.toString()));
                            try{
                                Thread.sleep(500);
                            }catch (Exception e){
                            }
                        }else{
                            json.put("result", "failed");
                            json.put("msg", "强制释放他人的紧急控制权失败。");
                            json.put("code", "-1");
                            memRobot.getController().writeAndFlush(new TextWebSocketFrame(json.toString()));
                            return;
                        }
                    }
                }else{
                    json.put("msg", "其他客户端正在紧急控制该机器人!");
                    json.put("code", "-1");
                    event.getChannel().writeAndFlush(new TextWebSocketFrame(json.toString()));
                    return ;
                }
            }
        }
        Result result= TaskControl.setTaskMode(memRobot,1);
        if(result.getCode()==0){
            memRobot.setController(event.getUserInfo(),event.getChannel());
            json.put("result", "success");
            json.put("msg", "切换到紧急定位模式成功。");
            json.put("code", "1");
        }else{
            json.put("result", "failed");
            json.put("msg", "切换到紧急定位模式失败。");
            json.put("code", "-1");
        }
        event.getChannel().writeAndFlush(new TextWebSocketFrame(json.toString()));
    }


    /**
     * 页面离开释放控制
     * @param robotControlEvent
     * @param memRobot
     */
    private void releaseAll(RobotControlEvent robotControlEvent,MemRobot memRobot) {
        UserInfo userInfo=memRobot.getUserController();
        if(userInfo==null ){
            return ;
        }
        if(memRobot.getRobotMode()==2 ){
            return ;
        }
        if(!isSameIp(memRobot.getController(),robotControlEvent.getChannel())){
            logger.debug("当前不是 "+userInfo.getLoginname()+" 控制，不能释放控制");
            return ;
        }
        if(memRobot.getEmergency()==1){
           TaskControl.setTaskMode(memRobot,0);
        }
        if(memRobot.getRobotMode()==1){
           ModeAndMoveControl.releaseControl(memRobot);
        }
        memRobot.setController(null,null);
        logger.debug("自动释放紧急模式或者后台遥控模式，进入任务模式");
    }

}

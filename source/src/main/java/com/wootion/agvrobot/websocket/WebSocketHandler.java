package com.wootion.agvrobot.websocket;

import com.alibaba.fastjson.JSONObject;
import com.wootion.commons.MSG_TYPE;
import com.wootion.model.UserInfo;
import com.wootion.robot.MemRobot;
import com.wootion.robot.MemUtil;
import com.wootion.task.EventQueue;
import com.wootion.task.event.AddPtzSetEvent;
import com.wootion.task.event.CameraControlEvent;
import com.wootion.task.event.RobotControlEvent;
import com.wootion.task.event.TerraceControlEvent;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WebSocketHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final Channel ch = ctx.channel();
        if (msg instanceof WebSocketFrame) {
            if (msg instanceof BinaryWebSocketFrame) {
                System.out.println("BinaryWebSocketFrame Received : ");
                System.out.println(((BinaryWebSocketFrame) msg).content());
            } else if (msg instanceof TextWebSocketFrame) {
                JSONObject jobj = JSONObject.parseObject(((TextWebSocketFrame) msg).text());
                if (jobj.containsKey("msgtype")){
                    MemRobot memRobot = MemUtil.queryRobot(ctx.channel());
                    int msgType = jobj.getIntValue("msgtype");
                    if (msgType == 1) {
                        if (!jobj.containsKey("robotIp")) {
                            //JSONObject resp = JSONObject.fromObject(jobj).accumulate("result", -1);
                            jobj.put("result", -1);
                            JSONObject resp =jobj;
                            ctx.channel().writeAndFlush(new TextWebSocketFrame(resp.toString()));
                            return ;
                        }
                        String robotIp = jobj.getString("robotIp");
                        EventQueue.addTask(new RobotControlEvent(MSG_TYPE.MT_MONITOR, ctx.channel(), 0.0, robotIp));
                    } else if (msgType == 3 || msgType == 4|| msgType == 5 || msgType == 6 ) {
                        JSONObject jsonUser = (JSONObject) jobj.get("userInfo");
                        UserInfo userInfo =JSONObject.toJavaObject(jsonUser,UserInfo.class);
                        int isForced = jobj.getIntValue("isForced");
                        MSG_TYPE msg_type;
                        if(msgType==3){
                            msg_type= MSG_TYPE.MT_GET_CONTROL;
                        }else  if(msgType==4){
                            msg_type= MSG_TYPE.MT_GET_TASK_MODE;
                        }else  if(msgType==5){
                            msg_type= MSG_TYPE.MT_GET_EMERGENCY;
                        }else{
                            msg_type= MSG_TYPE.MT_RELEASE_ALL;
                        }
                        RobotControlEvent rcEvent=new RobotControlEvent(msg_type, ctx.channel(), 0.0, memRobot.getRobotIp());
                        rcEvent.setIsForced(isForced);
                        rcEvent.setUserInfo(userInfo);
                        EventQueue.addTask(rcEvent);
                    }else if (msgType == MSG_TYPE.MT_CONTROL_FORWARD.getValue()) {
                        double speed = jobj.getDouble("speed");
                        EventQueue.addTask(new RobotControlEvent(MSG_TYPE.MT_CONTROL_FORWARD, ctx.channel(), speed, memRobot.getRobotIp()));
                    } else if (msgType == MSG_TYPE.MT_CONTROL_BACKWARD.getValue()) {
                        double speed = jobj.getDouble("speed");
                        EventQueue.addTask(new RobotControlEvent(MSG_TYPE.MT_CONTROL_BACKWARD, ctx.channel(), speed, memRobot.getRobotIp()));
                    } else if (msgType == MSG_TYPE.MT_CONTROL_LEFT.getValue()) {
                        double speed = jobj.getDouble("speed");
                        EventQueue.addTask(new RobotControlEvent(MSG_TYPE.MT_CONTROL_LEFT, ctx.channel(), speed, memRobot.getRobotIp()));
                    } else if (msgType == MSG_TYPE.MT_CONTROL_RIGHT.getValue()) {
                        double speed = jobj.getDouble("speed");
                        EventQueue.addTask(new RobotControlEvent(MSG_TYPE.MT_CONTROL_RIGHT, ctx.channel(), speed, memRobot.getRobotIp()));
                    } else if (msgType == MSG_TYPE.MT_CONTROL_STOP.getValue()) {
                        double speed = 0.0;
                        EventQueue.addTask(new RobotControlEvent(MSG_TYPE.MT_CONTROL_STOP, ctx.channel(), speed, memRobot.getRobotIp()));
                    }else if (msgType == MSG_TYPE.MT_CONTROL_TERRACE.getValue()) {
                        String cmd = jobj.getString("cmd");
                        String data = jobj.getString("data");
                        EventQueue.addTask(new TerraceControlEvent(ctx.channel(),memRobot.getRobotIp(),cmd,data));

                    }else if (msgType == MSG_TYPE.MT_CONTROL_CAMERA.getValue()) {
                        String cmd = jobj.getString("cmd");
                        String data = jobj.getString("data");
                        EventQueue.addTask(new CameraControlEvent(ctx.channel(),memRobot.getRobotIp(),cmd,data));

                    }else if (msgType == 16){
                        String runMarkName = jobj.getString("runMarkName");
                        String ptzType = jobj.getString("ptzType");
                        String devName = null;
                        String regzSpotId ;
                        String devId ;
                        regzSpotId = jobj.getString("regzSpotId");
                        devId = jobj.getString("devId");
                        String ptzSetId = jobj.getString("ptzSetId");
                        EventQueue.addTask(new AddPtzSetEvent(runMarkName,devId,ptzType,ptzSetId,regzSpotId,ctx.channel()));
                    }
                }

            } else if (msg instanceof PingWebSocketFrame) {
                System.out.println("PingWebSocketFrame Received : ");
                System.out.println(((PingWebSocketFrame) msg).content());
            } else if (msg instanceof PongWebSocketFrame) {
                System.out.println("PongWebSocketFrame Received : ");
                System.out.println(((PongWebSocketFrame) msg).content());
            } else if (msg instanceof CloseWebSocketFrame) {
                System.out.println("CloseWebSocketFrame Received : ");
                System.out.println("ReasonText :" + ((CloseWebSocketFrame) msg).reasonText());
                System.out.println("StatusCode : " + ((CloseWebSocketFrame) msg).statusCode());
                MemRobot memRobot = MemUtil.queryRobot(ctx.channel());
                String robotIp=null;
                if(memRobot!=null){
                    robotIp=memRobot.getRobotIp();
                }
                EventQueue.addTask(new RobotControlEvent(MSG_TYPE.MT_RELEASE_MONITOR, ctx.channel(), 0.0,robotIp));
                EventQueue.addTask(new RobotControlEvent(MSG_TYPE.MT_RELEASE_ALL, ctx.channel(), 0.0,robotIp));
                ch.close(); // majunhui add
            } else {
                System.out.println("Unsupported WebSocketFrame");
            }


        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println("except: " + cause.getMessage());
    }


}

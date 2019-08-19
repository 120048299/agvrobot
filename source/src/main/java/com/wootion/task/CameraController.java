package com.wootion.task;

import com.alibaba.fastjson.JSONObject;
import com.wootion.commons.MSG_TYPE;
import com.wootion.commons.Result;
import com.wootion.protocols.robot.GeneralPublish;
import com.wootion.protocols.robot.msg.GeneralAckMsg;
import com.wootion.protocols.robot.msg.MsgNames;
import com.wootion.robot.MemRobot;
import com.wootion.robot.MemUtil;
import com.wootion.task.event.CameraControlEvent;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.json.JsonObjectDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.wootion.commons.Constans.MSG_TYPE_RESP;

public class CameraController {
    private static final Logger logger = LoggerFactory.getLogger(CameraController.class);

    /**
     * 后台控制云台
     */
    protected void doControlTask(Object evt) {
        CameraControlEvent event= (CameraControlEvent) evt;
        MemRobot memRobot = MemUtil.queryRobot(event.getRobotIp());
        if (memRobot == null) {
            logger.info("CameraControlEvent failed, memRobot is null");
            return;
        }
        memRobot = MemUtil.queryRobot(event.getChannel());
        if (memRobot == null) {
            logger.info("CameraControlEvent: no monitor for robot before");
            return;
        }

        if(event.getCmd().equals("readCameraStatus")){
             readCameraStatus(memRobot,event);
             return ;
        }
        Result result=GeneralPublish.publish(memRobot.getCh(),MsgNames.node_camera, MsgNames.topic_camera_command,event.getCmd(),event.getData());
        //返回消息给web客户端
        JSONObject json = new JSONObject();
        json.put("msgtype", MSG_TYPE.MT_CONTROL_CAMERA.getValue() + MSG_TYPE_RESP);
        json.put("cmd", event.getCmd());//把发送的命令返回到客户端，以让客户端知道先前发送的命令是什么
        //json.put("result", result);//result在这个接口 只是命令名称加后缀_ack，无用
        if(result.getCode()==1){
            json.put("data", "success");
        }else{
            json.put("data", "failed");
        }
        event.getChannel().writeAndFlush(new TextWebSocketFrame(json.toString()));
    }


    /**
     * read camera  param
     * 三个数值读完后再一起返回给客户端
     * @param event
     */
    private void readCameraStatus (MemRobot memRobot,CameraControlEvent event){
        String minFocusDistance = "failed";
        String wdrMode  = "failed";
        String wdrLevel = "failed";
        Result result=cameraAction(memRobot,"get_min_focus_distance","");
        if(result.getCode()==1 ){
            //处理接口返回的结果
            Object obj=result.getData();
            if(obj instanceof Result){

            }
            GeneralAckMsg resultMsg=(GeneralAckMsg)result.getData();
            System.out.println(resultMsg);
            minFocusDistance=resultMsg.getData();
        }
        result=cameraAction(memRobot,"get_wdr_mode","");
        if(result.getCode()==1 ){
            //处理接口返回的结果
            GeneralAckMsg resultMsg=(GeneralAckMsg)result.getData();
            System.out.println(resultMsg);
            wdrMode=resultMsg.getData();
        }
        result=cameraAction(memRobot,"get_wdr_level","");
        if(result.getCode()==1 ){
            //处理接口返回的结果
            GeneralAckMsg resultMsg=(GeneralAckMsg)result.getData();
            System.out.println(resultMsg);
            wdrLevel=resultMsg.getData();
        }
        //返回消息给web客户端
        JSONObject json = new JSONObject();
        json.put("msgtype", MSG_TYPE.MT_CONTROL_CAMERA.getValue() + MSG_TYPE_RESP);
        json.put("cmd", "readCameraStatus");//把发送的命令返回到客户端，以让客户端知道先前发送的命令是什么
        json.put("minFocusDistance", minFocusDistance);
        json.put("wdrMode", wdrMode);
        json.put("wdrLevel", wdrLevel);
        event.getChannel().writeAndFlush(new TextWebSocketFrame(json.toString()));
    }


    /**
     * 相机指令 :需要等待结果,读取或者执行结果
     * camera_control 接口
     */
    public Result cameraAction(MemRobot memRobot, String cmd, String param) {
        if (memRobot == null ||memRobot.getCh()==null) {
            logger.info("terraceControlEvent failed, memRobot is null");
            return new Result(-1,"memRobot or ch is null ",null);
        }
        Result result=GeneralPublish.publishTopic(memRobot.getCh(),MsgNames.node_camera, MsgNames.topic_camera_command,cmd,param,5);
        return result;
    }

    public double getZoomLevel(MemRobot memRobot){
        try{
            Result result=cameraAction(memRobot,"get_zoom_level",null);
            if(result.getCode()==1){
                return Double.parseDouble((String)result.getData());
            }else
            {
                return 0;
            }
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }

    }
}

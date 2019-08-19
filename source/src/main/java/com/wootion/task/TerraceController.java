package com.wootion.task;

import com.alibaba.fastjson.JSONObject;
import com.wootion.commons.MSG_TYPE;
import com.wootion.commons.Result;
import com.wootion.protocols.robot.GeneralPublish;
import com.wootion.protocols.robot.msg.MsgNames;
import com.wootion.robot.MemRobot;
import com.wootion.robot.MemUtil;
import com.wootion.task.event.TerraceControlEvent;
import com.wootion.utiles.ResultUtil;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.wootion.commons.Constans.MSG_TYPE_RESP;

public class TerraceController {
    private static final Logger logger = LoggerFactory.getLogger(TerraceController.class);

    public double getPanAngle(String robotIp){
        Result result=terraceAction(robotIp,"get_pan_angle",null);
        if(result.getCode()==0){
            try{
                int angle=Integer.valueOf((String)result.getData());
                return angle/100.0;
            }catch (Exception e){
                e.printStackTrace();
                return 0;
            }

        }else
        {
            return 0;
        }
    }

    public double getTiltAngle(String robotIp){
        Result result=terraceAction(robotIp,"get_tilt_angle",null);
        if(result.getCode()==0){
            int angle=Integer.valueOf((String)result.getData());
            return angle/100.0;
        }else
        {
            return 0;
        }
    }

    /**
     * 后台控制云台 :需要等待结果,读取或者执行结果
     * terrace_control 接口
     */
    public Result terraceAction(String robotIp, String cmd, String param) {
        MemRobot memRobot = MemUtil.queryRobot(robotIp);
        if (memRobot == null) {
            logger.info("terraceControlEvent failed, memRobot is null");
            return ResultUtil.failed();
        }
        if (memRobot == null ||memRobot.getCh()==null) {
            logger.info("terraceControlEvent failed, memRobot is null");
            return new Result(-1,"memRobot or ch is null ",null);
        }
        Result result= GeneralPublish.publishTopic(memRobot.getCh(), MsgNames.node_clound_terrace,MsgNames.topic_terrace_command,cmd,param,5);
        return result;
    }

    /**
     * 后台控制云台 不等待结果
     * @return
     */
    public void sendTerraceCmd(Object evt) {
        TerraceControlEvent event = (TerraceControlEvent) evt;
        MemRobot memRobot = MemUtil.queryRobot(event.getRobotIp());
        if (memRobot == null) {
            logger.info("terraceControlEvent failed, memRobot is null");
            return;
        }
        memRobot = MemUtil.queryRobot(event.getChannel());
        if (memRobot == null) {
            logger.info("terraceControlEvent: no monitor for robot before");
            return;
        }
        Result result= GeneralPublish.publish(memRobot.getCh(), MsgNames.node_clound_terrace,MsgNames.topic_terrace_command,event.getCommand(),event.getData());
        JSONObject jsonRet = new JSONObject();
        jsonRet.put("msgtype", MSG_TYPE.MT_CONTROL_TERRACE.getValue() + MSG_TYPE_RESP);
        if(result.getCode()==1){
            jsonRet.put("result", "success");
        }else{
            jsonRet.put("result", "failed");
        }
        event.getChannel().writeAndFlush(new TextWebSocketFrame(jsonRet.toString()));
    }

}

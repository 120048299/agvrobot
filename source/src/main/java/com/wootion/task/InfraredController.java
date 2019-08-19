package com.wootion.task;

import com.wootion.commons.Result;
import com.wootion.protocols.robot.GeneralPublish;
import com.wootion.protocols.robot.msg.MsgNames;
import com.wootion.robot.MemRobot;
import com.wootion.robot.MemUtil;
import com.wootion.utiles.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfraredController {
    private static final Logger logger = LoggerFactory.getLogger(InfraredController.class);

    /**
     * 红外相机指令 :需要等待结果,读取或者执行结果
     *
     */
    public Result doAction(String robotIp, String cmd, String param) {
        MemRobot memRobot = MemUtil.queryRobot(robotIp);
        if (memRobot == null) {
            logger.info("terraceControlEvent failed, memRobot is null");
            return ResultUtil.failed();
        }
        Result result= GeneralPublish.publishTopic(memRobot.getCh(), MsgNames.node_clound_terrace,MsgNames.topic_terrace_command,cmd,param,5);
        return result;
    }


    /**
     *
     *  这个功能页面好像没有了，可能不要了。如果需要，改写到此类中来
     *
     * else if (msgType == 17){
     String command = jobj.getString("cmd");
     ThermalCommandMsg commandMsg = new ThermalCommandMsg();
     if ("measurePoint".equals(command)) {
     int offsetX = jobj.getIntValue("offsetX");
     int offsetY = jobj.getIntValue("offsetY");
     commandMsg.setTrans_id(MemUtil.newOpId());
     commandMsg.setRobotIp("192.168.1.180");  //暂时没用 如果是多个机器人再改
     commandMsg.setData(offsetX + "," +offsetY);
     commandMsg.setCmd(command);
     }else if ("autoFocus".equals(command)) {
     commandMsg.setTrans_id(MemUtil.newOpId());
     commandMsg.setCmd(command);
     commandMsg.setRobotIp("192.168.1.180");
     }

     ThermalControlEvent thermalControlEvent = new ThermalControlEvent();
     thermalControlEvent.setCommandMsg(commandMsg);
     thermalControlEvent.setSender(ctx.channel());
     EventQueue.addTask(thermalControlEvent);
     }

     //热像仪控制
     if (evt instanceof ThermalControlEvent) {
     JSONObject jsonObject = new JSONObject();
     ThermalControlEvent controlEvent = (ThermalControlEvent) evt;
     try {
     ThermalOp controlOp = new ThermalOp(MemUtil.newOpId(), controlEvent.getCommandMsg());

     ChannelFuture future = MemUtil.queryRobot(controlEvent.getCommandMsg().getRobot_ip()).getCh().writeAndFlush(controlOp);
     future.addListener((f) -> {
     if (!f.isSuccess()) {
     jsonObject.put("msgtype", 10018);
     jsonObject.put("result", "failed");
     jsonObject.put("msg", "send message failed");
     controlEvent.getSender().writeAndFlush(new TextWebSocketFrame(jsonObject.toString()));
     }
     });
     } catch (Exception e) {
     e.printStackTrace();
     jsonObject.put("msgtype", 10018);
     jsonObject.put("result", "failed");
     jsonObject.put("msg", "send message failed");
     controlEvent.getSender().writeAndFlush(new TextWebSocketFrame(jsonObject.toString()));
     }
     continue;
     }
     */


}

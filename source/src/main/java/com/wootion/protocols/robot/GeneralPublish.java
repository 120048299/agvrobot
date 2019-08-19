package com.wootion.protocols.robot;

import com.alibaba.fastjson.JSONObject;
import com.wootion.commons.Result;
import com.wootion.protocols.robot.msg.*;
import com.wootion.protocols.robot.operation.CommonOp;
import com.wootion.robot.MemUtil;
import io.netty.channel.Channel;

/**
 * 统一格式的TOPOC消息发布
 */
public class GeneralPublish {


    /**
     * 通用消息发布，不等待返回
     * @param ch
     * @param reciever
     * @param topic
     * @param cmd
     * @param data
     * @return
     */
    public static Result publish(Channel ch,String reciever,String topic,String cmd, String data){
        GeneralCommandMsg commandMsg= new GeneralCommandMsg();
        commandMsg.setReceiver(reciever);
        commandMsg.setCmd(cmd);
        commandMsg.setData(data);
        int opId=MemUtil.newOpId();
        commandMsg.setTrans_id(opId);
        CommonOp op= new CommonOp(opId,commandMsg, topic);
        RosCommandExcecutor rosCommandExcecutor =new RosCommandExcecutor();
        rosCommandExcecutor.setCh(ch);
        Result result= rosCommandExcecutor.publish(op);
        return result;
    }


    /**
     * 发布topic消息，等待返回消息。返回消息对同一个transId可以多个，直到最后结束。
     *  等待相同的transId返回，如果有transId冲突，不同模块发来的？
     * @param
     * @param
     * @param timeOut
     * @return  msg object
     */
    public static Result publishTopic(Channel ch,String reciever,String topic,String cmd, String data, int timeOut){
        int ret=-1;
        GeneralCommandMsg commandMsg= new GeneralCommandMsg();
        commandMsg.setReceiver(reciever);
        commandMsg.setCmd(cmd);
        commandMsg.setData(data);
        int opId=MemUtil.newOpId();
        commandMsg.setTrans_id(opId);
        CommonOp op= new CommonOp(opId,commandMsg, topic);
        RosCommandExcecutor excecutor=new RosCommandExcecutor();
        excecutor.setCh(ch);
        Result result=excecutor.publish(op,timeOut);
        if(result.getCode()==1){
            Object obj=result.getData();
            GeneralAckMsg generalAckMsg=toGeneralAck((JSONObject)obj);
            result.setData(generalAckMsg);
        }
        return result;
    }

    public static GeneralAckMsg toGeneralAck(JSONObject obj){
        GeneralAckMsg generalAckMsg=new GeneralAckMsg();
        JSONObject msgObj=obj.getJSONObject("msg");
        generalAckMsg.setAck(msgObj.getString("ack"));
        generalAckMsg.setData(msgObj.getString("data"));
        return generalAckMsg;
    }
}

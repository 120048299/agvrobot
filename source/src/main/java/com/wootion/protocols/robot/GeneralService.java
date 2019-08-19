package com.wootion.protocols.robot;

import com.alibaba.fastjson.JSONObject;
import com.wootion.commons.Result;
import com.wootion.protocols.robot.msg.GeneralServiceAckMsg;
import com.wootion.protocols.robot.msg.GeneralServiceReqMsg;
import com.wootion.protocols.robot.msg.MsgNames;
import com.wootion.protocols.robot.msg.Publish;
import com.wootion.robot.MemUtil;
import io.netty.channel.Channel;

public class GeneralService {


    /**
     * 通用服务调用接口
     * @param ch
     * @param receiver
     * @param serviceName
     * @param type
     * @param data
     * @param timeOut
     * @return
     */
    public static Result call(Channel ch, String receiver, String serviceName, String type, String data,int timeOut){
        if(ch==null){
            return new Result(-1,"调用服务接口失败,channel is null",null);
        }
        if(receiver==null || "".equals(receiver)||
                serviceName==null || "".equals(serviceName) ||
                type==null || "".equals(type) ){
            return new Result(-1,"入参不正确，不能为空",null);
        }
        if(timeOut<3){
            return new Result(-1,"入参 超时时间不能小于3秒",null);
        }
        int opId = MemUtil.newOpId();
        GeneralServiceReqMsg reqMsg=new GeneralServiceReqMsg();
        reqMsg.setSender(MsgNames.node_server);
        reqMsg.setReceiver(receiver);
        reqMsg.setTrans_id(opId);
        reqMsg.setType(type);
        reqMsg.setData(data);
        //System.out.println(reqMsg.getData());
        Publish<GeneralServiceReqMsg> op = new Publish<>();
        op.setService(serviceName);
        op.setArgs(reqMsg);
        RosCommandExcecutor ce=new RosCommandExcecutor();
        ce.setCh(ch);
        Result  result=ce.callService(op,timeOut);
        if(result.getCode()!=1){
            return new Result(-1,"调用服务接口失败"+result.getMsg(),result.getData());
        }
        JSONObject jobj=(JSONObject) result.getData();
        GeneralServiceAckMsg ackMsg=toAckMsg(jobj.getJSONObject("values"));
        return new Result(1,"",ackMsg);
    }

    private static GeneralServiceAckMsg toAckMsg(JSONObject jsonObject) {
        GeneralServiceAckMsg msg = new GeneralServiceAckMsg();
        msg.setHeader(jsonObject.getJSONObject("header"));
        msg.setSender(jsonObject.getString("sender"));
        msg.setReceiver(jsonObject.getString("receiver"));
        msg.setTrans_id(jsonObject.getIntValue("trans_id"));
        msg.setRet_code(jsonObject.getString("ret_code"));
        msg.setRet_msg(jsonObject.getString("ret_msg"));
        msg.setData(jsonObject.getString("data"));
        return msg;
    }

}

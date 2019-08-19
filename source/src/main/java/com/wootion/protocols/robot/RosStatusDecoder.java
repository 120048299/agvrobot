package com.wootion.protocols.robot;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wootion.protocols.robot.msg.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class RosStatusDecoder extends MessageToMessageDecoder<TextWebSocketFrame> {
    private static final Logger logger = LoggerFactory.getLogger(RosStatusDecoder.class.toString());

    private Chatter toChatter(JSONObject jobj) {
        Chatter chatter = new Chatter();

        if (jobj.containsKey("id")) {
            chatter.setId(jobj.getString("id"));
        }
        chatter.setMsg(jobj.getJSONObject("msg"));
        chatter.setOp(jobj.getString("op"));
        chatter.setTopic(jobj.getString("topic"));

        return chatter;
    }


    @Override
    protected void decode(ChannelHandlerContext ctx, TextWebSocketFrame msg, List<Object> out) throws Exception {
        //System.out.println("recv Msg: " + msg.text());
        //recv Msg: {"topic": "/heartbeat_ack", "msg": {"orientation": 0.0, "disable_status": 0, "pump_status": 0, "header": {"stamp": {"secs": 1559294209, "nsecs": 216131925}, "frame_id": "", "seq": 5388}, "ptz": [], "velocity_x": 0.0, "wifi_strength": 0, "velocity_yaw": 0.0, "battery_quantity": 0, "battery_current": 0, "battery_voltage": 0, "robot_ip": "10.204.157.231", "stop_status": 0, "wheel_status": 0, "trans_id": 230, "status": 0, "obstacle_status": 0, "light_status": 0, "temperature": [], "sender": "robot_agent", "motor_status": 0, "nav_status": 0, "mode": 0, "battery_status": 0, "receiver": "SVR", "position": [], "sensor_status": 0}, "op": "publish"}
        //recv Msg: {"id": "12345", "values": {"sum": 0}, "result": true, "services": "add_two_ints", "op": "service_response"}
        JSONObject jobj = JSONObject.parseObject(msg.text());
        String op = jobj.getString("op");
        String topic = jobj.getString("topic");
        String status_list = jobj.getString("status_list");
        //  /x.x.x.x:9090
        String remoteRosIp=ctx.channel().remoteAddress().toString();
        String robotIp=remoteRosIp.substring(1,remoteRosIp.indexOf(":"));
        jobj.put("robot_ip",robotIp);
        out.add(jobj);
    }

    private Publish<?> toActionResponse(JSONObject jobj,String robotIp) {
        Publish<?> resp = new Publish<>();
        String topic=jobj.getString("topic");
        String op=jobj.getString("op");
        resp.setTopic(topic);
        resp.setOp(op);
        if(topic.endsWith("/feedback") || topic.endsWith("/result")){
            JSONObject msgObj=jobj.getJSONObject("msg");
            JSONObject obj;
            ActionResponseMsg msg;
            if(topic.endsWith("/feedback")){
                obj=msgObj.getJSONObject("feedback");
                msg=toActionResponseMsg(obj);
                msg.setType(0);
            }else{
                obj=msgObj.getJSONObject("result");
                msg=toActionResponseMsg(obj);
                msg.setType(1);
            }
            resp.setMsg(msg);
        }
        return resp;
    }


    private ActionResponseMsg toActionResponseMsg(JSONObject jsonObject) {
        ActionResponseMsg msg = new ActionResponseMsg();
        msg.setTrans_id(jsonObject.getIntValue("trans_id"));
        msg.setMsgObj(jsonObject);
        return msg;
    }

}
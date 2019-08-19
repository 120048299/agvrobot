package com.wootion.protocols.robot;

import com.alibaba.fastjson.JSONObject;
import com.wootion.controller.LoginController;
import com.wootion.protocols.robot.msg.*;
import com.wootion.robot.*;
import com.wootion.taskmanager.ResultSynThread;
import com.wootion.taskmanager.TaskEventThread;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RosClientMsgHandler {
    static Logger logger = LoggerFactory.getLogger(RosClientMsgHandler.class);
    //public static void handleMessage(Publish<?> msg,ChannelHandlerContext ctx) {
    public static void handleMessage(Map map, ChannelHandlerContext ctx) {
        // check for the correct fields
        //System.out.println(map);
      /*  if( msg.getMsg() instanceof  ActionResponseMsg){
            int transId=msg.getMsg().getTrans_id();
            RosActionCaller rosActionCaller = RosActionCallerHolder.getInstance().getStep(transId);
            if(rosActionCaller !=null){
                rosActionCaller.setResultMsg(msg.getMsg());
                return ;
            }
            return;
        }*/
        JSONObject jobj=(JSONObject)map;
        String id =jobj.getString("id");
        String op = jobj.getString("op");
        String topic = jobj.getString("topic");

        if(op==null || "publish".equals(op )){
            JSONObject msg=jobj.getJSONObject("msg");
            int transId=msg.getIntValue("trans_id");
            RosCommandCaller rosCommandCaller =null;
            if(transId!=0){
                //异步请求的topic对应请求和 service请求
                rosCommandCaller = RosCommandCallerHolder.getInstance().getStep(transId);
            }
            if(rosCommandCaller !=null){
                Publish<?> cmdOp=rosCommandCaller.getCmdOp();
                //XXX_cmd  对应返回 XXX_ack ，必须配对。排除不是后台发起的topic请求
                String cmdTopic=cmdOp.getTopic();
                if(cmdTopic==null){
                    logger.error("RosClientMsgHandler cmdOp.getTopic() =null"+cmdOp);
                }
                String name=cmdTopic.substring(0,cmdTopic.lastIndexOf("_"));
                if(topic.startsWith(name)){
                    rosCommandCaller.setResultMsg(jobj);
                    return ;
                }
            }
            //直接收到的广播消息
            if (MsgNames.topic_robot_status.equals(topic) ) {
                HeartBeat.addTask(jobj);
                return ;
            }
            if (MsgNames.topic_task_status.equals(topic) ) {
                ResultSynThread.addEvent(jobj);
                return ;
            }
            if (MsgNames.topic_ptz_status.equals(topic) ) {
                ResultSynThread.addEvent(jobj);
                return ;
            }
            if (MsgNames.topic_task_event.equals(topic ) ) {
                TaskEventThread.addEvent(jobj);
                return ;
            }
        }else if( "service_response".equals(op)){ //服务调用
            String strId=id.substring(id.lastIndexOf(":")+1);
            Integer transId=Integer.parseInt(strId);
            RosCommandCaller rosCommandCaller = RosCommandCallerHolder.getInstance().getStep(transId);
            if(rosCommandCaller !=null){
                rosCommandCaller.setResultMsg(jobj);
                return ;
            }
        }



       /* if (msg.equals(JRosbridge.OP_CODE_PUBLISH)) {
            // check for the topic name
            String topic = jsonObject.getString(JRosbridge.FIELD_TOPIC);

            // call each callback with the message
            ArrayList<TopicCallback> callbacks = topicCallbacks.get(topic);
            if (callbacks != null) {
                Message msg = new Message(
                        jsonObject.getJsonObject(JRosbridge.FIELD_MESSAGE));
                for (TopicCallback cb : callbacks) {
                    cb.handleMessage(msg);
                }
            }
        } else if (op.equals(JRosbridge.OP_CODE_SERVICE_RESPONSE)) {
            // check for the request ID
            String id = jsonObject.getString(JRosbridge.FIELD_ID);

            // call the callback for the request
            ServiceCallback cb = serviceCallbacks.get(id);
            if (cb != null) {
                // check if a success code was given
                boolean success = jsonObject
                        .containsKey(JRosbridge.FIELD_RESULT) ? jsonObject
                        .getBoolean(JRosbridge.FIELD_RESULT) : true;
                // get the response
                JsonObject values = jsonObject
                        .getJsonObject(JRosbridge.FIELD_VALUES);
                ServiceResponse response = new ServiceResponse(values, success);
                cb.handleServiceResponse(response);
            }
        } else if (op.equals(JRosbridge.OP_CODE_CALL_SERVICE)) {
            // check for the request ID
            String id = jsonObject.getString("id");
            String services = jsonObject.getString("services");

            // call the callback for the request
            CallServiceCallback cb = callServiceCallbacks.get(services);
            if (cb != null) {
                // get the response
                JsonObject args = jsonObject
                        .getJsonObject(JRosbridge.FIELD_ARGS);
                ServiceRequest request = new ServiceRequest(args);
                request.setId(id);
                cb.handleServiceCall(request);
            }
        } else {
            System.err.println("[WARN]: Unrecognized op code: "
                    + jsonObject.toString());
        }*/

    }



}

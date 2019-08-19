package com.wootion.protocols.robot.operation;

import com.alibaba.fastjson.JSONObject;
import com.wootion.commons.Constans;
import com.wootion.protocols.robot.msg.MsgNames;
import com.wootion.protocols.robot.msg.PresetScaleCommandMsg;
import com.wootion.protocols.robot.msg.Publish;

public class AddPtzSetOp extends Publish<PresetScaleCommandMsg> {

    public AddPtzSetOp(int opId, PresetScaleCommandMsg msg, String topic) {
        this.setMsg(msg);
        this.setOp("publish");
        this.setTopic(topic);
    }

    public static void main(String[] args) {
       /* PresetScaleCommandMsg commandMsg = new PresetScaleCommandMsg(1,"id","ip","DLB",(short)0);
        AddPtzSetOp addPtzSetOp = new AddPtzSetOp(1,commandMsg, MsgNames.topic_addptzset_command);
        System.out.println(JSONObject.toJSON(addPtzSetOp));*/
    }
}

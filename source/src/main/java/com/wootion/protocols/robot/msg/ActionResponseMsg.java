package com.wootion.protocols.robot.msg;

import com.alibaba.fastjson.JSONObject;

public class ActionResponseMsg implements RosMsg{
    protected Integer trans_id;
    protected JSONObject msgObj;
    protected int type=0; //0 feedback ,1 result
    @Override
    public Integer getTrans_id() {
        return this.trans_id;
    }

    @Override
    public void setTrans_id(Integer transId) {
        this.trans_id = transId;
    }

    public JSONObject getMsgObj() {
        return msgObj;
    }

    public void setMsgObj(JSONObject msgObj) {
        this.msgObj = msgObj;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

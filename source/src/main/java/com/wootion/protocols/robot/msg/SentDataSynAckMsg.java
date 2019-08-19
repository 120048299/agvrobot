package com.wootion.protocols.robot.msg;


import com.alibaba.fastjson.JSONObject;

import java.util.Arrays;

public class SentDataSynAckMsg implements RosMsg{
    protected Header  header;
    protected String  sender;
    protected String  receiver;
    protected Integer trans_id;
    protected String  robot_id;
    protected String  type;
    protected String  data;
    protected String  ret_code;
    protected String  ret_msg;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }
    public void setHeader(JSONObject header) {
        if (header == null || header.size() == 0) {
            return ;
        }
        if (this.header == null) {
            this.header = new Header();
        }

        this.header.setFrame_id((String) header.get("frame_id"));
        this.header.setSeq(header.getIntValue("seq"));
        this.header.setStamp(header.getJSONObject("stamp"));
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    @Override
    public Integer getTrans_id() {
        return trans_id;
    }

    public void setTrans_id(Integer trans_id) {
        this.trans_id = trans_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getRet_code() {
        return ret_code;
    }

    public void setRet_code(String ret_code) {
        this.ret_code = ret_code;
    }

    public String getRet_msg() {
        return ret_msg;
    }

    public void setRet_msg(String ret_msg) {
        this.ret_msg = ret_msg;
    }

    public String getRobot_id() {
        return robot_id;
    }

    public void setRobot_id(String robot_id) {
        this.robot_id = robot_id;
    }

    @Override
    public String toString() {
        return "SentDataSynAckMsg{" +
                "header=" + header +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", trans_id=" + trans_id +
                ", robot_id='" + robot_id + '\'' +
                ", type='" + type + '\'' +
                ", data='" + data + '\'' +
                ", ret_code='" + ret_code + '\'' +
                ", ret_msg='" + ret_msg + '\'' +
                '}';
    }
}

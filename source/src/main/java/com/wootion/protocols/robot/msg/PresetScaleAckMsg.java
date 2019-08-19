package com.wootion.protocols.robot.msg;

import com.alibaba.fastjson.JSONObject;

public class PresetScaleAckMsg implements RosMsg{
    protected Header  header;
    protected String  sender;
    protected String  receiver;
    protected Integer trans_id;
    protected String  robot_ip;

    private String result;
    private String directory;



    public void setRobot_ip(String robot_ip) {
        this.robot_ip = robot_ip;
    }

    @Override
    public Integer getTrans_id() {
        return this.trans_id;
    }

    public Short ctrlMode() {
        return null;
    }

    public Short cmdMode() {
        return null;
    }

    public String getRobot_ip() {
        return this.robot_ip;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

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

    public void setTrans_id(Integer trans_id) {
        this.trans_id = trans_id;
    }
}

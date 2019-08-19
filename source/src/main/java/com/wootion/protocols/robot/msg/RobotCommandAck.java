package com.wootion.protocols.robot.msg;


import com.alibaba.fastjson.JSONObject;
import com.wootion.task.CONTROL_CMD;

/**
 * 以前的状态消息，现在只剩下命令响应消息。
 */
public class RobotCommandAck implements RosMsg {
    protected Header  header;
    protected String  sender;
    protected String  receiver;
    protected Integer trans_id;
    protected String  robot_ip;
    protected Integer ack;
    protected String  data;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Integer getTrans_id() {
        return trans_id;
    }

    public void setTrans_id(Integer trans_id) {
        this.trans_id = trans_id;
    }

    public Short ctrlMode() {
        return CONTROL_CMD.IDLE_CMD.getCtrl();
    }

    public Short cmdMode() {
        return CONTROL_CMD.IDLE_CMD.getCmd();
    }

    public String getRobot_ip() {
        return robot_ip;
    }

    public void setRobot_ip(String robot_ip) {
        this.robot_ip = robot_ip;
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

    public Integer getAck() {
        return ack;
    }

    public void setAck(Integer ack) {
        this.ack = ack;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RobotCommandAck{" +
                "trans_id=" + trans_id +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", robot_ip='" + robot_ip + '\'' +
                ", ack=" + ack +
                ", data='" + data + '\'' +
                '}';
    }


}

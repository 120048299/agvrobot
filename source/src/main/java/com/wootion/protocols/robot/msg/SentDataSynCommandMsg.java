package com.wootion.protocols.robot.msg;

import com.wootion.commons.Constans;

public class SentDataSynCommandMsg implements RosMsg{
    protected Header  header;
    protected String  sender = MsgNames.node_server;
    protected String  receiver = "task_manager";
    protected String  type;
    protected Integer trans_id;
    protected String  data;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


    @Override
    public String toString() {
        return "SentDataSynCommandMsg{" +
                "header=" + header +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", type='" + type + '\'' +
                ", trans_id=" + trans_id +
                ", data='" + data + '\'' +
                '}';
    }
}

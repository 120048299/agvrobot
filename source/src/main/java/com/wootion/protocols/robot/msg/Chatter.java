package com.wootion.protocols.robot.msg;


import com.alibaba.fastjson.JSONObject;

public class Chatter extends Publish {
    String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


    public void setMsg(JSONObject msg) {
        data = msg.getString("data");
    }
}

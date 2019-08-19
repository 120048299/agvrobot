package com.wootion.protocols.robot.msg;

import com.alibaba.fastjson.JSONObject;
import com.wootion.task.event.AdvertiseRobotEvent;


public class Advertise {
    // {"op":"advertise","topic":"\/chatter","id":"0","type":"std_msgs\/String"}
    private String op;
    private String topic;
    private String id = "1";
    private String type;
    private boolean latch=false;
    private int queue_size = 0;

    public Advertise() {

        this.setOp("");
    }

    public Advertise(String topic, String type) {
        this.topic = topic;
        this.type = type;
        setOp("advertise");
    }

    public Advertise(AdvertiseRobotEvent advertiseRobotEvent) {
        this.topic = advertiseRobotEvent.getTopic();
        this.type = advertiseRobotEvent.getType();
        setOp("advertise");
	}

	public String getOp() {
        return op;
    }

    public void setOp(String op) {

        this.op = op;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

   /* @Override
    public String toString() {
        return type+topic;
    }*/

    public boolean isLatch() {
        return latch;
    }

    public void setLatch(boolean latch) {
        this.latch = latch;
    }

    public int getQueue_size() {
        return queue_size;
    }

    public void setQueue_size(int queue_size) {
        this.queue_size = queue_size;
    }

    public static void main(String[] args) {
        Advertise advertise = new Advertise();
        advertise.setId("0");
        advertise.setTopic("/chatter");
        advertise.setType("std_msg/String");

        String msg = JSONObject.toJSON(advertise).toString();
        System.out.println("msg " + msg);
    }
}

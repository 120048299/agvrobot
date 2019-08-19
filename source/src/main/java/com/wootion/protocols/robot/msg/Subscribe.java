package com.wootion.protocols.robot.msg;

import com.wootion.task.event.SubscribeRobotEvent;

public class Subscribe  {
    // {"op":"advertise","topic":"\/chatter","id":"0","type":"std_msgs\/String"}
    private String op;
    private String topic;
    private String id = "0";
    private String type;
    private String compression="none";
    private int throttle_rate=0;
    private int queue_length=0;


    public Subscribe() {
        this.setOp("");
    }

    public Subscribe(String topic, String type) {
        this.topic = topic;
        this.type = type;
        this.setOp("subscribe");
    }

    public Subscribe(SubscribeRobotEvent subscribeRobotEvent) {
        this.topic = subscribeRobotEvent.getTopic();
        this.type = subscribeRobotEvent.getType();
        this.setOp("subscribe");
	}

	public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = "subscribe";
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

    @Override
    public String toString() {
        return type + topic;
    }

    public String getCompression() {
        return compression;
    }

    public void setCompression(String compression) {
        this.compression = compression;
    }

    public int getThrottle_rate() {
        return throttle_rate;
    }

    public void setThrottle_rate(int throttle_rate) {
        this.throttle_rate = throttle_rate;
    }

    public int getQueue_length() {
        return queue_length;
    }

    public void setQueue_length(int queue_length) {
        this.queue_length = queue_length;
    }
}

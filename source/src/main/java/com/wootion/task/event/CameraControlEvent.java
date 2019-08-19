package com.wootion.task.event;

import io.netty.channel.Channel;

/**
 * 摄像机控制 websocket事件
 */

public class CameraControlEvent {
    private Channel channel;
    private String robotIp;
    private String cmd;
    private String data;

    public CameraControlEvent(Channel channel, String robotIp, String cmd, String data) {
        this.channel = channel;
        this.robotIp = robotIp;
        this.cmd = cmd;
        this.data    = data;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getRobotIp() {
        return robotIp;
    }

    public void setRobotIp(String robotIp) {
        this.robotIp = robotIp;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
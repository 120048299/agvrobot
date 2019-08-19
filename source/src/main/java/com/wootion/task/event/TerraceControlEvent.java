package com.wootion.task.event;

import com.wootion.commons.MSG_TYPE;
import com.wootion.task.CONTROL_CMD;
import io.netty.channel.Channel;

/**
 * 云台控制 websocket事件
 */

public class TerraceControlEvent {
    private Channel channel;
    private String robotIp;
    private String command;
    private String data;

    public TerraceControlEvent(Channel channel, String robotIp,String command,String data) {
        this.channel = channel;
        this.robotIp = robotIp;
        this.command = command;
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

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
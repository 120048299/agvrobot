package com.wootion.task.event;

import com.wootion.commons.MSG_TYPE;
import com.wootion.task.CONTROL_CMD;
import io.netty.channel.Channel;

/**
 * 机器人控制相关消息
 */

public class RemoteControlTimeOutEvent {
    private String robotIp;
    public RemoteControlTimeOutEvent(String robotIp) {
        this.robotIp = robotIp;
    }



    public String getRobotIp() {
        return robotIp;
    }

    public void setRobotIp(String robotIp) {
        this.robotIp = robotIp;
    }


}
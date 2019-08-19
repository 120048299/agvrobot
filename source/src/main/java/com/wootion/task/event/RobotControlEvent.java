package com.wootion.task.event;

import com.wootion.commons.MSG_TYPE;

import com.wootion.model.UserInfo;
import com.wootion.task.CONTROL_CMD;
import io.netty.channel.Channel;
import lombok.Data;

/**
 * 机器人控制相关消息
 */
@Data
public class RobotControlEvent {
    private MSG_TYPE msgtype;
    private int isForced = 0;//强制释放其他客户端的控制权
    private Channel channel;
    private Double speedValue; // speed value
    private String robotIp;
    private CONTROL_CMD control_cmd;
    private UserInfo userInfo;

    private String command;//当msgtype=21 云台操作 和 22 摄像机操作时有用
    private String data;

    public RobotControlEvent(MSG_TYPE msgtype,Channel channel, Double speedValue, String robotIp) {
        this.msgtype = msgtype;
        this.channel = channel;
        this.speedValue = speedValue;
        this.robotIp = robotIp;
    }

    public RobotControlEvent(MSG_TYPE msgtype, Channel channel, Double speedValue, String robotIp, CONTROL_CMD control_cmd) {
        this.msgtype = msgtype;
        this.channel = channel;
        this.speedValue = speedValue;
        this.robotIp = robotIp;
        this.control_cmd = control_cmd;
    }

}
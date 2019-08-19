package com.wootion.task.event;

import com.wootion.protocols.robot.msg.PresetScaleAckMsg;
import io.netty.channel.Channel;

public class AddPtzSetOverEvent {
    private PresetScaleAckMsg msg;
    private Channel channel;

    public AddPtzSetOverEvent(PresetScaleAckMsg msg, Channel channel) {
        this.msg = msg;
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public AddPtzSetOverEvent() {
    }

    public PresetScaleAckMsg getMsg() {
        return msg;
    }

    public void setMsg(PresetScaleAckMsg msg) {
        this.msg = msg;
    }


}

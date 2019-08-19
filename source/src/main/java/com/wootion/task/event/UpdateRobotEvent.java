package com.wootion.task.event;

import com.wootion.protocols.robot.msg.Publish;
import com.wootion.protocols.robot.msg.RobotCommandAck;

import io.netty.channel.ChannelHandlerContext;

public class UpdateRobotEvent {
    private String topicNameSpace;
    private RobotCommandAck robotCommandAck;
    private ChannelHandlerContext ctx;

    public UpdateRobotEvent(Publish<RobotCommandAck> resp, ChannelHandlerContext ctx) {
        this.robotCommandAck = resp.getMsg();
        topicNameSpace = "/" + resp.getTopic().split("/")[0];
        this.ctx = ctx;
    }

    public RobotCommandAck getRobotCommandAck() {
        return robotCommandAck;
    }

    public void setRobotCommandAck(RobotCommandAck robotCommandAck) {
        this.robotCommandAck = robotCommandAck;
    }

    public ChannelHandlerContext getCtx() {
        return this.ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }
	/**
	 * @return the topicNameSpace
	 */
	public String topicNameSpace() {
		return topicNameSpace;
	}

	/**
	 * @param topicNameSpace the topicNameSpace to set
	 */
	public void setTopicNameSpace(String topicNameSpace) {
		this.topicNameSpace = topicNameSpace;
	}
}

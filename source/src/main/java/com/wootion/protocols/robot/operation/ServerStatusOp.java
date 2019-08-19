package com.wootion.protocols.robot.operation;

import com.wootion.protocols.robot.msg.GeneralTopicMsg;
import com.wootion.protocols.robot.msg.MsgNames;
import com.wootion.protocols.robot.msg.Publish;


public class ServerStatusOp extends Publish<GeneralTopicMsg> {
    private int opId;

	/**
	 * @return the opId
	 */
	public int opId() {
		return opId;
	}

	/**
	 * @param opId the opId to set
	 */
	public void setOpId(int opId) {
		this.opId = opId;
	}

	public ServerStatusOp(int opId, GeneralTopicMsg msg) {
		this.opId = opId;
		this.setMsg(msg);
		this.setOp("publish");
		this.setTopic(MsgNames.topic_server_status);
	}

	@Override
	public String content() {
		StringBuilder builder = new StringBuilder();
		builder.append("ServerStatusOp{").append("opId:" + opId);
		builder.append(",").append("sendTime:" + sendTime());
		builder.append(",").append("sendTimes:" + resendTimes());
		builder.append(",").append("opStatus:" + opStatus());
		builder.append(",").append(getMsg().toString());
		builder.append("}");
		return builder.toString();
	}

}

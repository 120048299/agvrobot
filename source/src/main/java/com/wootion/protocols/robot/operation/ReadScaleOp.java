package com.wootion.protocols.robot.operation;

import com.wootion.commons.Constans;
import com.wootion.protocols.robot.msg.MsgNames;
import com.wootion.protocols.robot.msg.ReadScaleCommandMsg;
import com.wootion.protocols.robot.msg.Publish;


public class ReadScaleOp extends Publish<ReadScaleCommandMsg> {
    private int opId; // 操作步骤id，不用发送给机器人，不能实现getOpId

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

	public ReadScaleOp(int opId, ReadScaleCommandMsg msg) {
		this.opId = opId;
		this.setMsg(msg);
		this.setOp("publish");
		this.setTopic(MsgNames.topic_read_scale_command);
	}

	@Override
	public String content() {
		StringBuilder builder = new StringBuilder();
		builder.append("ReadScaleOp{").append("opId:" + opId);
		builder.append(",").append("sendTime:" + sendTime());
		builder.append(",").append("sendTimes:" + resendTimes());
		builder.append(",").append("opStatus:" + opStatus());
		builder.append(",").append(getMsg().content());
		builder.append("}");
		return builder.toString();
	}

}

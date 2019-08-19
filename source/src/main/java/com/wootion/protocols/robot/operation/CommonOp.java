package com.wootion.protocols.robot.operation;

import com.wootion.protocols.robot.msg.Publish;
import com.wootion.protocols.robot.msg.RosMsg;

public class CommonOp extends Publish<RosMsg> {
    private int opId;

    public CommonOp(int opId, RosMsg msg, String topic) {
        this.setMsg(msg);
        this.setOp("publish");
        this.setTopic(topic);
        this.opId=opId;
    }

    public int opId() {
        return opId;
    }
    public void setOpId(int opId) {
        this.opId = opId;
    }
}

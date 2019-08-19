package com.wootion.protocols.robot.msg;

import com.wootion.protocols.robot.operation.OP_STATUS;


public class Publish<T extends RosMsg> {
    // {"op":"advertise","topic":"\/chatter","id":"0","type":"std_msgs\/String"}
    private String op;
    private String topic;
    private String service;
    private String id;
    private T msg;
    private T args;
    private OP_STATUS opStatus;
    private long sendTime = 0;
    private int resendTimes = 0;
    private boolean latch =false;

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getId() {
        return this.op+":"+this.topic+":"+id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public T getMsg() {
        if(args!=null){
            return args;
        }else {
            return msg;
        }
    }

    public void setMsg(RosMsg msg) {
        this.msg = (T) msg;
        if(msg==null){
            System.out.println("set msg ,msg is null");
        }
        this.id=this.msg.getTrans_id().toString();
    }


    public void setTransId(int transId) {
        this.msg.setTrans_id(transId);
    }

    public int opId() {
        return 0;
    }


	/*public Object responseCode() {
		return this.msg.responseCode();
	}*/

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String content() {
        return "";
    }

	/**
	 * @return the sendTime
	 */
	public long sendTime() {
		return sendTime;
	}

    public int resendTimes() {
        return resendTimes;
    }

	/**
	 * @param sendTime the sendTime to set
	 */
	public void sendTime(long sendTime) {
        resendTimes++;
		this.sendTime = sendTime;
	}

    /**
	 * @return the opStatus
	 */
	public OP_STATUS opStatus() {
		return opStatus;
	}

	/**
	 * @param opStatus the opStatus to set
	 */
	public void opStatus(OP_STATUS opStatus) {
		this.opStatus = opStatus;
	}

    public T getArgs() {
        return args;
    }

    public void setArgs(T args) {
        this.args = args;
        this.op="call_service";
        this.id = this.args.getTrans_id().toString();
    }

    public boolean isLatch() {
        return latch;
    }

    public void setLatch(boolean latch) {
        this.latch = latch;
    }
}

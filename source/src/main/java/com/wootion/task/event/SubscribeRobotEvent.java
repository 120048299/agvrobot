package com.wootion.task.event;

/**
 * 注册监听topic
 */
public class SubscribeRobotEvent {
	private String robotIp;
	private String type;
	private String topic;

    public SubscribeRobotEvent(String ip, String topic,String type) {
		this.robotIp = ip;
		this.type = type;
		this.topic = topic;
    }

    public SubscribeRobotEvent() {
        this.robotIp = "";
    }

	/**
	 * @return the robotIp
	 */
	public String getRobotIp() {
		return robotIp;
	}

	/**
	 * @param robotIp the robotIp to set
	 */
	public void setRobotIp(String robotIp) {
		this.robotIp = robotIp;
	}


	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the topic
	 */
	public String getTopic() {
		return topic;
	}

	/**
	 * @param topic the topic to set
	 */
	public void setTopic(String topic) {
		this.topic = topic;
	}


}

package com.wootion.task.event;

/**
 * 一键返航到门前点,即充电
 */
public class OneKeyBackEvent {

	private String robotIp;

	public OneKeyBackEvent( String robotIp) {
		this.robotIp =robotIp;
	}

	public String getRobotIp() {
		return robotIp;
	}

	public void setRobotIp(String robotIp) {
		this.robotIp = robotIp;
	}

	@Override
	public String toString() {
		return "OneKeyBackEvent{robotIp='" + robotIp + '\'' +
				'}';
	}
}

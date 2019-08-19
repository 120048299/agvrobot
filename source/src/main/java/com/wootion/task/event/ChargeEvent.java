package com.wootion.task.event;

/**
 * 充电返航
 */
public class ChargeEvent {
    private String robotIp;

	public ChargeEvent(String robotIp) {
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
		return "ChargeEvent{" +
				", robotIp='" + robotIp + '\'' +
				'}';
	}
}

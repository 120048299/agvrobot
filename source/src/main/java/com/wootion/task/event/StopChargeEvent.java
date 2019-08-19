package com.wootion.task.event;

public class StopChargeEvent {
    private int style ;// 暂时无用
	private String robotIp;

	public StopChargeEvent(int style, String robotIp) {
		this.style = style;
		this.robotIp =robotIp;
	}

	public int getStyle() {
		return style;
	}

	public void setStyle(int style) {
		this.style = style;
	}

	public String getRobotIp() {
		return robotIp;
	}

	public void setRobotIp(String robotIp) {
		this.robotIp = robotIp;
	}

	@Override
	public String toString() {
		return "StopChargeEvent{" +
				"style=" + style +
				", robotIp='" + robotIp + '\'' +
				'}';
	}
}

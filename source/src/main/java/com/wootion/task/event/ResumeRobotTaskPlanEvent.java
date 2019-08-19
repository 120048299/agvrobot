package com.wootion.task.event;

/**
 * 监控界面 恢复运行任务
 */
public class ResumeRobotTaskPlanEvent {

	private String robotIp;
	private  String planId;

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public ResumeRobotTaskPlanEvent(String id, String robotIp) {
		this.planId = id;
		this.robotIp = robotIp;
	}

	public String getRobotIp() {
		return robotIp;
	}

	public void setRobotIp(String robotIp) {
		this.robotIp = robotIp;
	}

}

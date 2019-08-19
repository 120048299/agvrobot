package com.wootion.task.event;

/**
 * 立即启动任务
 */
public class StartTaskEvent {
    private String taskPlanId; //对应task_plan
	private String robotIp;
	private String robotId;

	public StartTaskEvent(String taskPlanId, String robotIp,String robotId) {
		this.taskPlanId = taskPlanId;
		this.robotIp=robotIp;
		this.robotId = robotId;
	}

	public String getTaskPlanId() {
		return taskPlanId;
	}

	public void setTaskPlanId(String taskPlanId) {
		this.taskPlanId = taskPlanId;
	}

	public String getRobotIp() {
		return robotIp;
	}

	public void setRobotIp(String robotIp) {
		this.robotIp = robotIp;
	}

	public String getRobotId() {
		return robotId;
	}

	public void setRobotId(String robotId) {
		this.robotId = robotId;
	}

	@Override
	public String toString() {
		return "StartTaskEvent{" +
				"taskPlanId='" + taskPlanId + '\'' +
				", robotIp='" + robotIp + '\'' +
				", robotId='" + robotId + '\'' +
				'}';
	}
}

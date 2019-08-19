package com.wootion.task.event;

/**
 * 监控界面暂停机器人的任务：任务线程仍然在
 */
public class PauseRobotTaskPlanEvent {

	private String robotIp;
	private String taskplanId;

	private int taskStatus=2;// 2原地暂停  4临时暂停，由于机器人告警和远程控制，遥控 .

	public String getTaskplanId() {
		return taskplanId;
	}

	public void setTaskplanId(String taskplanId) {
		this.taskplanId = taskplanId;
	}


	/**
	 *
	 * @param taskplanId
	 * @param robotIp
	 * @param taskStatus  2原地暂停  4临时暂停
	 */
	public PauseRobotTaskPlanEvent(String taskplanId,String robotIp,int taskStatus) {
		this.taskplanId = taskplanId;
		this.robotIp = robotIp;
		this.taskStatus = taskStatus;
	}

	public String getRobotIp() {
		return robotIp;
	}

	public void setRobotIp(String robotIp) {
		this.robotIp = robotIp;
	}

	public int getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(int taskStatus) {
		this.taskStatus = taskStatus;
	}
}

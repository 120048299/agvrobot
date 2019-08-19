package com.wootion.task.event;

/**
 * 日历暂停下达的任务task_plan
 *  参数只需一个就可以.
 * 如果有taskPlanId,则暂停此taskPlan;如果有robotIp，则按robotIp查询.
 * 二者都有时，都查询处理
 *
 * 关于暂停和停止：注意区分，前端页面上的任务列表暂停任务，对机器人来说是停止执行任务。
  前端地图页面上可能做一个真正的暂停，即机器人上也是暂停，此时任务线程还在，阻塞方式。
  而后端发现机器人状态不同时，可能暂停任务线程，也可能时终止执行。

 */
public class StopTaskEvent {
    private String taskPlanId;
	private String robotIp;

	public StopTaskEvent(String taskPlanId, String robotIp) {
		this.taskPlanId = taskPlanId;
		this.robotIp =robotIp;
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

	@Override
	public String toString() {
		return "StopTaskEvent{" +
				"taskPlanId='" + taskPlanId + '\'' +
				", robotIp='" + robotIp + '\'' +
				'}';
	}
}

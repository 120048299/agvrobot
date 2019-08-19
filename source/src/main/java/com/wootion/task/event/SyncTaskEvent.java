package com.wootion.task.event;

/**
 * 可以指定同步一个task_plan
 *
 */
public class SyncTaskEvent {
    private String taskPlanId;
    private String robotId;

    public SyncTaskEvent(){

    }

    public SyncTaskEvent(String taskPlanId) {
        this.taskPlanId = taskPlanId;
    }

    public String getTaskPlanId() {
        return taskPlanId;
    }

    public void setTaskPlanId(String taskPlanId) {
        this.taskPlanId = taskPlanId;
    }

    public String getRobotId() {
        return robotId;
    }

    public void setRobotId(String robotId) {
        this.robotId = robotId;
    }

    @Override
    public String toString() {
        return "SyncTaskEvent{" +
                "taskPlanId='" + taskPlanId + '\'' +
                ", robotId='" + robotId + '\'' +
                '}';
    }
}

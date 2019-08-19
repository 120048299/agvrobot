package com.wootion.task.event;

/**
 * 告警计算结束事件：
 *
 */
public class AlarmCalcFinishedEvent {

    String logId; // 机器人日志id，或者任务日志id
    String taskPlanId;

    public AlarmCalcFinishedEvent() {
    }

    public AlarmCalcFinishedEvent(String taskPlanId,String logId ) {
        this.logId = logId;
        this.taskPlanId = taskPlanId;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getTaskPlanId() {
        return taskPlanId;
    }

    public void setTaskPlanId(String taskPlanId) {
        this.taskPlanId = taskPlanId;
    }
}

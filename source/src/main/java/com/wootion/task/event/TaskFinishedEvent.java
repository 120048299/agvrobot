package com.wootion.task.event;

import lombok.Data;

import java.util.Arrays;

/**
 * 执行任务，读表结束
 */
@Data
public class TaskFinishedEvent {
    private String robotId;
    private String taskId;
    private String jobId;

    public TaskFinishedEvent(String robotId,String taskId, String jobId) {
        this.robotId = robotId;
        this.taskId = taskId;
        this.jobId = jobId;
    }
}

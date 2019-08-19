package com.wootion.agvrobot.dto.robotTask;

import java.util.Date;

public class TaskDetail {
    private String taskId;
    private String taskName;
    private Long publishTime;
    private String publisherName;
    private String devName;
    private String spotName;
    private int opsType;
    private int taskStat;
    private String markName;
    public TaskDetail() {
    }


    public TaskDetail(String taskId, String taskName, Long publishTime, String publisherName, String devName,
                      String spotName, int opsType, short taskStat, String markName) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.publishTime = publishTime;
        this.publisherName = publisherName;
        this.devName = devName;
        this.spotName = spotName;
        this.opsType = opsType;
        this.taskStat = taskStat;
        this.markName = markName;
    }

    public String getMarkName() {
        return markName;
    }

    public void setMarkName(String markName) {
        this.markName = markName;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Long publishTime) {
        this.publishTime = publishTime;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    public String getSpotName() {
        return spotName;
    }

    public void setSpotName(String spotName) {
        this.spotName = spotName;
    }

    public int getOpsType() {
        return opsType;
    }

    public void setOpsType(int opsType) {
        this.opsType = opsType;
    }

    public int getTaskStat() {
        return taskStat;
    }

    public void setTaskStat(int taskStat) {
        this.taskStat = taskStat;
    }
}

package com.wootion.agvrobot.dto.robotTask;

import java.util.Date;

public class TaskResultInfo {
    private String taskLogId;
    private String taskId;
    private String taskName;
    private Long publishTime;
    private Date finishTime;
    private String publisherName;
    private String devName;
    private String spotName;
    private int opsType;
    private int taskStat;
    private String fileName;
    private String result;
    private String markName;
    public TaskResultInfo() {
    }


    public TaskResultInfo(String taskLogId, String taskId, String taskName, Long publishTime, Date finishTime,
                          String publisherName, String devName, String spotName, int opsType, int taskStat,
                          String fileName, String result, String markName) {
        this.taskLogId = taskLogId;
        this.taskId = taskId;
        this.taskName = taskName;
        this.publishTime = publishTime;
        this.finishTime = finishTime;
        this.publisherName = publisherName;
        this.devName = devName;
        this.spotName = spotName;
        this.opsType = opsType;
        this.taskStat = taskStat;
        this.fileName = fileName;
        this.result = result;
        this.markName = markName;
    }

    public String getMarkName() {
        return markName;
    }

    public void setMarkName(String markName) {
        this.markName = markName;
    }

    public String getTaskLogId() {
        return taskLogId;
    }

    public void setTaskLogId(String taskLogId) {
        this.taskLogId = taskLogId;
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

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}

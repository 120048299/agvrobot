package com.wootion.model;

public class TaskPtz {
    private String uid;

    private String taskId;

    private String ptzSetId;

    private String siteId;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid == null ? null : uid.trim();
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId == null ? null : taskId.trim();
    }

    public String getPtzSetId() {
        return ptzSetId;
    }

    public void setPtzSetId(String ptzSetId) {
        this.ptzSetId = ptzSetId == null ? null : ptzSetId.trim();
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId == null ? null : siteId.trim();
    }
}
package com.wootion.model;

import java.util.Date;

public class AlarmLog {
    private String uid;

    private String siteId;

    private String logId;

    private Integer isSystem;

    private String ptzSetId;

    private String devId;

    private Date time;

    private Integer status;

    private String alarmCodeId;

    private Integer alarmLevel;

    private String auditAlarmCodeId;

    private Integer auditAlarmLevel;

    private Integer isNewAlarm;

    private String alarmDesc;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid == null ? null : uid.trim();
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId == null ? null : siteId.trim();
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId == null ? null : logId.trim();
    }

    public Integer getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(Integer isSystem) {
        this.isSystem = isSystem;
    }

    public String getPtzSetId() {
        return ptzSetId;
    }

    public void setPtzSetId(String ptzSetId) {
        this.ptzSetId = ptzSetId == null ? null : ptzSetId.trim();
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId == null ? null : devId.trim();
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAlarmCodeId() {
        return alarmCodeId;
    }

    public void setAlarmCodeId(String alarmCodeId) {
        this.alarmCodeId = alarmCodeId == null ? null : alarmCodeId.trim();
    }

    public Integer getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(Integer alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public String getAuditAlarmCodeId() {
        return auditAlarmCodeId;
    }

    public void setAuditAlarmCodeId(String auditAlarmCodeId) {
        this.auditAlarmCodeId = auditAlarmCodeId == null ? null : auditAlarmCodeId.trim();
    }

    public Integer getAuditAlarmLevel() {
        return auditAlarmLevel;
    }

    public void setAuditAlarmLevel(Integer auditAlarmLevel) {
        this.auditAlarmLevel = auditAlarmLevel;
    }

    public Integer getIsNewAlarm() {
        return isNewAlarm;
    }

    public void setIsNewAlarm(Integer isNewAlarm) {
        this.isNewAlarm = isNewAlarm;
    }

    public String getAlarmDesc() {
        return alarmDesc;
    }

    public void setAlarmDesc(String alarmDesc) {
        this.alarmDesc = alarmDesc == null ? null : alarmDesc.trim();
    }
}
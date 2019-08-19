package com.wootion.model;

public class RobotTempl {
    private String uid;

    private String robotTemplName;

    private String devTypeId;

    private String param;

    private String devName;

    private Integer readOnly;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid == null ? null : uid.trim();
    }

    public String getRobotTemplName() {
        return robotTemplName;
    }

    public void setRobotTemplName(String robotTemplName) {
        this.robotTemplName = robotTemplName == null ? null : robotTemplName.trim();
    }

    public String getDevTypeId() {
        return devTypeId;
    }

    public void setDevTypeId(String devTypeId) {
        this.devTypeId = devTypeId == null ? null : devTypeId.trim();
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param == null ? null : param.trim();
    }

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName == null ? null : devName.trim();
    }

    public Integer getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Integer readOnly) {
        this.readOnly = readOnly;
    }
}
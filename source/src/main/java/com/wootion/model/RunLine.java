package com.wootion.model;

public class RunLine {
    private String uid;

    private String siteId;

    private String lineName;

    private Integer lineId;

    private Integer status;

    private String markId1;

    private String markId2;

    private Float maxVel;

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

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName == null ? null : lineName.trim();
    }

    public Integer getLineId() {
        return lineId;
    }

    public void setLineId(Integer lineId) {
        this.lineId = lineId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMarkId1() {
        return markId1;
    }

    public void setMarkId1(String markId1) {
        this.markId1 = markId1 == null ? null : markId1.trim();
    }

    public String getMarkId2() {
        return markId2;
    }

    public void setMarkId2(String markId2) {
        this.markId2 = markId2 == null ? null : markId2.trim();
    }

    public Float getMaxVel() {
        return maxVel;
    }

    public void setMaxVel(Float maxVel) {
        this.maxVel = maxVel;
    }
}
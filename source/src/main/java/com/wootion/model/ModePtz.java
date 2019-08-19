package com.wootion.model;

public class ModePtz {
    private String uid;

    private String modeId;

    private String ptzSetId;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid == null ? null : uid.trim();
    }

    public String getModeId() {
        return modeId;
    }

    public void setModeId(String modeId) {
        this.modeId = modeId == null ? null : modeId.trim();
    }

    public String getPtzSetId() {
        return ptzSetId;
    }

    public void setPtzSetId(String ptzSetId) {
        this.ptzSetId = ptzSetId == null ? null : ptzSetId.trim();
    }
}
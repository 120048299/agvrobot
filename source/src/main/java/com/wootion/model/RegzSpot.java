package com.wootion.model;

public class RegzSpot {
    private String uid;

    private String devTypeId;

    private String subDevTypeId;

    private String spotName;

    private Integer meterType;

    private Integer opsType;

    private Integer heatType;

    private Integer saveType;

    private String meterUnit;

    private Integer isSystem;

    private String siteId;

    private String code;

    private String spotGroupId;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid == null ? null : uid.trim();
    }

    public String getDevTypeId() {
        return devTypeId;
    }

    public void setDevTypeId(String devTypeId) {
        this.devTypeId = devTypeId == null ? null : devTypeId.trim();
    }

    public String getSubDevTypeId() {
        return subDevTypeId;
    }

    public void setSubDevTypeId(String subDevTypeId) {
        this.subDevTypeId = subDevTypeId == null ? null : subDevTypeId.trim();
    }

    public String getSpotName() {
        return spotName;
    }

    public void setSpotName(String spotName) {
        this.spotName = spotName == null ? null : spotName.trim();
    }

    public Integer getMeterType() {
        return meterType;
    }

    public void setMeterType(Integer meterType) {
        this.meterType = meterType;
    }

    public Integer getOpsType() {
        return opsType;
    }

    public void setOpsType(Integer opsType) {
        this.opsType = opsType;
    }

    public Integer getHeatType() {
        return heatType;
    }

    public void setHeatType(Integer heatType) {
        this.heatType = heatType;
    }

    public Integer getSaveType() {
        return saveType;
    }

    public void setSaveType(Integer saveType) {
        this.saveType = saveType;
    }

    public String getMeterUnit() {
        return meterUnit;
    }

    public void setMeterUnit(String meterUnit) {
        this.meterUnit = meterUnit == null ? null : meterUnit.trim();
    }

    public Integer getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(Integer isSystem) {
        this.isSystem = isSystem;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId == null ? null : siteId.trim();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getSpotGroupId() {
        return spotGroupId;
    }

    public void setSpotGroupId(String spotGroupId) {
        this.spotGroupId = spotGroupId == null ? null : spotGroupId.trim();
    }
}
package com.wootion.model;

public class RegzObjectField {
    private String uid;

    private String regzObjectId;

    private Integer orderNumber;

    private String fieldName;

    private Integer fieldType;

    private String fieldUnit;

    private String memo;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid == null ? null : uid.trim();
    }

    public String getRegzObjectId() {
        return regzObjectId;
    }

    public void setRegzObjectId(String regzObjectId) {
        this.regzObjectId = regzObjectId == null ? null : regzObjectId.trim();
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName == null ? null : fieldName.trim();
    }

    public Integer getFieldType() {
        return fieldType;
    }

    public void setFieldType(Integer fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldUnit() {
        return fieldUnit;
    }

    public void setFieldUnit(String fieldUnit) {
        this.fieldUnit = fieldUnit == null ? null : fieldUnit.trim();
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo == null ? null : memo.trim();
    }
}
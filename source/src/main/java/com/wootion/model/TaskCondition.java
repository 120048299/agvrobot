package com.wootion.model;

public class TaskCondition {
    private String uid;

    private String taskId;

    private Integer conditionOrder;

    private Integer conditionType;

    private String selectedValue;

    private String disabledValue;

    private Integer display;

    private String conditionParam;

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

    public Integer getConditionOrder() {
        return conditionOrder;
    }

    public void setConditionOrder(Integer conditionOrder) {
        this.conditionOrder = conditionOrder;
    }

    public Integer getConditionType() {
        return conditionType;
    }

    public void setConditionType(Integer conditionType) {
        this.conditionType = conditionType;
    }

    public String getSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(String selectedValue) {
        this.selectedValue = selectedValue == null ? null : selectedValue.trim();
    }

    public String getDisabledValue() {
        return disabledValue;
    }

    public void setDisabledValue(String disabledValue) {
        this.disabledValue = disabledValue == null ? null : disabledValue.trim();
    }

    public Integer getDisplay() {
        return display;
    }

    public void setDisplay(Integer display) {
        this.display = display;
    }

    public String getConditionParam() {
        return conditionParam;
    }

    public void setConditionParam(String conditionParam) {
        this.conditionParam = conditionParam == null ? null : conditionParam.trim();
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId == null ? null : siteId.trim();
    }
}
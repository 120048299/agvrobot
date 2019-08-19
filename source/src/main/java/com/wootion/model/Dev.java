package com.wootion.model;

public class Dev {
    private String uid;

    private String name;

    private String parentId;

    private String devTypeId;

    private String params;

    private Integer status;

    private Integer isSystem;

    private String siteId;

    private Integer orderNumber;

    private String code;


    public Dev(Dev d) {
        uid = d.getUid();
        name = d.getName();
        parentId = d.getParentId();
        params = d.getParams();
        status = d.getStatus();
        isSystem = d.getIsSystem();
        siteId = d.getSiteId();
        devTypeId = d.getDevTypeId();
        orderNumber = d.getOrderNumber();
        code = d.getCode();
    }

    public Dev() {
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid == null ? null : uid.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId == null ? null : parentId.trim();
    }

    public String getDevTypeId() {
        return devTypeId;
    }

    public void setDevTypeId(String devTypeId) {
        this.devTypeId = devTypeId == null ? null : devTypeId.trim();
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params == null ? null : params.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    /**
     * 用于比较顺序
     * @param other
     * @return
     */
    public int compareTo(Dev other){
        if(orderNumber < other.getOrderNumber()){
            return -1;
        }else if(orderNumber > other.getOrderNumber()) {
            return 1;
        }else{
            return 0;
        }
    }

}
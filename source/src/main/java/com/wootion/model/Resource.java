package com.wootion.model;

/**
 * 用mybatis重建时注意保留compareTo he implements
 * 这个排序最终用到菜单的排序上。树上的排序。
 */
public class Resource implements Comparable<Resource>{
    private String uid;

    private String parentId;

    private String name;

    private Integer type;

    private String permission;

    private String url;

    private Boolean available;

    private Integer orderNum;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid == null ? null : uid.trim();
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId == null ? null : parentId.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission == null ? null : permission.trim();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    /**
     * 用于比较顺序
     * @param other
     * @return
     */
    public int compareTo(Resource other){
        if(orderNum < other.getOrderNum()){
            return -1;
        }else if(orderNum > other.getOrderNum()) {
            return 1;
        }else{
            return 0;
        }
    }

    @Override
    public String toString() {
        return "Resource{" +
                "uid='" + uid + '\'' +
                ", parentId='" + parentId + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", permission='" + permission + '\'' +
                ", url='" + url + '\'' +
                ", available=" + available +
                ", orderNum=" + orderNum +
                '}';
    }
}
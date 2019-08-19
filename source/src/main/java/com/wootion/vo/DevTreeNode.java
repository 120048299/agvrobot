package com.wootion.vo;

public class DevTreeNode implements Comparable<DevTreeNode>{
    private String id;
    private String parentId;
    private String name;
    private String nodeType;
    private Integer orderNumber;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    private Object data;



    /**
     * 用于比较顺序
     * @param other
     * @return
     */
    public int compareTo(DevTreeNode other){
        if(orderNumber < other.getOrderNumber()){
            return -1;
        }else if(orderNumber > other.getOrderNumber()) {
            return 1;
        }else{
            return 0;
        }
    }
}

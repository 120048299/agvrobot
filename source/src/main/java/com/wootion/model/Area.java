package com.wootion.model;

import lombok.Data;

@Data
public class Area {
    private String uid;

    private String name;

    private String params;

    private String siteId;

    private Integer orderNumber;


    /**
     * 用于比较顺序
     * @param other
     * @return
     */
    public int compareTo(Area other){
        if(orderNumber < other.getOrderNumber()){
            return -1;
        }else if(orderNumber > other.getOrderNumber()) {
            return 1;
        }else{
            return 0;
        }
    }

}
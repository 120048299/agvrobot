package com.wootion.model;

import lombok.Data;

import java.util.Date;

@Data
public class Task {
    private String uid;

    private String name;

    private String userId;

    private Date createTime;

    private Date editTime;

    private Integer status;

    private String description;

    private String siteId;

    private String robotId;

    private Integer drivenMethod;

    private Integer repeat;

    private Integer mapTask;

    private Integer emergency;

    private Integer syncStatus;
}
package com.wootion.model;

import lombok.Data;

import java.util.Date;

@Data
public class Job {
    private String uid;

    private String taskId;

    private String name;

    private Date createTime;

    private Date planStartTime;

    private Date planEndTime;

    private Date realStartTime;

    private Date realEndTime;

    private String userId;

    private int priority;

    private int status;

    private String siteId;

    private String robotId;

    private String pathImage;

    private String endReason;

}
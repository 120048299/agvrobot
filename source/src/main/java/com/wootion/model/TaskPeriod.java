package com.wootion.model;

import lombok.Data;

import java.util.Date;
@Data
public class TaskPeriod {
    private String uid;

    private String taskId;

    private Integer style;

    private String styleParam;

    private Date startDate;

    private Date endDate;

    private String withinAdayTime;

    private String siteId;

}
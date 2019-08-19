package com.wootion.model;

import lombok.Data;

import java.util.Date;

@Data
public class SystemLog {
    private String uid;

    private String siteId;
    private String robotId;
    private Integer  event;
    private Integer result;
    private Date logTime;
    private String desc;
    private String memo;
}
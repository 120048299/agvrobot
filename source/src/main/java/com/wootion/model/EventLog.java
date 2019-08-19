package com.wootion.model;

import lombok.Data;

import java.util.Date;
@Data
public class EventLog {
    private String uid;

    private String siteId;

    private String robotId;

    private String eventType;

    private Integer eventLevel;

    private Date eventTime;

    private String eventDesc;


}
package com.wootion.model;

import lombok.Data;

import java.util.Date;
@Data
public class AlarmMsg {
    private String uid;

    private Date createdTime;

    private Date toSendTime;

    private String userId;

    private String jobId;

    private String ptzSetId;

    private String regzSpotId;

    private String devId;

    private String taskLogId;

    private String alarmLogId;

    private String alarmCodeId;

    private String alarmSubId;

    private String msgContent;

    private Integer sendStyle;

    private Integer status;

    private Integer failureTimes;

    private Date sendTime;

    private String siteId;

}
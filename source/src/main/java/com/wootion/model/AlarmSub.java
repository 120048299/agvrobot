package com.wootion.model;

import lombok.Data;
@Data
public class AlarmSub {
    private String uid;

    private String userId;

    private String ptzSetId;

    private Integer sendStyle;

    private Integer sendFrequence;

    private String fixedTime;

    private Integer smsInterval;

    private Integer telInterval;

    private Integer emailInterval;

    private Integer subEmail;

    private Integer subSms;

    private Integer subTel;

    private String siteId;

    private String alarmLevel;

}
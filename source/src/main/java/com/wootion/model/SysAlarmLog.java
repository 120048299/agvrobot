package com.wootion.model;

import lombok.Data;

import java.util.Date;

/**
 * @Author: majunhui
 * @Date: 2018/12/20
 * @Version 1.0
 */
@Data

public class SysAlarmLog {
    private String uid;
    private String alarmCode;
    private Integer sourceType;
    private String sourceTypeName;
    private String sourceId;
    private String sourceName;
    private String alarmType;
    private String alarmTypeName;
    private Integer alarmLevel;
    private String alarmLevelName;
    private String alarmExp;
    private Date alarmTime;
    private Date removeTime;
    private Integer status;
    private String statusName;
    private String description;
    private String siteId;
}

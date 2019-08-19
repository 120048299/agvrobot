package com.wootion.model;

import lombok.Data;

/**
 * @Author: majunhui
 * @Date: 2018/12/20
 * @Version 1.0
 */
@Data
public class SysAlarmConfig {
    private String uid;
    private String alarmCode;
    private Integer sourceType;
    private String alarmType;
    private Integer alarmLevel;
    private String alarmExp;
}
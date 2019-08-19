package com.wootion.model;


import lombok.Data;

@Data
public class AlarmExpression {
    private String uid;

    private String alarmCodeId;

    private Integer alarmLevel;

    private String description;

}
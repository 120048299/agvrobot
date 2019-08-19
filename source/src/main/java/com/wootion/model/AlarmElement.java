package com.wootion.model;

import lombok.Data;

@Data
public class AlarmElement {
    private String uid;

    private String alarmExpressionId;

    private Integer elementOrder;

    private String element;

}
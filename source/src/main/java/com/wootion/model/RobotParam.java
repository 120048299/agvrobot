package com.wootion.model;

import lombok.Data;

@Data
public class RobotParam {
    private String uid;
    private String robotId;
    private String name;
    private String key;
    private String value;
    private int editable;
    private String desc;
}

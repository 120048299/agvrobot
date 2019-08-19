package com.wootion.model;

import lombok.Data;

@Data
public class AlarmCode {
    private String uid;

    private String code;
    private String name;
    private String regzObjectId;
    private Integer templateType;
    private String siteId;

}
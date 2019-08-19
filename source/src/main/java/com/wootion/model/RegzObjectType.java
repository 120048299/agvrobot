package com.wootion.model;

import lombok.Data;

import java.util.List;

@Data
public class RegzObjectType {
    private String uid;

    private String code;

    private String name;

    private Integer opsType;

    private Integer meterType;

    private String memo;

}
package com.wootion.model;

import lombok.Data;

import java.util.Date;

@Data
public class FindScaleResult {
    private String uid;
    private String taskLogId;
    private Date findScaleTime;
    private Integer status;
    private String findResult;
}
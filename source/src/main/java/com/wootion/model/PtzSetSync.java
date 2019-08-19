package com.wootion.model;

import lombok.Data;

import java.util.Date;

@Data
public class PtzSetSync {
    private String uid;

    private String robotId;

    private String ptzSetId;

    private Integer syncStatus;

    private Date syncTime;
}
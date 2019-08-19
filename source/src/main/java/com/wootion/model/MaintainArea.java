package com.wootion.model;

import java.util.Date;
import lombok.Data;

@Data
public class MaintainArea {
    private String uid;

    private String name;

    private Date modifyTime;

    private String point1;

    private String point2;

    private String point3;

    private String point4;

    private String point5;

    private Integer maintainType;

    private String memo;

    private String siteId;
}
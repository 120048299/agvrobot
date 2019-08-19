package com.wootion.model;

import lombok.Data;

import java.util.List;

@Data
public class RunMark {
    private String uid;

    private String markName;

    private Integer status;

    private Double lon; //数据库记录的导航坐标

    private Double lat;

    private String siteId;

    private Double mapLon;//地图坐标

    private Double mapLat;

    private List<PtzSet> ptzSetList; // 关联ptzSet信息

    private boolean existRoute; // 存在经过该点的线路

    private Integer moveStyle;

}
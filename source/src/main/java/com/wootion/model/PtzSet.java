package com.wootion.model;

import lombok.Data;

import java.util.List;

@Data
public class PtzSet {
    private String uid;

    private Integer ptzType;
    private Float robotAngle;
    private Integer ptzPan;
    private Integer ptzTilt;

    private String areaId;
    private String markId;
    private String description;

    private Integer scan;
    private String siteId;
    private Integer status;
    private Integer setted;

    public String toShortString() {
        return " ptzSet:uid="+uid+",name="+description+" ";
    }

}
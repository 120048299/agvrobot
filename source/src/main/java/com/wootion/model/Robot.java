package com.wootion.model;

import lombok.Data;

/**
 * @Author: majunhui
 * @Date: 2018/12/20
 * @Version 1.0
 */
@Data
public class Robot {
    private String uid;
    private String code;
    private String name;
    private Integer status;
    private String robotIp;
    private Integer robotPort;
    private String videoAddr;
    private String thermalAddr;
    private Integer thermalType;
    private String siteId;
    private String description;
    private String siteName;
    private String statusString;
    private String thermalTypeString;

}

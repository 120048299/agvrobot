package com.wootion.model;

import lombok.Data;

/**
 * @Author: majunhui
 * @Date: 2018/12/20
 * @Version 1.0
 */
@Data
public class WeatherStation {
    private String uid;
    private String code;
    private String name;
    private Integer status;
    private String addr;
    private String siteId;
    private String description;
}

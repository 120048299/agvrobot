package com.wootion.model;

import lombok.Data;

/**
 * @Author: majunhui
 * @Date: 2018/12/20
 * @Version 1.0
 */
@Data
public class Site {
    private String uid;
    private String code;
    private String name;
    private Integer status;
    private String pic;
    private Integer width;
    private Integer height;
    private Double resolution;
    private Double originX;
    private Double originY;
    private Double scale;
    private Double rotation;
    private String description;
}

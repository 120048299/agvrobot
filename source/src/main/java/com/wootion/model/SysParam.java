package com.wootion.model;

import lombok.Data;

/**
 * @Author: majunhui
 * @Date: 2018/12/20
 * @Version 1.0
 */
@Data
public class SysParam {
    private String uid;
    private String name;
    private String key;
    private String value;
    private int editable;
    private String desc;
}

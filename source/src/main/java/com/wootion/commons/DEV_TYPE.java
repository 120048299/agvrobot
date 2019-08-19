package com.wootion.commons;

public enum DEV_TYPE {
    // params: robotIp,robotName,
    DT_ROBOT("1"),

    // params:imageurl,
    DT_SITE_500KV("100"),
    DT_SITE_220KV("101"),
    DT_SITE_110KV("102"),

    // params:ip,port,
    DT_HEAT("200"),

    // params:ip,port,username,password
    DT_CAMERA("210"),


    DT_CLOUD_HEAD("220");

    private final String value;

    DEV_TYPE(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }


}

package com.wootion.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties()
public class SysParam {

    @Value("${ros.isMainServer}")
    public int isMainServer;

    @Value("${ros.dataCacheTime}")
    public int dataCacheTime;

    @Value("${foreignDetect.existPath}")
    public String foreignDetectExistPath;

    @Value("${foreignDetect.nonePath}")
    public String foreignDetectNonePath;

}
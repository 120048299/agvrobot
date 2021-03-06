package com.wootion.agvrobot.utils;

import org.slf4j.LoggerFactory;

public class HeartBeatLog {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HeartBeatLog.class);



    public static void debug(String text){
        logger.debug(text);
    }

    public static void error(String text){
        logger.error(text);
    }

    public static void info(String text){
        logger.info(text);
    }


}


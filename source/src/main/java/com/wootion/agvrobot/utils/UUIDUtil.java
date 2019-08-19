package com.wootion.agvrobot.utils;

import java.util.UUID;

public class UUIDUtil {

    /**
     * 产生UUI.
     *
     * @return the uuid
     */
    public static String getUUID() {
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replaceAll("-", "");
        return uuid;
    }

}

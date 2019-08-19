package com.wootion.commons;

public enum ALARM_SOURCE_TYPE {

    SYSTEM(0), ROBOT(1), CHARGE_ROOM(2),WEATHER_STATION(3);


    private final int value;

     ALARM_SOURCE_TYPE(int i) {
            value = i;
    }

    public final int getValue() {
        return value;
    }

    public static ALARM_SOURCE_TYPE fromInt(int i) {
        for (ALARM_SOURCE_TYPE b : ALARM_SOURCE_TYPE.values()) {
            if (b.getValue() == i) {
                return b;
            }
        }
        return null;
    }

    public String toStrValue() {
        switch (value) {
            case 0:
                return "系统";
            case 1:
                return "机器人";
            case 2:
                return "充电房";
            case 3:
                return "气象站";
        }
        return "未知";
    }
}

package com.wootion.commons;

public enum ALARM_LEVEL {

    FAILED(-1),NORMAL(0), EARLY(1), COMMONLY(2),MAJOR(3),DANGER(4);


    private final int value;

     ALARM_LEVEL(int i) {
            value = i;
    }

    public final int getValue() {
        return value;
    }

    public static ALARM_LEVEL fromInt(int i) {
        for (ALARM_LEVEL b : ALARM_LEVEL.values()) {
            if (b.getValue() == i) {
                return b;
            }
        }
        return null;
    }

    public String toStrValue() {
        switch (value) {
            case -1:
                return "识别异常";
            case 0:
                return "正常";
            case 1:
                return "预警";
            case 2:
                return "一般告警";
            case 3:
                return "严重告警";
            case 4:
                return "危急告警";
        }
        return "未知";
    }
}

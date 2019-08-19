package com.wootion.commons;

public enum TASK_LOG_RESULT {

    SUCCESS(0),
    MOVE_FAILED(-2),
    TAKE_CANCELED(-6),
    TAKE_DELETED(-7),
    FIND_PATH_FAILED(-8),
    TAKE_TIMEOUT(-9),

    // >10 为各种业务失败
    FIND_METER_FAILED(-11),
    TAKE_PICTURE_FAILED(-12),
    READ_METER_FAILED(-13),
    READ_INFRARED_FAILED(-14),

    OTHER_REASON(-1);

    private final int value;

     TASK_LOG_RESULT(int i) {
            value = i;
    }

    public final int getValue() {
        return value;
    }

    public static TASK_LOG_RESULT fromInt(int i) {
        for (TASK_LOG_RESULT b : TASK_LOG_RESULT.values()) {
            if (b.getValue() == i) {
                return b;
            }
        }
        return null;
    }

    public String toStrValue() {
        return "未知";
    }
}

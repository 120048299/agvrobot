package com.wootion.commons;

public enum TASK_AUDIT_STATUS {

    NOTAUDITED(0), AUDITED(1);
    private final int value;

    TASK_AUDIT_STATUS(int i) {
        value = i;
    }

    public final int getValue() {
        return value;
    }

    public static TASK_AUDIT_STATUS fromInt(int i) {
        for (TASK_AUDIT_STATUS b : TASK_AUDIT_STATUS.values()) {
            if (b.getValue() == i) {
                return b;
            }
        }
        return null;
    }

    public String toStrValue() {
        switch (value) {
            case 1:
                return "已审核";
        }
        return "未审核";
    }
}
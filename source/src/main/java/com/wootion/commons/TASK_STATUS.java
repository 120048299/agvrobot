package com.wootion.commons;

public enum TASK_STATUS {

    NNOTACTIVE(-1), DEACTIVE(0),ACTIVE(1),DELETED(2),FINISHED(4),ExpiredStop(7);
    private final int value;
    //0-去激活 1-已激活 2-删除 3-完成
     TASK_STATUS(int i) {
            value = i;
    }

    public final int getValue() {
        return value;
    }

    public static TASK_STATUS fromInt(int i) {
        for (TASK_STATUS b : TASK_STATUS.values()) {
            if (b.getValue() == i) {
                return b;
            }
        }
        return null;
    }

    public String toStrValue() {
        switch (value) {
            case -1:
                return "未启动";
            case 0:
                return "去激活";
            case 1:
                return "已激活";
            case 2:
                return "删除";
            case 3:
                return "完成";
            case 7:
                return "已超期";
        }
        return "未知";
    }
}

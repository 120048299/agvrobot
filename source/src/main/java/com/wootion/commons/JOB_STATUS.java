package com.wootion.commons;

public enum JOB_STATUS {
    //0-等待执行; 1-正在执行; 21-手工暂停; 3-取消; 4-执行完成,5-终止,7-任务超期',
    NOTSTART(0), EXECUTING(1),MANUAL_PAUSED(21),AUTO_PAUSED(22),CANCELLED(3),FINISHED(4),TERMINATION(5),ExpiredStop(7);
    private final int value;

     JOB_STATUS(int i) {
            value = i;
    }

    public final int getValue() {
        return value;
    }

    public static JOB_STATUS fromInt(int i) {
        for (JOB_STATUS b : JOB_STATUS.values()) {
            if (b.getValue() == i) {
                return b;
            }
        }
        return null;
    }

    public String toStrValue() {
        switch (value) {
            case 0:
                return "等待执行";
            case 1:
                return "正在执行";
            case 21:
            case 22:
                return "暂停中";
            case 3:
                return "取消";
            case 5:
                return "终止";
            case 4:
                return "执行完成";
            case 7:
                return "任务超期";
        }
        return "未知";
    }
}

package com.wootion.commons;

public enum TASK_EXEC_STATUS {
    NOT_START(-1), //未启动
    NOT_EXECUTE(0), //未执行
    EXECUTING(1), //正在执行
    EXECUTE_SUCCESS(2), //执行成功
    GIVE_UP(3), //暂时放弃
    DELETED(4), //已删除
    SUSPEND(5), //暂停
    OFFLINE_SUSPEND(6), //离线暂停
    EXECUTE_FAILED(7), //执行失败
    FIND_SUCCESS(8), //找表成功
    READ_SUCCESS(9), //待确认
    CONFIRMING(10); //确认中

    private final int value;

     TASK_EXEC_STATUS(int i) {
            value = i;
    }

    public final int getValue() {
        return value;
    }

    public static TASK_EXEC_STATUS fromInt(int i) {
        for (TASK_EXEC_STATUS b : TASK_EXEC_STATUS.values()) {
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
                return "未执行";
            case 1:
                return "正在执行";
            case 2:
                return "执行成功";
            case 3:
                return "暂时放弃";
            case 4:
                return "已删除";
            case 5:
                return "暂停";
            case 6:
                return "离线暂停";
            case 7:
                return "执行失败";
            case 8:
                return "找表成功";
            case 9:
                return "待确认";
            case 10:
                return "确认中";
        }
        return "未知";
    }
}

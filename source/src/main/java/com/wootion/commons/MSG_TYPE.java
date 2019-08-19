package com.wootion.commons;

public enum MSG_TYPE {
    MT_STATUS(0),MT_MONITOR(1), MT_RELEASE_MONITOR(2),
    MT_GET_CONTROL(3), MT_GET_TASK_MODE(4),
    MT_GET_EMERGENCY(5),
    MT_RELEASE_ALL(6),
    MT_TASKSTATUS(10),
    MT_CONTROL_FORWARD(11), MT_CONTROL_BACKWARD(12), MT_CONTROL_LEFT(13), MT_CONTROL_RIGHT(14), 
    MT_CONTROL_STOP(15),MT_CONTROL_TERRACE(21),MT_CONTROL_CAMERA(22)

    ;
    
    // 响应消息+10000

    private final int value;

    MSG_TYPE(int i) {
        value = i;
    }

    public final int getValue() {
        return value;
    }

    public static MSG_TYPE fromInt(int i) {
        for (MSG_TYPE b : MSG_TYPE.values()) {
            if (b.getValue() == i) {
                return b;
            }
        }
        return null;
    }

    public String toStrValue() {
        switch (value) {
            case 0:
                return "状态";
            case 1:
                return "监控机器人";
            case 2:
                return "释放机器人监控";
            case 3:
                return "后台遥控机器人";
            case 4:
                return "进入任务模式";
            case 5:
                return "获取紧急定位";
            case 6:
                return "释放紧急定位";
            case 10:
                return "任务状态";
            case 11:
                return "机器人前进";
            case 12:
                return "机器人后退";
            case 13:
                return "机器人左转";
            case 14:
                return "机器人右转";
            case 15:
                return "机器人停止";
            case 21:
                return "控制云台";
            case 22:
                return "控制摄像头";
            default:
            return "";
        }
    }
}

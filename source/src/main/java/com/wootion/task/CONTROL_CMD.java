package com.wootion.task;


public enum CONTROL_CMD {
    IDLE_CMD(0, 0), // 空闲
    REMOTE_GET_CMD(1, 1), // 进入后台遥控模式
    REMOTE_RELEASE_CMD(1, 2), //退出后台遥控模式
    REMOTE_MOVE_CMD(1, 3), // 远程遥控操作,移动指令

    WEB_MOVE_PTZ(4,1),// ctrl 4 云台系列

    MOVE_ROUGHLY(2, 1),//粗略移动
    MOVE_PRECISELY_WITH_ANGLE(2, 2), //精确移动，带方向
    MOVE_PRECISELY_NO_ANGLE(2,9),  //精确移动，无方向
    MOVE_PRECISELY_BACK_WITH_ANGLE(2, 10), //精确移动，倒退，带方向
    MOVE_PRECISELY_BACK_NO_ANGLE(2,11),  //精确移动，倒退，无方向
    PREVENT_FALL_CLOSE(2,5),PREVENT_FALL_OPEN(2,6),//防跌落开关控制
    PREVENT_CRASH_CLOSE(2,7),PREVENT_CRASH_OPEN(2,8),//防碰撞开关控制

    CHARGE_MOVE(3,1),
    CHARGE_ABSORB(3,2),//充电接触器吸合
    CHARGE_OPEN(3,3), // 充电接触器打开

    WARNING(5,1),

    OFFLINE(6,1),

    TERRACE_LEFT(4,2),TERRACE_RIGHT(4,3),TERRACE_UP(4,4),TERRACE_DOWN(4,5),TERRACE_STOP(4,11),TERRACE_FOCUS_ENLARGE(4,6),
    TERRACE_FOCUS_ENSMALL(4,7),TERRACE_SET_AUTO_FOCUS(4,8),TERRACE_FOCUS_NEAR(4,9),TERRACE_FOCUS_FAR(4,10),
   TERRACE_PHOTO(4,12), TERRACE_FOCUS_STOP(4,13),TERRACE_SET_MANUAL_FOCUS(4,14), TERRACE_OPEN_LIGHT(4,15),TERRACE_OFF_LIGHT(4,16),
    TERRACE_OPEN_WIPER(4,17),TERRACE_OFF_WIPER(4,18)
    ;
    private final int value;
    CONTROL_CMD(int ctrl, int cmd) {
        this.value = (ctrl<<16) | cmd;
    }

    public final int getValue() {
        return value;
    }

    public static CONTROL_CMD fromInt(int i) {
        for (CONTROL_CMD b : CONTROL_CMD.values()) {
            if (b.getValue() == i) {
                return b;
            }
        }
        //未知的都算空闲
        return IDLE_CMD;
    }

    public static CONTROL_CMD fromInt(int ctrl, int cmd) {
        int ctrl2 = (ctrl << 16) | cmd;
        return fromInt(ctrl2);
    }
    public static CONTROL_CMD fromShort(Short ctrl, Short cmd) {
        int ctrl1=ctrl & 0xFFFF;
        int cmd1=cmd & 0x7FFF;  //返回的最高位标志，表示正常。但不能作为状态, 8001  改为0001
        int ctrl2 = (ctrl1 << 16) | cmd1;
        return fromInt(ctrl2);
    }

    public String toStrValue() {
        switch (value) {
            case 0:
                return "空闲";
            case 0x00010001:
                return "手柄控制";
            case 0x00010002:
                return "远程手柄控制";
            case 0x00020001:
                return "移动任务";
            case 0x00020002:
                return "移动任务和云台";
            case 0x00030001:
                return "去充电房";
            case 0x00030002:
                return "充电状态";
            case 0x00030003:
                return "充电状态";
            case 0x00040001:
                return "获取云台巡检点坐标";
            case 0x00040002:
                return "云台左转动";
            case 0x00040003:
                return "云台右转动";
            case 0x00040004:
                return "云台上转动";
            case 0x00040005:
                return "云台下转动";
            case 0x00040006:
                return "调焦放大";
            case 0x00040007:
                return "调焦缩小";
            case 0x00040008:
                return "自动聚焦";
            case 0x00040009:
                return "手动焦距拉近";
            case 0x0004000a:
                return "手动焦距拉远";
            case 0x0004000b:
                return "云台停止";
            case 0x0004000c:
                return "拍照";
        }

        return "";
    }

    public Short getCtrl() {
        return (short)(this.value>>16);
    }
    public Short getCmd() {
        return (short)(this.value & 0xffff);
    }



}

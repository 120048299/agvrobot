package com.wootion.commons;

public enum OPS_TYPE {

        READ_SCALE(1), POSTION_STATE(2),APPEARANCE_LIGHT(3),INFRA(4),APPEARANCE_DATA(5),SOUND(6);


    private final int value;

     OPS_TYPE(int i) {
            value = i;
    }

    public final int getValue() {
        return value;
    }

    public static OPS_TYPE fromInt(int i) {
        for (OPS_TYPE b : OPS_TYPE.values()) {
            if (b.getValue() == i) {
                return b;
            }
        }
        return null;
    }

    public String toStrValue() {
        switch (value) {
           /* case 1:
                return "表计读取";
            case 2:
                return "红外测温";
            case 3:
                return "外观查看（可见光）";
            case 4:
                return "位置状态";
            case 5:
                return "外观查看（数据）";
            case 6:
                return "声音检测";*/
           case 1:
                return "表计读取";
            case 2:
                return "位置状态";
            case 3:
                return "外观查看（可见光）";
            case 4:
                return "红外测温";
            case 5:
                return "外观查看（数据）";
            case 6:
                return "声音检测";
        }
        return "未知";
    }
}

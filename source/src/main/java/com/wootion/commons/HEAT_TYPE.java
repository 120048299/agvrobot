package com.wootion.commons;

public enum HEAT_TYPE {
    Unknown(0),ElectricCurrentHeatType(1), VoltageHeatType(2);
    private final int value;

    HEAT_TYPE(int i) {
        value = i;
    }
    public final int getValue() {
        return value;
    }

    public static HEAT_TYPE fromInt(int i) {
        for (HEAT_TYPE b : HEAT_TYPE.values()) {
            if (b.getValue() == i) {
                return b;
            }
        }
        return null;
    }

    public String toStrValue() {
        switch (value) {
            case 1:
                return "电流致热型";
            case 2:
                return "电压致热型";
        }

        return "未知";
    }
}

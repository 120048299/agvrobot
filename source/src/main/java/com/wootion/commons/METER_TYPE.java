package com.wootion.commons;

public enum METER_TYPE {

        Unknown(0),OilPositionMeter(1), LightningProtector(2),LeakageCurrentMeter(3),SF6PressureMeter(4),
    SwitchActionCounter(5),GradePlaceMeter(6), GasPressureMeter(7),HydraulicPressureGauge(8),Thermometer(9),
    MoistureAbsorber(10);

    private final int value;

     METER_TYPE(int i) {
            value = i;
    }

    public final int getValue() {
        return value;
    }

    public static METER_TYPE fromInt(int i) {
        for (METER_TYPE b : METER_TYPE.values()) {
            if (b.getValue() == i) {
                return b;
            }
        }
        return null;
    }

    public String toStrValue() {
        switch (value) {
            case 0:
                return "无";
            case 1:
                return "油位表";
            case 2:
                return "避雷器动作次数表";
            case 3:
                return "泄漏电流表";
            case 4:
                return "SF6压力表";
            case 5:
                return "开关动作次数表";
            case 6:
                return "档位表";
            case 7:
                return "气压表";
            case 8:
                return "液压表";
            case 9:
                return "温度表";
            case 10:
                return "吸湿器";
            default:
                return "未知";
        }
    }
}

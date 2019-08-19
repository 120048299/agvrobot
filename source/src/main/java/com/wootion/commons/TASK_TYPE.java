package com.wootion.commons;

public enum TASK_TYPE {

    Total("1"), Routine("2"), Specail("3"),Particular("4"),UserDefined("5"),
    SpecialInfra("31"),SpecialOil("32"),SpecialArrester("33"),
    SpecialSF("34"),SpecialHydraulic("35"),SpecialPositon("36"),
    ParticularBadWether("41"),ParticularFaults("42"),ParticularRemoteAbnormal("43"),
    ParticularRemoteStatus("44"),ParticularSecure("45"),
    ParticularSummer("411"),ParticularStorm("412"),
    ParticularTyphoon("413"),ParticularRainSnow("414"),
    ParticularFog("415"),ParticularStrongWind("416"),
    ParticularEmergency("46"),
    ;


    private final String value;

     TASK_TYPE(String i) {
            value = i;
    }

    public final String getValue() {
        return value;
    }

    public static TASK_TYPE fromString(String s) {
        for (TASK_TYPE b : TASK_TYPE.values()) {
            if (b.getValue().equals(s)) {
                return b;
            }
        }
        return null;
    }

    public String toStrValue() {
        switch (value) {
            case "1":
                return "全面巡检";
            case "2":
                return "例行巡检";
            case "3":
                return "专项巡检";
            case "4":
                return "特殊巡检";
            case "5":
                return "自定义巡检";
            case "31":
                return "红外测温专项巡视";
            case "32":
                return "油温油位表专项巡视";
            case "33":
                return "避雷器表计专项巡视";
            case "34":
                return "SF6压力表专项巡视";
            case "35":
                return "液压表专项巡视";
            case "36":
                return "位置状态识别专项巡视";
            case "41":
                return "恶劣天气特巡";
            case "42":
                return "缺陷跟踪特巡";
            case "43":
                return "远方异常告警确认特巡";
            case "44":
                return "远方状态确认特巡";
            case "45":
                return "安防联动特巡";
            case "411":
                return "恶劣天气迎峰度夏特巡";
            case "412":
                return "恶劣天气雷暴天气特巡";
            case "413":
                return "恶劣天气防汛抗台特巡";
            case "414":
                return "恶劣天气雨雪冰冻特巡";
            case "415":
                return "恶劣天气雾霾特巡";
            case "416":
                return "恶劣天气大风特巡";
            case "46":
                return "协助应急事故处理";

        }
        return "其他";
    }
}

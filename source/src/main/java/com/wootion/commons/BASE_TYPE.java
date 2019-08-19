package com.wootion.commons;

public enum BASE_TYPE {
    CIRCUITBREAKER("CircuitBreaker"), OILFILLEDTRANSFORMER("oilFilledTransformer"), ROBOT("Robot"), SITE("Site"), CELECAPPARATUS("CElecApparatus")
    , ISOLATINGSWITCH("IsolatingSwitch"), SAWITCHCABINET("SwitchCabinet"), CURRENTTRANSFORMER("CurrentTransformer"), VOLTAGETRANSFORMER("VoltageTransformer"),
    ARRESTER("Arrester"), SHUNTCAPACITOR("ShuntCapacitor"), DRYREACTOR("DryReactor"), BUSBARINSULATOR("BusBarInsulator"), WALLCASING("WallCasing"),
    POWERCABLE("PowerCable"), EXTINCTIONCOIL("ExtinctionCoil"), HFWAVAHINDRER("HFwaveHindrer"), COUPLINGCAPACITOR("CouplingCapacitor"),
    HVOLTAGEFUSE("HvoltageFuse"), GROUNDDEVICE("GroundDevice"), TERMINALBOX("TerminalBox"), STATIONTRANSFORMER("StationTransformer"), STATIONACPOWER("StationACpower")
    , STATIONDCPOWER("StationDCpower"), EQUIPMENTFRAME("EquipmentFrame"), AUXILIARYFACILITIES("AuxiliaryFacilities"), CIVILCONSTRUCTION("CivilConstruction")
    , INDELIGHTNINGROD("IndeLightningRod"), TCSC("TCSC"), NEUTRALPOINTSTRAIGHT("NeutralPointStraight");

    private final String value;
    BASE_TYPE(String i) {
        value = i;
    }

    public final String getValue() {
        return value;
    }

    public static BASE_TYPE fromString(String i) {
        for (BASE_TYPE b : BASE_TYPE .values()) {
            if (b.getValue().equals(i) ) { return b; }
        }
        return null;
    }

    public String toStrValue() {
        switch (value) {
            case "CircuitBreaker":
                return "断路器";
            case "oilFilledTransformer":
                return "油浸式变压器（电抗器）";
            case "Robot":
                return "机器人";
            case "Site":
                return "站点";
            case "CElecApparatus":
                return "组合电器";
            case "IsolatingSwitch":
                return "隔离开关";
            case "SwitchCabinet":
                return "开关柜";
            case "CurrentTransformer":
                return "电流互感器";
            case "VoltageTransformer":
                return "电压互感器";
            case "Arrester":
                return "避雷器";
            case "ShuntCapacitor":
                return "并联电容器";
            case "DryReactor":
                return "干式电抗器";
            case "BusBarInsulator":
                return "母线及绝缘子";
            case "WallCasing":
                return "穿墙套管";
            case "PowerCable":
                return "电力电缆";
            case "ExtinctionCoil":
                return "消弧线圈";
            case "HFwaveHindrer":
                return "高频阻波器";
            case "CouplingCapacitor":
                return "耦合电容器";
            case "HvoltageFuse":
                return "高压熔断器";
            case "GroundDevice":
                return "接地装置";
            case "TerminalBox":
                return "端子箱及检修电源箱";
            case "StationTransformer":
                return "站用变压器";
            case "StationACpower":
                return "站用交流电源系统";
            case "StationDCpower":
                return "站用直流电源系统";
            case "EquipmentFrame":
                return "设备构架";
            case "AuxiliaryFacilities":
                return "辅助设施";
            case "CivilConstruction":
                return "土建设施";
            case "IndeLightningRod":
                return "独立避雷针";
            case "TCSC":
                return "串联补偿装置";
            case "NeutralPointStraight":
                return "中性点隔直（限直）装置";
        }
        return "";
    }
}

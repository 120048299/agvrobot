package com.wootion.agvrobot.dto;

import com.wootion.commons.DEV_STATUS;
import com.wootion.model.Dev;

public class InfraredImager extends Dev {
    private String ip;
    DEV_STATUS thermalImagerStatus ;

    public InfraredImager(Dev d) {
        super(d);
        thermalImagerStatus = DEV_STATUS.fromInt(d.getStatus());
        ip = d.getParams().split(",")[0];

    }

    @Override
    public String getParams() {
        return String.format("%s", this.ip);
    }

    public Dev getDev() {
        Dev d = new Dev(this);
        return d;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public DEV_STATUS getThermalImagerStatus() {
        thermalImagerStatus = DEV_STATUS.fromInt(this.getStatus());
        return thermalImagerStatus;
    }

    public void setThermalImagerStatus(DEV_STATUS thermalImagerStatus) {
        this.thermalImagerStatus = thermalImagerStatus;
        this.setStatus(thermalImagerStatus.getValue());
    }
}

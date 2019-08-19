package com.mock.RoomServer;

/**
 * biao
 */
public class ChargeRoom {

    /**摄氏温度，整型数据*/
    private int temp;
    /**相对湿度：0~100（不带“ % ”）*/
    private int hum;
    /**是否正在充电(状态) 1，机器人正在充电*/
    private int isChargeing;
    /**卷帘门是否已经打开 0，未完全打开；1，已经完全打开*/
    private int isDoorOpened;
    /**卷帘门是否已经关闭 0，未完全关闭；1，已经完全关闭*/
    private int isDoorClosed;
    /**PLC运行状态 0，PLC运行正常；1，PLC运行异常*/
    private int plcStatus;
    /**充电房判别是否对接可靠 0，未与充电桩对接；1，已与充电桩对接*/
    private int chargeRoomAbtment;

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public int getHum() {
        return hum;
    }

    public void setHum(int hum) {
        this.hum = hum;
    }

    public int getIsChargeing() {
        return isChargeing;
    }

    public void setIsChargeing(int isChargeing) {
        this.isChargeing = isChargeing;
    }

    public int getIsDoorOpened() {
        return isDoorOpened;
    }

    public void setIsDoorOpened(int isDoorOpened) {
        this.isDoorOpened = isDoorOpened;
    }

    public int getIsDoorClosed() {
        return isDoorClosed;
    }

    public void setIsDoorClosed(int isDoorClosed) {
        this.isDoorClosed = isDoorClosed;
    }

    public int getPlcStatus() {
        return plcStatus;
    }

    public void setPlcStatus(int plcStatus) {
        this.plcStatus = plcStatus;
    }

    public int getChargeRoomAbtment() {
        return chargeRoomAbtment;
    }

    public void setChargeRoomAbtment(int chargeRoomAbtment) {
        this.chargeRoomAbtment = chargeRoomAbtment;
    }

    @Override
    public String toString() {
        return "ChargeRoom{" +
                "temp=" + temp +
                ", hum=" + hum +
                ", isChargeing=" + isChargeing +
                ", isDoorOpened=" + isDoorOpened +
                ", isDoorClosed=" + isDoorClosed +
                ", plcStatus=" + plcStatus +
                ", chargeRoomAbtment=" + chargeRoomAbtment +
                '}';
    }
}

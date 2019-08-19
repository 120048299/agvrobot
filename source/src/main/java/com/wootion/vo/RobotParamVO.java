package com.wootion.vo;
import lombok.Data;

@Data
public class RobotParamVO {
    private String robotId;
    private double speed;
    private double wheelDiameter;
    private double disWheelAndCenter;
    private double terraceX;
    private double terraceY;
    private double terraceDisX;
    private double terraceDisY;
    private int controlMode;
    private int infraredUsed;
    private int imageUsed;
    private int wiperUsed;
    private int avoidanceUsed;
    private int lightingUsed;
    private int chargeRoomUsed;
    private int robotStatusUsed;

    //以下为系统参数
    private double batteryMin;
    private int warnStyle;
    private int stopStyle;
    private double radarDisAlarm;

}
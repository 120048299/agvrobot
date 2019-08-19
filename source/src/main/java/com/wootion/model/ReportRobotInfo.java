package com.wootion.model;

import lombok.Data;

@Data
public class ReportRobotInfo {
    private int robotTemperature;   //机身温度
    private double robotSpeed;        //机器人速度
    private double terraceHorizontal; //云台水平位置
    private double terraceVertical; //云台垂直位置
    private double opticalZoom; // 相机倍数
    private int wirelessTower;//无线基站
    private int controlSystem;//控制系统
    private int imagePickup;//可见光摄像
    private int chargeSystem;//充电系统
    private int infraredPickup; //红外摄像
    private double batteryQuantity; // 电池电量
    private double leftWheelSpeed;  //左轮速度
    private double rightWheelSpeed; //右轮速度
    private double externalPowerCurrent; //外供电源电流
    private double externalPowerVoltage;//外供电源电压
    private double charge;       //充电
    private String chargeStatus;//充电状态
    private double totalMileage;  //运行里程
    private int totalInspectiDevs; //巡检设备总数
    private double totalRunTime;//运行时间
    private  int totalDefects;  //发现缺陷数
    private double envTemperature; //环境温度
    private double envHumidity;//环境湿度
    private double windSpeed; //风速
}

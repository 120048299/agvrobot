package com.wootion.vo;


import com.alibaba.fastjson.JSONArray;
import com.wootion.agvrobot.utils.NumberUtil;
import com.wootion.model.UserInfo;
import com.wootion.protocols.robot.msg.RobotInfo;
import com.wootion.robot.MemRobot;
import lombok.Data;

/**
 *
 机器人状态：
 运行状态信息： 当前坐标  运行速度  云台水平位置： 云台垂直位置   相机倍数  聚焦位置   最近点位

 通信状态信息：无线基站。可见光摄像，红外摄像，气象站

 电池状态信息：电池电量，电池电压，充电状态
 天气状态信息：温度 C 湿度  %RH   风速m/s

 本体温度信息： 四个电机温度 运动控制主板CPU温度 工控机CPU温度 电池BMS板温度

 总运行累积：运行里程  运行时间
 机器人控制：
 控制状态信息：红外  可见光  雨刷 避障 补光灯 充电房
 */
@Data
public class RobotStatusInfo  {

    boolean online  =false;
    boolean waring  =false;
    boolean stopped =false;
    boolean navLost =true;
    int emergency   =0;
    int robotMode   = -1;
    UserInfo userController;
    int stopInPlace;

    private int chargeFlowStatus =0;
    private int charging=0;
    String chargeStatus;//用于界面显示充电准备
    //运行状态信息：
    Float[] position=new Float[2]; //当前坐标x,y
    Float velocityX; //运行速度
    Float velocityYaw;//运行线速度
    Float orientation=0f;//角度
    Float ptzHorizontal;//云台水平角度
    Float ptzVertical;//云台俯仰角度
    Float cameraAmplify; //相机放大倍数
    Short cameraFocus; //相机焦距
    //最近点位


    //网络状态
    short netAP;//无线AP状态  wifi_strength  强度值
    short netCamera;//可见光摄像仪
    short netInfrar;//红外摄像仪
    short netWeatherStation;//气象站

    //电池状态
    short batteryVolume;//电池电量
    double batteryVotage;//电池电压,V
    int batteryCharging;//充电状态


    //天气状态
    double weatherTemperature;//温度C
    double humidity;//湿度
    double rainFall;//雨量
    double windSpeed;//风速
    int isRain;//是否有雨雪

    //本体温度
    short[] tempMotor=new short[4];//电机温度 4个
    short tempMotionControllCpu; //运动控制主板CPU
    short tempBatteryBms;//电池BMS板
    short tempIpcCpu; //工控机主板CPU

    //当前任务状态

    //总运行累积：运行里程  运行时间  ////没有提供
    float totalMileage;//总里程
    long totalRunTime;//总运行时间
    float currentMileage;//本次开机里程
    long currentRunTime;//本次开机运行时间

    Short control_mode;
    Short cmd_mode;


    public RobotStatusInfo() {

    }
    /**
     * 用接口信息初始化
     * @param robotInfo
     */
    public void setRobotInfo (RobotInfo robotInfo){
        this.position = robotInfo.getPosition();
        this.velocityX = robotInfo.getVelocity_x();
        this.velocityYaw = robotInfo.getVelocity_yaw();
        batteryVolume = robotInfo.getBattery_quantity();
        short uint16=robotInfo.getBattery_voltage();
        this.batteryVotage =NumberUtil.getUint16(uint16)/10.0;
        int []bits = NumberUtil.byteToBitArray(robotInfo.getBattery_status());;
        this.batteryCharging =bits[4];
        Byte temperature[] = robotInfo.getTemperature();
        if(temperature!=null){
            tempMotor[0] = temperature[1];//电机温度 4个
            tempMotor[1] = temperature[2];
            tempMotor[2] = temperature[3];
            tempMotor[3] = temperature[4];
            tempMotionControllCpu = temperature[0]; //运动控制主板CPU
            tempBatteryBms = temperature[5];//电池BMS板
            tempIpcCpu = temperature[6]; //工控机主板CPU
        }
    }

    public void setStatuses (MemRobot memRobot){
        this.online = memRobot.isOnline();
        this.waring = memRobot.isWaring();
        this.stopped = memRobot.isStopped();
        this.navLost = memRobot.isNavLost();
        this.emergency = memRobot.getEmergency();
        this.setRobotMode(memRobot.getRobotMode());
        this.userController=memRobot.getUserController();
        this.stopInPlace=memRobot.getStopInPlace();
        this.charging =  memRobot.getCharging();
        this.chargeFlowStatus=memRobot.getChargeFlowStatus();
    }

    public Float[] getPosition() {
        if (position == null) {
            return new Float[]{0f, 0f

            };
        }
        return position;
    }

    public void setPoint(JSONArray position) {
        if (position == null || position.size() == 0) {
            return ;
        }

        if (this.position == null) {
            this.position = new Float[3];
        }

        this.position[0] = ((Double)position.getDouble(0)).floatValue();
        this.position[1] = ((Double)position.getDouble(1)).floatValue();
        this.position[2] = 0.0f;
    }
}

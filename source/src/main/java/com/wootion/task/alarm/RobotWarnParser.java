package com.wootion.task.alarm;

import com.wootion.agvrobot.utils.NumberUtil;
import com.wootion.model.*;
import com.wootion.protocols.robot.msg.RobotInfo;
import com.wootion.utiles.DataCache;

import java.util.*;

public class RobotWarnParser {

    public RobotWarnParser () {
    }

    /**
     * 返回告警信息是否有变化
     * @param robotInfo
     * @param robotId
     * @param siteId
     * @return
     */
    public  boolean parseAlarm(RobotInfo robotInfo, String robotId, String siteId){
        return parseStatus(robotInfo, robotId, siteId);
    }

    // 解析机器人状态信息
    private  boolean parseStatus(RobotInfo robotInfo, String robotId, String siteId){
        List<SysAlarmLog> addList=new ArrayList<>();
        List<SysAlarmLog> removeList=new ArrayList<>();
        // 电量
        int battery = robotInfo.getBattery_quantity();
        checkSysAlarm(addList, removeList, siteId, robotId,"0102", battery, null);

        // 电压  3,4字节为电压值，short, 单位mV
        int voltage = robotInfo.getBattery_voltage()/1000;
        checkSysAlarm(addList, removeList, siteId, robotId,"0103", voltage, null);
        checkSysAlarm(addList, removeList, siteId, robotId,"0104", voltage, null);

        // bit 0-7 欠压 过压 低温 过温 充电中 放电中 预留 电池线路报警
        int []bits=NumberUtil.byteToBitArray(robotInfo.getBattery_status());
        checkSysAlarm(addList, removeList, siteId, robotId,"0105", bits[0], null);
        checkSysAlarm(addList, removeList, siteId, robotId,"0106", bits[1], null);
        checkSysAlarm(addList, removeList, siteId, robotId,"0107", bits[2], null);
        checkSysAlarm(addList, removeList, siteId, robotId,"0108", bits[3], null);
        checkSysAlarm(addList, removeList, siteId, robotId,"0101", bits[7], null);
        //bit 0-7 左前超声波  右前超声波 右后超声波 左后超声波  防撞条  紧急停车  左防跌落 右防跌落
        bits=NumberUtil.byteToBitArray(robotInfo.getStop_status());
        checkSysAlarm(addList, removeList, siteId, robotId,"0401", bits[0], null);
        checkSysAlarm(addList, removeList, siteId, robotId,"0402", bits[1], null);
        checkSysAlarm(addList, removeList, siteId, robotId,"0403", bits[2], null);
        checkSysAlarm(addList, removeList, siteId, robotId,"0404", bits[3], null);
        checkSysAlarm(addList, removeList, siteId, robotId,"0405", bits[4], null);
        checkSysAlarm(addList, removeList, siteId, robotId,"0406", bits[5], null);
        checkSysAlarm(addList, removeList, siteId, robotId,"0407", bits[6], null);
        checkSysAlarm(addList, removeList, siteId, robotId,"0408", bits[7], null);
        //bit 0-3 电缸
        bits=NumberUtil.byteToBitArray(robotInfo.getPump_status());
        checkSysAlarm(addList, removeList, siteId, robotId,"0201", bits[0], null);
        checkSysAlarm(addList, removeList, siteId, robotId,"0202", bits[1], null);
        checkSysAlarm(addList, removeList, siteId, robotId,"0203", bits[2], null);
        checkSysAlarm(addList, removeList, siteId, robotId,"0204", bits[3], null);
        //bit 0-3 电机
        bits=NumberUtil.byteToBitArray(robotInfo.getMotor_status());
        checkSysAlarm(addList, removeList, siteId, robotId,"0301", bits[0], null);
        checkSysAlarm(addList, removeList, siteId, robotId,"0302", bits[1], null);
        checkSysAlarm(addList, removeList, siteId, robotId,"0303", bits[2], null);
        checkSysAlarm(addList, removeList, siteId, robotId,"0304", bits[3], null);
        /*机器人温度
        0 运动控制主板CPU温度
        1 左前电机温度
        2 左后电机温度
        3 右后电机温度
        4 右前电机温度
        5 电池BMS板
        6 工控机CPU温度
        */
        Byte temperature[] = robotInfo.getTemperature();
        if(temperature!=null){
            checkSysAlarm(addList, removeList, siteId, robotId,"0501", temperature[0], null);
            checkSysAlarm(addList, removeList, siteId, robotId,"0305", temperature[1], null);
            checkSysAlarm(addList, removeList, siteId, robotId,"0306", temperature[2], null);
            checkSysAlarm(addList, removeList, siteId, robotId,"0307", temperature[3], null);
            checkSysAlarm(addList, removeList, siteId, robotId,"0308", temperature[4], null);
            checkSysAlarm(addList, removeList, siteId, robotId,"0109", temperature[5], null);
            checkSysAlarm(addList, removeList, siteId, robotId,"0601", temperature[6], null);
        }

        // bit 0-1 定位失效 行进路线偏轨 障碍物
        bits=NumberUtil.byteToBitArray(robotInfo.getNav_status());
        checkSysAlarm(addList, removeList, siteId, robotId,"0801",  bits[0], null);
        checkSysAlarm(addList, removeList, siteId, robotId,"0802",  bits[1], null);
        checkSysAlarm(addList, removeList, siteId, robotId,"0803",  bits[2], null);

        // bit 0-1 可见光无信号 红外无信号
        bits=NumberUtil.intToBitArray(robotInfo.getSensor_status());
        checkSysAlarm(addList, removeList, siteId, robotId,"0003",  bits[0], null);
        checkSysAlarm(addList, removeList, siteId, robotId,"0004",  bits[1], null);

        // 无变化
        if (addList.size()==0 && removeList.size()==0) {
            return false;
        }

        // 新告警
        for (SysAlarmLog item:addList) {
            DataCache.addToSysAlarmLogMap(item);
        }

        // 恢复告警
        for (SysAlarmLog item:removeList) {
            DataCache.removeFromSysAlarmLogMap(item);
        }
        return true;
    }

    private void checkSysAlarm (List<SysAlarmLog> addList, List<SysAlarmLog> removeList,
                                String siteId, String sourceId, String alarmCode, Object value, String desc) {
        SysAlarmLog sysAlarmLog = SysAlarmHelper.checkSysAlarm(siteId, sourceId,alarmCode, value, desc);
        if (sysAlarmLog!=null) {
            if (sysAlarmLog.getStatus()==0) {
                addList.add(sysAlarmLog);
            } else {
                removeList.add(sysAlarmLog);
            }
        }
    }

    public static void main(String args[]){

        String s1="00"+"00"+"11"+"0000"+"00"+"00"+"00";
        byte[] bytes = NumberUtil.hexStringToBytes(s1);
        NumberUtil.putShort(bytes,(short)35,3);
        long status= NumberUtil.byteToLong(bytes);
        //System.out.println(status);
        //parsWarn(status,"1");

    }

}

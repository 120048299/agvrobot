package com.wootion.protocols.robot.msg;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wootion.agvrobot.utils.NumberUtil;
import com.wootion.task.CONTROL_CMD;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * 机器人信息：心跳返回的消息（或者ros主动发上来的）
 */
@Data
public class RobotInfo implements RosMsg {
    protected Header  header;
    protected String  sender;
    protected String  receiver;
    protected Integer trans_id;
    protected String  robot_ip;

    protected short offline_warn;
    protected short mode;

    protected Float velocity_x;
    protected Float velocity_yaw;
    protected Float[] position;
    protected Float orientation;

    protected Byte  wheel_status;
    protected Byte light_status;
    protected Byte stop_status;

    protected Short battery_voltage;//uint16 最高位不会使用到
    protected Short battery_current;//uint16 最高位不会使用到
    protected Byte battery_quantity;
    protected Byte battery_status;

    protected Byte pump_status;
    protected Byte motor_status;
    protected Byte disable_status;
    protected Byte[] temperature;
    protected Byte nav_status;
    protected Integer sensor_status;//uint32


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



    public void setTemperature(JSONArray temperature) {
        if(null== temperature || temperature.size()==0){
            return ;
        }
        this.temperature=new Byte[temperature.size()];
        for(int i=0;i<temperature.size();i++){
            this.temperature[i]=temperature.getByte(i);
        }
    }

    public void setTemperatureArray(Byte[] temperature) {
        this.temperature=temperature;
    }




    public String toStringShortMsg() {
        return  "trans_id=" + trans_id +
                " mode=" + mode +" position=" + Arrays.toString(position) +
                " orientation=" + orientation
                ;
    }
}

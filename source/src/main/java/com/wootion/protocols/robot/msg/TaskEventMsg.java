package com.wootion.protocols.robot.msg;

import com.alibaba.fastjson.JSONObject;
import com.wootion.commons.Constans;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class TaskEventMsg extends BaseMsg implements RosMsg {
    protected String  sender ;
    protected String  receiver;
    protected String  robot_ip;
    protected String data;

}

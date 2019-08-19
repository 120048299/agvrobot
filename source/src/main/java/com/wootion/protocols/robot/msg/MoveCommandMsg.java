package com.wootion.protocols.robot.msg;

import com.wootion.commons.Constans;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class MoveCommandMsg extends BaseMsg implements RosMsg {
    protected String  sender = MsgNames.node_server;
    protected String  receiver = MsgNames.node_robot_control;

    protected Float velocity_x;
    protected Float velocity_yaw;
    protected Byte wheel_control;

}

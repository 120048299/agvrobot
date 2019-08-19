package com.wootion.protocols.robot.msg;

import com.wootion.commons.Constans;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)

public class GeneralServiceAckMsg extends BaseMsg implements RosMsg{
    protected String  sender ;
    protected String  receiver;
    protected String  robot_ip;

    protected String  ret_code;
    protected String  ret_msg;
    protected String  data;

}

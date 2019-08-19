package com.wootion.protocols.robot.msg;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class CommonMsg extends BaseMsg implements RosMsg {
    protected String  sender ;
    protected String  receiver;
    protected String data;
}

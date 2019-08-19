package com.wootion.protocols.robot.msg;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper=false)
public class GeneralAckMsg extends BaseMsg implements RosMsg {
    protected String  sender ;
    protected String  receiver ;
    protected String ack;
    protected String data;
}

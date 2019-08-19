package com.wootion.protocols.robot.msg;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper=false)
public class GeneralCommandMsg extends BaseMsg implements RosMsg {
    protected String  sender = MsgNames.node_server;
    protected String  receiver ;
    protected String cmd;
    protected String data;
}

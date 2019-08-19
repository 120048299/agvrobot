package com.wootion.protocols.robot.msg;

import com.wootion.commons.Constans;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class GeneralServiceReqMsg extends BaseMsg implements RosMsg{
    protected String  sender ;
    protected String  receiver ;
    protected String  type;
    protected String  data;
}

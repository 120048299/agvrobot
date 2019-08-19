package com.wootion.protocols.robot.msg;

import lombok.Data;

@Data
public class ActionPublish {
    private String op;
    private String topic;
    private String service;
    private String id;

    ActionGoalMsg msg;

}

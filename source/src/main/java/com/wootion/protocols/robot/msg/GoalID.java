package com.wootion.protocols.robot.msg;

import lombok.Data;

@Data
public class GoalID {
    TimePrimitive stamp;
    String id;

    public GoalID(String id) {
        this.id = id;
        stamp=new TimePrimitive(System.currentTimeMillis());
    }
}

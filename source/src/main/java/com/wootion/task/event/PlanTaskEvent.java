package com.wootion.task.event;

public class PlanTaskEvent {
    private String robotIp;

    public PlanTaskEvent(String robotIp) {
        this.robotIp = robotIp;
    }



    public String getRobotIp() {
        return robotIp;
    }

    public void setRobotIp(String robotIp) {
        this.robotIp = robotIp;
    }
}

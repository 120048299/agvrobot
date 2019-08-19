package com.wootion.task.event;

public class DisplayTaskEvent {
    String robotIp;

    public DisplayTaskEvent(String robotIp) {
        this.robotIp = robotIp;
    }

    public String getRobotIp() {
        return robotIp;
    }

    public void setRobotIp(String robotIp) {
        this.robotIp = robotIp;
    }
}

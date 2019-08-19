package com.wootion.task.event;

public class DeleteMemRobot {
    private String robotIp;


    public DeleteMemRobot(String robotIp) {
        this.robotIp = robotIp;
    }
    
    public String getRobotIp() {
        return robotIp;
    }

    public void setRobotIp(String robotIp) {
        this.robotIp = robotIp;
    }
}

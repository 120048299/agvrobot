package com.wootion.task.event;

import io.netty.channel.Channel;

public class AdjustTerraceEvent {
    private String robotIp;
    private int diffX;
    private int diffY;

    public AdjustTerraceEvent (String robotIp) {
            this.robotIp =robotIp;
    }

    public String getRobotIp() {
        return robotIp;
    }

    public void setRobotIp(String robotIp) {
        this.robotIp = robotIp;
    }

    public int getDiffX() {
        return diffX;
    }

    public void setDiffX(int diffX) {
        this.diffX = diffX;
    }

    public int getDiffY() {
        return diffY;
    }

    public void setDiffY(int diffY) {
        this.diffY = diffY;
    }

    @Override
    public String toString() {
        return "AdjustTerraceEvent{" +
                "robotIp='" + robotIp + '\'' +
                ", diffX=" + diffX +
                ", diffY=" + diffY +
                '}';
    }
}

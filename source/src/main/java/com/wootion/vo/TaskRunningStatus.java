package com.wootion.vo;

public class TaskRunningStatus {
    String planName;
    String planId;
    int total;   //整个plan
    int finished;  //整个plan完成的exec数
    int rateOfProgress;//整个plan的进度
    int estimatedTime;//minute  -1未知
    String currentPoint;
    int warn;
    String pause;

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    public int getRateOfProgress() {
        return rateOfProgress;
    }

    public void setRateOfProgress(int rateOfProgress) {
        this.rateOfProgress = rateOfProgress;
    }

    public int getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(int estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public String getCurrentPoint() {
        return currentPoint;
    }

    public void setCurrentPoint(String currentPoint) {
        this.currentPoint = currentPoint;
    }

    public int getWarn() {
        return warn;
    }

    public void setWarn(int warn) {
        this.warn = warn;
    }

    public String getPause() {
        return pause;
    }

    public void setPause(String pause) {
        this.pause = pause;
    }
}

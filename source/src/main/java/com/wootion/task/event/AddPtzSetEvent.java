package com.wootion.task.event;

import io.netty.channel.Channel;

public class AddPtzSetEvent {
    //巡检点名称
    private String runMarkName;
    //设备编号
    private String devId;
    //巡检点类型，路过点 or 一般设备
    private String ptzType;
    private String regzSpotId;
    private String ptzSetId;

    //机器人控制者的 webSocket Channel
    private Channel channel;

    public AddPtzSetEvent(String runMarkName, String devId, String ptzType,String ptzSetId,String regzSpotId, Channel channel) {
        this.runMarkName = runMarkName;
        this.devId = devId;
        this.ptzType = ptzType;
        this.ptzSetId = ptzSetId;
        this.regzSpotId = regzSpotId;
        this.channel = channel;
    }

    public AddPtzSetEvent() {
    }

    public String getRunMarkName() {
        return runMarkName;
    }

    public void setRunMarkName(String runMarkName) {
        this.runMarkName = runMarkName;
    }


    public String getPtzType() {
        return ptzType;
    }

    public void setPtzType(String ptzType) {
        this.ptzType = ptzType;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public String getRegzSpotId() {
        return regzSpotId;
    }

    public void setRegzSpotId(String regzSpotId) {
        this.regzSpotId = regzSpotId;
    }

    public String getPtzSetId() {
        return ptzSetId;
    }

    public void setPtzSetId(String ptzSetId) {
        this.ptzSetId = ptzSetId;
    }
}

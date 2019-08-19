package com.wootion.agvrobot.dto;

import com.wootion.task.event.AddPtzSetEvent;

public class AddPtzSetInfo {
    private String ptzSetId;
    private AddPtzSetEvent event;

    public String getPtzSetId() {
        return ptzSetId;
    }

    public void setPtzSetId(String ptzSetId) {
        this.ptzSetId = ptzSetId;
    }

    public AddPtzSetEvent getEvent() {
        return event;
    }

    public void setEvent(AddPtzSetEvent event) {
        this.event = event;
    }
}

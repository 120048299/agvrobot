package com.wootion.task.event;

import lombok.Data;

@Data
public class SentDataSyncEvent {
    private String robotId;
    public SentDataSyncEvent(String robotId) {
        this.robotId = robotId;
    }
}

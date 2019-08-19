package com.wootion.model;

import lombok.Data;

@Data
public class TaskResult {
    private String uid;

    private String taskLogId;

    private String fieldId;

    private String fieldValue;

    private String fieldPosibility;

    private String auditValue;

}
package com.wootion.model;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;
@Data
public class TaskLog {
    private String uid;
    private String taskId;
    private String jobId;
    private String ptzSetId;
    private String robotId;
    private Date beginTime;
    private Date finishTime;
    private String siteId;
    private String areaId;
    private Integer status;
    private Integer result;
    private String memo;
}
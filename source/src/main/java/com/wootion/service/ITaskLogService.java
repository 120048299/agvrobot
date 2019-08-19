package com.wootion.service;

import com.github.pagehelper.PageInfo;
import com.wootion.model.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ITaskLogService {

    TaskLog getTaskLog(String id);
    TaskLog getTaskLogByPtzSetAndPlan(String ptzSetId,String taskPlanId);

    TaskLog getLastTaskLog(String ptzSetId, Date beginTime);


    PageInfo<Map> selectTaskLogList(Map params, int pageNum, int pageSize);

    List<Map> selectTaskLogByPtzId(Map<String,String> params);

    int updateTaskLog(TaskLog taskLog);

    List<Map> selectTaskLogOfPlan(String taskPlanId);

}

package com.wootion.dao;

import com.wootion.model.*;

import java.util.List;
import java.util.Map;


public interface ITaskDao extends  IDao{
    TaskLog getFinishedTaskLog(String jobId, String ptzSetId);
    Task getTask(String id);
    List<Job> getTaskPlanByTaskId(String taskId);
    Job getTaskPlan(String id);
    String queryUserSiteId(String userId);


    RunMark getMarkByUid(String uid);
    PtzSet queryPtzById(String ptzId);

	RegzSpot queryRegzSpotById(String regzSpotID);

    List<AlarmCode> findAlarmCodes(String taskId);
    List<AlarmCode>  getSystemAlarmCode();


    RegzSpot findRegzSpotByDev(String devId);
    //Dev findDevByName(String devName);
    Dev findDevById(String devId);


}

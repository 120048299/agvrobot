package com.wootion.service;


import com.github.pagehelper.PageInfo;
import com.wootion.commons.Result;
import com.wootion.model.*;
import com.wootion.robot.MemRobot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface ITaskService {

    int addTask(Task task,ArrayList<String> ptzIds);
    int modifyTask(Task task,ArrayList<String> ptzIds);
    Result addMapTask(MemRobot memRobot,Task task, ArrayList<String> ptzIds );
    Task getTask(String taskId);
    PageInfo<Map> getTaskList(Map<String,String> params, int pageNum, int pageSize);
    Result startTaskImmediately(String taskId, String robotIp);

    int setTaskPeriod(TaskPeriod taskPeriod);
    int deleteTaskPeriod(String taskId);

    Result activateTask(String taskId,String robotIp);
    Result stopTask(String taskId, String robotIp);

    Result deleteTask(String  taskId,String robotIp);

    Result cancelJob(String jobIds,String robotIp);

    int startCharge(String robotIp);
    Result stopCharge(String robotIp);
    List<?> getTaskPlanStatisticsByMonth(String siteId,String robotId,String date);

    List<Job> getTaskPlanWaitAudit(String siteId);

    List<Job> getAllTaskPlan(String siteId);

    PageInfo<Map> getTaskListData(Map<String,Object > params, int pageNum, int pageSize);

    boolean checkRepeatTaskName(String name,String siteId);
    String getMapTaskName(Site site,String robotId);

    boolean syncFullTask(MemRobot memRobot);
    boolean sentTasksDataSynAck(MemRobot memRobot,String taskId);
    boolean addTasksDataSynAck(MemRobot memRobot,String taskId);
    boolean syncMapData(MemRobot memRobot);
    boolean syncParamsData(MemRobot memRobot);
}

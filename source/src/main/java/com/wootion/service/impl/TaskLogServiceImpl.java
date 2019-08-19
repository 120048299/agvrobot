package com.wootion.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wootion.commons.*;
import com.wootion.dao.IDao;
import com.wootion.mapper.JobMapper;
import com.wootion.mapper.PtzSetMapper;
import com.wootion.mapper.TaskLogMapper;
import com.wootion.model.*;
import com.wootion.service.ITaskLogService;
import com.wootion.utiles.DataCache;
import com.wootion.utiles.ValueUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TaskLogServiceImpl implements ITaskLogService {
    public static final Logger logger = LoggerFactory.getLogger(TaskLogServiceImpl.class);
    @Autowired
    IDao iDao;
    @Autowired
    JobMapper jobMapper;
    @Autowired
    TaskLogMapper taskLogMapper;
    @Autowired
    PtzSetMapper ptzSetMapper;

    @Override
    public TaskLog getTaskLog(String id){
        return  taskLogMapper.select(id);
    }

    @Override
    public TaskLog getTaskLogByPtzSetAndPlan(String ptzSetId,String taskPlanId) {
//        Map params =new HashMap();
//        params.put("ptzSetId",ptzSetId);
//        params.put("taskPlanId",taskPlanId);
//        return (TaskLog) iDao.selectOne("TaskLog.selectTaskLogByptsSetAndPlan",params);
        return taskLogMapper.selectTaskLogByptsSetAndPlan(ptzSetId,taskPlanId);
    }

    @Override
    public TaskLog getLastTaskLog(String ptzSetId, Date beginTime) {
//        Map params =new HashMap();
//        params.put("ptzSetId",ptzSetId);
//        params.put("beginTime",beginTime);
//        return (TaskLog) iDao.selectOne("TaskLog.selectLastTaskLogByPtzSetId",params);
        TaskLog taskLog = taskLogMapper.selectLastTaskLogByPtzSetId(ptzSetId,beginTime);
        return taskLog;
    }

    @Override
    public PageInfo<Map> selectTaskLogList(Map params, int pageNum, int pageSize){
//        PageInfo<Map>  pageInfo= (PageInfo<Map>)iDao.selectPageInfo("TaskLog.selectTaskLogList",params,pageNum, pageSize);
        PageHelper.startPage(pageNum, pageSize);
        String taskLogId = (String)params.get("taskLogId");
        String ptzSetId = (String)params.get("ptzSetId");
        String fromDate = (String)params.get("fromDate");
        String toDate = (String)params.get("toDate");
        String execFailReason = (String)params.get("execFailReason");
        String auditStatus = (String)params.get("auditStatus");
        String selectedJobId = (String)params.get("selectedJobId");
        String alarmLevel = (String)params.get("alarmLevel");
        Integer hasAlarm = (Integer)params.get("hasAlarm");
        Integer forAudit = (Integer)params.get("forAudit");
        String robotId = (String)params.get("robotId");

        List<Map> tempList = taskLogMapper.selectTaskLogList(taskLogId,ptzSetId,fromDate,toDate,execFailReason,auditStatus,selectedJobId,alarmLevel,hasAlarm,forAudit,robotId);
        PageInfo pageInfo = new PageInfo(tempList);
        List<Map> list = pageInfo.getList();
        fillTaskLogAlarm(list,iDao);
        return pageInfo;
    }

    /**
     * 用于导出
     * @param params
     * @return
     */
    @Override
    public List<Map> selectTaskLogByPtzId(Map params){
//        List<Map> list = (List<Map>) iDao.selectList("TaskLog.selectTaskLogByPtzId",params);
        String ptzSetId = (String)params.get("ptzSetIds");
        String fromDate = (String)params.get("fromDate");
        String toDate = (String)params.get("toDate");
        List<Map> list = taskLogMapper.selectTaskLogByPtzId(ptzSetId,fromDate,toDate);
        fillTaskLogAlarm(list,iDao);
        return list;
    }


    @Override
    public int updateTaskLog(TaskLog taskLog){
        int ret= taskLogMapper.update(taskLog);
        if(ret<=0){
            return ret;
        }
        return 1;
    }

    @Override
    public List<Map> selectTaskLogOfPlan(String taskId){
//        Map params =new HashMap();
//        params.put("taskPlanId",taskPlanId);
//        List<Map> list=(List<Map>) iDao.selectList("TaskLog.selectTaskExecLogForPlanReport",params);
        List<Map> list = taskLogMapper.selectTaskExecLogForPlanReport(taskId);
        fillTaskLogAlarm(list,iDao);
        return list;
    }



    public void fillTaskLogAlarm(List<Map>  list,IDao iDao){
        if(list==null ){
            return ;
        }
        for (Map map:list){
            fillTaskLogAlarm(map,iDao);
        }
    }
    public void fillTaskLogAlarm(Map map,IDao iDao)
    {
        Integer alarmLevel = (Integer) map.get("alarm_level");
        if (alarmLevel!=null) {
            map.put("alarmLevelName", ALARM_LEVEL.fromInt(alarmLevel).toStrValue());
        } else {
            map.put("alarmLevelName", "无告警");
        }
        PtzSet ptzSet =  DataCache.findPtzSetByUid((String)map.get("site_id"),(String)map.get("ptz_set_id"));
        map.put("ptzSet",ptzSet);
        Job job =DataCache.findJob((String)map.get("site_id"),(String)map.get("job_id"));
        if(job==null){
            return ;
        }
        map.put("taskPlan", job);

        String resultStr="";
        map.put("resultStr",resultStr);
        map.put("taskName", job.getName());
        map.put("description", ptzSet.getDescription());
        map.put("planName", job.getName());
    }

    // TaskResult list转换成map
    public static Map<String,TaskResult> transTaskResultListToMap(List<TaskResult> list) {
        Map<String,TaskResult> map = new HashMap<>();
        if (list==null) {
            return map;
        }
        for (TaskResult item:list) {
            map.put(item.getFieldId(), item);
        }
        return map;
    }

}

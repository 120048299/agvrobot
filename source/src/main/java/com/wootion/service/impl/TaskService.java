package com.wootion.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wootion.agvrobot.utils.DateUtil;
import com.wootion.agvrobot.utils.StringUtil;
import com.wootion.agvrobot.utils.UUIDUtil;
import com.wootion.commons.Result;
import com.wootion.dao.IDao;
import com.wootion.dao.ITaskDao;
import com.wootion.mapper.*;
import com.wootion.model.*;
import com.wootion.protocols.robot.GeneralService;
import com.wootion.protocols.robot.msg.GeneralServiceAckMsg;
import com.wootion.protocols.robot.msg.MsgNames;
import com.wootion.protocols.robot.msg.SentDataSynAckMsg;
import com.wootion.robot.MemRobot;
import com.wootion.robot.MemUtil;
import com.wootion.service.ITaskService;
import com.wootion.task.EventQueue;
import com.wootion.taskmanager.TaskControl;
import com.wootion.utiles.DataCache;
import com.wootion.utiles.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service("taskService ")
public class TaskService implements ITaskService {
    public static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    @Autowired
    private IDao dao;
    @Autowired
    private JobMapper jobMapper;
    @Autowired
    TaskMapper taskMapper;

    @Autowired
    TaskPtzMapper taskPtzMapper;

    @Autowired
    TaskPeriodMapper taskPeriodMapper;
    @Autowired
    private RunMarkMapper runMarkMapper;
    @Autowired
    RunLineMapper runLineMapper;

    @Autowired
    MaintainAreaMapper maintainAreaMapper;

    @Autowired
    ChargeRoomMapper chargeRoomMapper;
    @Autowired
    RobotParamMapper robotParamMapper;
    @Autowired
    SysParamMapper sysParamMapper;
    @Autowired
    private PtzSetMapper ptzSetMapper;

    @Override
    public int addTask(Task task,ArrayList<String> ptzIds){
        int ret=taskMapper.insert(task);
        addTaskPtz(task.getSiteId(),task.getUid(),ptzIds);
        DataCache.reload();
        return ret;
    }

    @Override
    public int modifyTask(Task task,ArrayList<String> ptzIds){
        int ret=taskMapper.update(task);
        taskPtzMapper.deleteByTaskId(task.getUid());
        addTaskPtz(task.getSiteId(),task.getUid(),ptzIds);
        return ret;
    }

    private int addTaskPtz(String siteId,String taskId,ArrayList<String> ptzIds) {
        //允许没有设置ptzset
        if(ptzIds==null){
            return 0;
        }
        List<TaskPtz> list=new ArrayList<>();
        for (String ptzId:ptzIds) {
            TaskPtz taskPtz = new TaskPtz();
            taskPtz.setUid(UUIDUtil.getUUID());
            taskPtz.setTaskId(taskId);
            taskPtz.setPtzSetId(ptzId);
            taskPtz.setSiteId(siteId);
            list.add(taskPtz);
        }
        int ret=taskPtzMapper.insertBatch(list);
        return ret;
    }
    private RunMark findNearRunMark(MemRobot memRobot) {
        List<RunMark> exitRunMarkList = runMarkMapper.getRunMarkBySiteId(memRobot.getSiteId());
        for (RunMark exitRunMark : exitRunMarkList) {
            if (Math.abs(exitRunMark.getLat() - memRobot.getRobotInfo().getPosition()[1]) <= 0.05 && Math.abs(exitRunMark.getLon() - memRobot.getRobotInfo().getPosition()[0]) <= 0.05) {
                return exitRunMark;
            }
        }
        return null;
    }

    public Task getTask(String taskId){
        return  taskMapper.select(taskId);
    }

    @Override
    public PageInfo getTaskList(Map<String,String > params, int pageNum, int pageSize) {
//        return dao.selectPageInfo("Task.selectTaskList",params,pageNum, pageSize);
        String taskTypeId = (String) params.get("taskTypeId");
        String siteId = (String) params.get("siteId");
        String robotId = (String) params.get("robotId");
        String taskName = (String) params.get("taskName");
//                (@Param("taskTypeId") String taskTypeId,@Param("siteId") String siteId,@Param("robotId") String robotId,@Param("taskName") String taskName
        PageHelper.startPage(pageNum, pageSize);
        List<Task> taskList = taskMapper.selectTaskList(taskTypeId,siteId,robotId,taskName);
        PageInfo pageInfo = new PageInfo(taskList);
        return pageInfo;
    }



    private  Map findRealJobInList(List<Map > jobsTempList,String filterTaskId,Date filterPST,Date filterPET ){

        for(Map item2:jobsTempList){
            String jobsTaskId = (String)item2.get("task_id");
            Date jobsPST = (Date) item2.get("plan_start_time");
            if(filterPET!=null){
                if(filterTaskId.equals(jobsTaskId) && jobsPST.getTime() > filterPST.getTime() && jobsPST.getTime() < filterPET.getTime()){
                    return item2;
                }
            }else {
                if(filterTaskId.equals(jobsTaskId) && jobsPST.getTime() >= filterPST.getTime()){
                    return item2;
                }
            }

        }
        return null;
    }

    /**
     * 添加地图任务
     * @param memRobot
     * @param task
     * @param ptzIds
     * @return
     */
    @Override
    public  Result addMapTask(MemRobot memRobot,Task task, ArrayList<String> ptzIds ){
        int ret=addTask(task,ptzIds);
        logger.info("addRobotMapTask save task : ret= "+ret);
        if(ret<0){
            return ResultUtil.failed("地图任务：保存任务失败");
        }
        boolean sendResult = addTasksDataSynAck(memRobot, task.getUid());
        if(!sendResult){
            JSONObject data = new JSONObject();
            data.put("id",task.getUid());
            logger.info("地图任务：同步任务成功失败,taskId="+task.getUid());
            return ResultUtil.build(0,  "地图任务：同步任务成功失败", "");
        }
        try{
            Thread.sleep(1000);
        }catch (Exception e){
            e.printStackTrace();
        }
        //立即启动
        Result result=startTaskImmediately(task.getUid(),memRobot.getRobotIp());
        return result;
    }

    @Override
    public Result startTaskImmediately(String taskId, String robotIp){
        Task task=taskMapper.select(taskId);
        if(task==null || task.getStatus()==4){
            return ResultUtil.build(-1,"任务不存在或者已删除.",null);
        }
        MemRobot memRobot=MemUtil.queryRobot(robotIp);
        if(!memRobot.isOnline()){
            return ResultUtil.build(-2,"机器人不在线.",null);
        }
        List<Job> jobList= jobMapper.selectByTaskUid(task.getUid());
        if(jobList!=null||jobList.size()==0){
            for(Job job:jobList){
                if(job.getStatus()==0 || job.getStatus()==1){
                    return ResultUtil.build(-3,"当前正有任务实例在运行.",null);
                }
            }
        }
        Result result=TaskControl.activateTask(memRobot,taskId);
        logger.info("startTaskImmediately activateTask"+result);
        if(result.getCode()!=0){
            return ResultUtil.build(-4,"激活任务失败."+result.getMsg(),null);
        }
        result=TaskControl.startTask(memRobot,taskId);
        logger.info("startTaskImmediately "+result);
        if(result.getCode()!=0){
            return ResultUtil.build(-5,"立即执行失败."+result.getMsg(),null);
        }
        task.setStatus(1);
        int ret=taskMapper.update(task);
        logger.debug("update task ret="+ret,"task"+task);
        return ResultUtil.build(0,"立即执行成功.",null);
    }


    /**
     * 设置周期时，任务当前不能在运行。
        task_exec在task_plan 计划执行的时候生成
        -1 未成功。可能任务在运行。
     */
    @Transactional
    public int setTaskPeriod(TaskPeriod taskPeriod){
        Task task=taskMapper.select(taskPeriod.getTaskId());

        if (task==null || task.getStatus()==2) { // 任务不存在或已删除
            return -1;
        }

        if (task.getStatus()==1) { // 任务正在执行
            return -2;
        }

        // 删除调度计划
        int ret=deleteTaskPeriod(taskPeriod.getTaskId());
        if(ret<0){
            return -1;
        }
        ret=taskPeriodMapper.insert(taskPeriod);
        task.setStatus(1);
        taskMapper.update(task);
        return ret;
    }

    /**
     * 删除调度计划，未启动的taskplan直接删掉
     * 调这个方法的前提条件需要在外部保证
     * @param taskId
     * @return
     */
    public int deleteTaskPeriod(String taskId){
        TaskPeriod taskPeriod=taskPeriodMapper.selectByTaskId(taskId);
        if(taskPeriod==null){
            return 0;
        }
        taskPeriodMapper.delete(taskPeriod.getUid());
        return 0;
    }


    /**
     * 激活任务
     * @param taskId
     * @return
     */
    @Override
    public Result activateTask(String taskId,String robotIp){
        Task task=taskMapper.select(taskId);
        if(task==null || task.getStatus()==4){
            return ResultUtil.build(-1,"任务不存在或者已删除",null);
        }
        if(task.getStatus()==1){
            return ResultUtil.build(-1,"任务已经是去激活状态",null);
        }
        MemRobot memRobot=MemUtil.queryRobot(robotIp);
        Result result= TaskControl.activateTask(memRobot,taskId);
        if(result.getCode()==0){
            task.setStatus(0);
            taskMapper.update(task);
            logger.info("激活任务成功");
            return ResultUtil.build(0,"激活任务成功",null);
        }else{
            logger.error("激活任务失败:"+result.getMsg());
            return ResultUtil.build(-1,"激活任务失败",null);
        }
    }

    /**
     * 停止任务 ：对任务下的task_plan如果有运行的，暂停
     * @param taskId
     * @return
     */
    @Override
    public Result stopTask(String taskId,String robotIp){
        Task task=taskMapper.select(taskId);
        if(task==null || task.getStatus()==2){ //
            return ResultUtil.build(-1,"任务不存在或者已删除",null);
        }
        if(task.getStatus()==0){ //
            return ResultUtil.build(-1,"任务未执行",null);
        }
        MemRobot memRobot=MemUtil.queryRobot(robotIp);
        Result result= TaskControl.deactivateTask(memRobot,taskId);
        if(result.getCode()==0){
            task.setStatus(0);
            taskMapper.update(task);
            logger.info("去激活任务成功");
            return ResultUtil.build(0,"停止任务成功",null);
        }else{
            logger.error("去激活任务失败:"+result.getMsg());
            return ResultUtil.build(-1,"停止任务失败",null);
        }
    }


    /**
     * 删除任务
     * @param taskId
     * @param robotIp
     * @return
     */
    @Override
    public Result deleteTask(String  taskId,String robotIp){
        Task task=taskMapper.select(taskId);
        if(task==null || task.getStatus()==2){
            return ResultUtil.build(-1,"任务不存在或者已删除",null);
        }
        MemRobot memRobot=MemUtil.queryRobot(robotIp);
        if(memRobot.isOnline() ) {
            Result result=TaskControl.deleteTask(memRobot,taskId);
            if(result.getCode()==0){
                task.setStatus(3);
                taskMapper.update(task);
                deleteTaskPeriod(taskId);
                logger.info("删除任务成功");
                return ResultUtil.build(0,"删除任务成功，已同步。",null);
            }else{
                logger.error("删除任务失败:"+result.getMsg());
                return ResultUtil.build(-1,"删除任务失败。",null);
            }
        }else{
            task.setStatus(3);
            taskMapper.update(task);
            deleteTaskPeriod(taskId);
            return ResultUtil.build(0,"删除任务成功,未同步。",null);
        }
    }


    /**
     * 任务列表删除任务实例，即取消
     * @param jobId
     * @return
     */
    public Result cancelJob(String jobId,String robotIp){
        MemRobot memRobot=MemUtil.queryRobot(robotIp);
        if(memRobot.isOnline() ) {
            return ResultUtil.build(-1, "机器人不在线。",null);
        }

        StringBuffer resutlBuffer = new StringBuffer();
        Job job =jobMapper.select(jobId);
        if(!( job.getStatus()==-1 || job.getStatus()==5) ){
            return ResultUtil.build(-2, job.getName()+"任务实例已经删除或者结束。",null);
        }
        Result result=TaskControl.cancelJob(memRobot,jobId,1);
        if(result.getCode()==0){
            return ResultUtil.build(0,"删除任务实例成功",null);
        }else{
            return ResultUtil.build(-3,"删除任务实例失败",null);
        }
    }

    /**
     *
     * @param robotIp
     * @return 0 成功 -1 失败，正在处理充电或者停止充电流程。-2 手柄遥控模式
     */
    @Override
    public int startCharge(String robotIp) {
        MemRobot memRobot=MemUtil.queryRobot(robotIp);
        if(memRobot.getCharging()==1 || memRobot.getChargeFlowStatus()==1){
            return -2;
        }
        if(memRobot.getRobotMode()==2){
            return -3;
        }
        if(memRobot.getRobotMode()==1){
            return -4;
        }
        Result result=TaskControl.goBack(memRobot);
        logger.debug("startCharge result"+result);
        return result.getCode();
    }

    /**
     * 结束充电
     * @param robotIp
     * @return
     */
    @Override
    public Result stopCharge(String robotIp) {
        MemRobot memRobot = MemUtil.queryRobot(robotIp);
        Result result=TaskControl.stopCharge(memRobot);
        return result;
    }



    @Override
    public List<?> getTaskPlanStatisticsByMonth(String siteId,String robotId,String str) {
        List<java.util.Date> list = EventQueue.getFirstAndLastDdate(str, "yyyy-MM-dd");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fromDate =sdf.format(list.get(0));
        String toDate =sdf.format(list.get(1));

/**
　　* @description: TODO
　　* @author Marico
        构造任务展示：
 1，通过根据task表任务配合task_period,构造出所有将要执行的任务
 2，通过查询Job表，查询机器人上报的数据
 3，通过聚合以上两者数据，构造最终forQueryList 和resultList ;
    resultList用于生成所在月份的任务
    forQueryList用于过滤所在月份任务
　　*/
        List<Task> taskList = taskMapper.queryBySiteWithStatus(siteId);
        List<Map> jobsTempList = new ArrayList<>();
        List<Map> generateJobsList= new ArrayList<>();
        for(Task task:taskList){
            String taskId = task.getUid();
            List<Map> jobList = jobMapper.selectJobsList(taskId,siteId,fromDate,toDate,null,null,null);
            if(jobList!=null && jobList.size()>0){
                for(Map map:jobList){
                    jobsTempList.add(map);
                }
            }
            TaskPeriod taskPeriod = taskPeriodMapper.selectByTaskId(taskId);
            if(taskPeriod==null){
                Map map = new HashMap();
                int status = -1;
                Date nowTime = new Date(System.currentTimeMillis());
                Date dateEndtTime = DateUtil.stringToDate(
                        DateUtil.dateToString(nowTime, "yyyy.MM.dd") + " 23:59:59",
                        "yyyy.MM.dd HH:mm:ss");
                Date tempdate= task.getCreateTime();

                java.util.Date startDate = DateUtil.stringToDate(
                        DateUtil.dateToString(tempdate, "yyyy.MM.dd") + " 00:00:00",
                        "yyyy.MM.dd HH:mm");

                if(startDate.getTime() <= dateEndtTime.getTime()){
                    status = 7;
                }
                map.put("uid", task.getUid());
                map.put("task_id", task.getUid());
                map.put("create_time", "");
                map.put("name", task.getName());
                map.put("site_id", task.getSiteId());
                map.put("robot_id", task.getRobotId());
                map.put("plan_start_time", startDate);
                map.put("plan_end_time", null);
                map.put("user_id", task.getUserId());
                map.put("status", status);
                map.put("real_start_time", "");
                map.put("real_end_time", "");
                map.put("priority", "");
                map.put("audit_status", 0);
                map.put("audit_user_id", "");
                map.put("audit_time", "");
                generateJobsList.add(map);
                continue;
            }

            int style=taskPeriod.getStyle();
            if(style==3){//定期
                java.util.Date startDate = null;
                java.util.Date endDate = null;
                StringBuffer bufferDate = new StringBuffer();
                String[] dateList=taskPeriod.getStyleParam().split(";");
                for(String strDay:dateList){
                    java.util.Date date = DateUtil.stringToDate(strDay,"yyyy-MM-dd");
                    Map map= generateMapTaskPlan(task, date, taskPeriod.getWithinAdayTime());
                    generateJobsList.add(map);
                }

            }

            java.util.Date date = taskPeriod.getStartDate();
            if(date!=null) {
                while (date.compareTo(taskPeriod.getEndDate()) <= 0) {
                    if (taskPeriod.getStyle() == 0) {//间隔daySpan天执行
                        int daySpan = Integer.parseInt(taskPeriod.getStyleParam());
                        Map map = generateMapTaskPlan(task, date, taskPeriod.getWithinAdayTime());
                        generateJobsList.add(map);
                        date = DateUtil.add(date, 1, daySpan + 1);
                    } else if (taskPeriod.getStyle() == 1) {//按周
                        int weekDay = DateUtil.getWeekDay(date);
                        if (taskPeriod.getStyleParam().contains(String.valueOf(weekDay))) {
                            Map map = generateMapTaskPlan(task, date, taskPeriod.getWithinAdayTime());
                            generateJobsList.add(map);
                        }
                        date = DateUtil.add(date, 1, 1);
                    } else if (taskPeriod.getStyle() == 2) {//按月
                        int monthDay = DateUtil.getMonthDay(date);
                        String temp = String.format("%02d", monthDay);
                        if (taskPeriod.getStyleParam().contains(temp)) {
                            Map map = generateMapTaskPlan(task, date, taskPeriod.getWithinAdayTime());
                            generateJobsList.add(map);
                        }
                        date = DateUtil.add(date, 1, 1);
                    }
                }
            }
        }


        List<Map> filtergenerateJobsList = new ArrayList<>();
        java.util.Date tempBeginDate = null;
        java.util.Date tempEndDate =null;

        tempBeginDate = DateUtil.stringToDate((fromDate+" 00:00:00"),"yyyy-MM-dd hh:mm:ss");
        tempEndDate = DateUtil.stringToDate((toDate +" 23:59:59"),"yyyy-MM-dd hh:mm:ss");

        if(generateJobsList!=null&&generateJobsList.size()>0) {
            for (Map map : generateJobsList) {
                Date date = (Date) map.get("plan_start_time");
                    if(date.getTime() >= tempBeginDate.getTime() && date.getTime() < tempEndDate.getTime()) {
                        filtergenerateJobsList.add(map);
                    }

            }
        }
        List<Map> resultList = new ArrayList<>();
        if(jobsTempList==null||jobsTempList.size()==0){
            resultList=filtergenerateJobsList;
        }else if(filtergenerateJobsList==null||filtergenerateJobsList.size()==0){
            resultList=null;
        }else {
            for(Map item1:filtergenerateJobsList) {
                String filterTaskId = (String) item1.get("task_id");
                Date filterPST = (Date) item1.get("plan_start_time");
                Date filterPET = (Date) item1.get("plan_end_time");
                    Map realJob = findRealJobInList(jobsTempList, filterTaskId, filterPST, filterPET);
                    if (realJob != null) {
                        resultList.add(realJob);
                    } else {
                        resultList.add(item1);
                    }
                }
        }

        List<Map> data = getJobsByMonth(resultList);


        Map map = new HashMap<>();
        for (Map taskCount : data) {
            map.put(taskCount.get("taskDate"), taskCount.get("num"));
        }
        String yearMonth = sdf.format(list.get(0)).substring(0, 7);
        int maxDay = Integer.parseInt(sdf.format(list.get(1)).substring(8)); //当前日期中当前月对应的最大天数
        List<Object> result = new ArrayList<>();//存储需要返回的月历数据
        for (int i = 1; i <= maxDay; i++) {
            //根据日期取map中数据
            String day = "" + i;
            if (day.length() == 1) {
                day = "0" + i;
            }
            day = yearMonth +"-"+ day;
            tempBeginDate = DateUtil.stringToDate((day+" 00:00:00"),"yyyy-MM-dd hh:mm:ss");
            tempEndDate = DateUtil.stringToDate((day +" 23:59:59"),"yyyy-MM-dd hh:mm:ss");
            List<Map> listData = new ArrayList<>();
            for(Map item:resultList){
                String site_Id = (String) item.get("site_id");
                String robot_Id = (String) item.get("robot_id");
                Date date = (Date) item.get("plan_start_time");
                if (date.getTime()>=tempBeginDate.getTime()  && date.getTime() <= tempEndDate.getTime() && site_Id.contains(siteId)&& robot_Id.contains(robotId)) {
                    listData.add(item);
                }

            }
            Map map2 = new HashMap();
            map2.put("date", i);
            map2.put("taskCount", map.get(tempBeginDate) == null ? "" : map.get(tempBeginDate));//任务数量对象taskCount
            map2.put("taskList", listData);//具体任务执行结果记录
            result.add(map2);
        }
        return result;
    }


    public Job getTaskPlan(String id){
        return (Job)dao.selectOne(Job.class,id);
    }

    public List<Job> getTaskPlanWaitAudit(String siteId){
//        return (List<Job>) dao.selectList("Job.selectWaitAudit",siteId);
        return jobMapper.selectWaitAudit(siteId);

    }

    public List<Job> getAllTaskPlan(String siteId){
//        return (List<Job>) dao.selectList("Job.selectAllTaskPlan",siteId);
        return  jobMapper.selectAllTaskPlan(siteId);
    }


    @Override
    public PageInfo<Map> getTaskListData(Map<String,Object> params,int pageNum,int pageSize) {
//        return (PageInfo<Map>)dao.selectPageInfo("Task.getTaskListData",params,pageNum, pageSize);
        PageHelper.startPage(pageNum, pageSize);
        String siteId = (String)params.get("siteId");
        String fromDate = (String)params.get("fromDate");
        String toDate = (String)params.get("toDate");
        String taskName = (String)params.get("taskName");
        String robotId = (String)params.get("robotId");
        List<Map> list=taskMapper.getTaskListData(siteId,fromDate,toDate,taskName,robotId);

        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    /**
     *
     * @param name
     * @param siteId
     * @return
     */
    @Override
    public boolean checkRepeatTaskName(String name,String siteId){
        // 检查重名 站点+自定义名称+年月日
//        List<Task> list=(List<Task>)dao.selectList("Task.selectAll",siteId);
        List<Task> list = taskMapper.selectAll(siteId);
        if(list!=null && list.size()>0){
            for(Task task:list){
                if(task.getName().equals(name)){
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 地图任务 和紧急任务 的名字
     * @return
     */
    @Override
    public String getMapTaskName(Site site,String robotId){
        if(site==null || robotId==null ){
            return null;
        }
        java.util.Date dateNow=new java.util.Date();
        String newName ="地图任务";
        MemRobot memRobot=MemUtil.queryRobotById(robotId);
        if(memRobot.getEmergency()==1){
            newName="紧急定位";
        }
        newName = site.getName()+newName + DateUtil.dateToString(dateNow, "yyyyMMdd");
        // 检查重名 站点+年月日
//        List<Task> list=(List<Task>)dao.selectList("Task.selectAll",site.getUid());
        List<Task> list = taskMapper.selectAll(site.getUid());
        int sameNameCount=0;
        if(list!=null && list.size()>0){
            for(Task task:list){
                if(task.getName().indexOf(newName)>=0){
                    sameNameCount++;
                }
            }
        }
        //重名后追加 -计数
        if(sameNameCount>0){
            newName=newName+"-"+sameNameCount;
        }
        return newName;
}

private Map buildTaskSyncData(Task task){
    String taskUid = task.getUid();
    List<String> ptzSetList = taskPtzMapper.selectListStringByTaskUid(taskUid);
    TaskPeriod taskPeriod = taskPeriodMapper.selectByTaskId(taskUid);
    Map plan=new HashMap();
    if(taskPeriod!=null){
        plan.put("style",taskPeriod.getStyle());
        plan.put("param",taskPeriod.getStyleParam());
        plan.put("start_date",DateUtil.dateToString(taskPeriod.getStartDate(),"yyyy-MM-dd"));
        plan.put("end_date",DateUtil.dateToString(taskPeriod.getEndDate(),"yyyy-MM-dd"));
        plan.put("run_times",taskPeriod.getWithinAdayTime());
    }
    Map map = new HashMap();
    map.put("id",task.getUid());
    map.put("name",task.getName());
    map.put("type",task.getEmergency());
    map.put("status",task.getStatus());
    map.put("ptz_sets",ptzSetList);
    map.put("plan",plan);
    return map;
}

public boolean syncFullTask(MemRobot memRobot){
        String siteId= memRobot.getSiteId();
        String robotId=memRobot.getRobotId();
        List<Task> tasksList = taskMapper.syncTasks(siteId,robotId) ;
        List<Map> tasksSynList = new ArrayList<>();
        for(Task task:tasksList){
            Map map=buildTaskSyncData(task);
            tasksSynList.add(map);
        }
        JSONArray  tasksJsonArray =JSONArray.parseArray(JSON.toJSONString(tasksSynList,SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.WriteMapNullValue));
        JSONObject tasksJson = new JSONObject();
        tasksJson.put("sync_style","full");
        tasksJson.put("full",tasksJsonArray);

        String tasksSynDataString =tasksJson.toJSONString();

        String type="tasks";
        Result  result= GeneralService.call(memRobot.getCh(),MsgNames.node_task_manage,MsgNames.service_data_syn,type,tasksSynDataString,5);
        if(result.getCode()==1){
            GeneralServiceAckMsg ackMsg=(GeneralServiceAckMsg)result.getData();
            if(ackMsg.getRet_code().equals("true")){
                List<String> taskUidList = new ArrayList<>();
                for(Task task:tasksList){
                    String  taskUid = task.getUid();
                    taskUidList.add(taskUid);
                }
                taskMapper.updateTaskDataSynSuccess(taskUidList);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean sentTasksDataSynAck(MemRobot memRobot,String taskId){

        List<Task> tasksList = taskMapper.queryByUid(taskId) ;
        List<Map> tasksSynList = new ArrayList<>();
        for(Task task:tasksList){
            Map map=buildTaskSyncData(task);
            tasksSynList.add(map);
        }

        JSONArray tasksJsonArray =JSONArray.parseArray(JSON.toJSONString(tasksSynList,SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.WriteMapNullValue));
        JSONObject tasksJson = new JSONObject();
        tasksJson.put("sync_style","alter");
        tasksJson.put("add_modify",tasksJsonArray);

        String tasksSynDataString =tasksJson.toJSONString();

        String type="tasks";
        Result  result= GeneralService.call(memRobot.getCh(),MsgNames.node_task_manage,MsgNames.service_data_syn,type,tasksSynDataString,5);
        if(result.getCode()==1){
            GeneralServiceAckMsg ackMsg=(GeneralServiceAckMsg)result.getData();
            if(ackMsg.getRet_code().equals("true")){
                List<String> taskUidlist = new ArrayList<>();
                for(Task task:tasksList){
                    String taskUid = task.getUid();
                    taskUidlist.add(taskUid);
                }
                taskMapper.updateTaskDataSynSuccess(taskUidlist);
                return true;
            }else{
                logger.debug("同步任务接口调用失败"+ackMsg.getRet_msg());
            }
        }else{
            logger.debug("同步任务接口调用失败"+result.getMsg());
        }
        return false;

    }

    @Override
    public boolean addTasksDataSynAck(MemRobot memRobot,String taskId){

        List<Task> tasksList = taskMapper.queryByUid(taskId) ;
        List<Map> tasksSynList = new ArrayList<>();
        for(Task task:tasksList){
            Map map=buildTaskSyncData(task);
            tasksSynList.add(map);
        }

        JSONArray tasksJsonArray =JSONArray.parseArray(JSON.toJSONString(tasksSynList,SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.WriteMapNullValue));
        JSONObject tasksJson = new JSONObject();
        tasksJson.put("sync_style","alter");
        tasksJson.put("add_modify",tasksJsonArray);

        String tasksSynDataString =tasksJson.toJSONString();

        String type="tasks";
        Result  result= GeneralService.call(memRobot.getCh(),MsgNames.node_task_manage,MsgNames.service_data_syn,type,tasksSynDataString,5);
        if(result.getCode()==1){
            GeneralServiceAckMsg ackMsg=(GeneralServiceAckMsg)result.getData();
            if(ackMsg.getRet_code().equals("true")){
                List<String> taskUidList = new ArrayList<>();
                for(Task task:tasksList){
                    String taskUid = task.getUid();
                    taskUidList.add(taskUid);
                }
                taskMapper.updateTaskDataSynSuccess(taskUidList);
                return true;

            }
        }
        List<String> taskUidList = new ArrayList<>();
        for(Task task:tasksList){
            String taskUid = task.getUid();
            taskUidList.add(taskUid);
        }
        taskMapper.updateTaskDataSynFail(taskUidList);
        return false;
    }

   public boolean syncMapData(MemRobot memRobot){
        JSONObject mapsSynJson = new JSONObject();
        String siteId=memRobot.getSiteId();
        List<RunMark> runMarkList = runMarkMapper.getRunMarkBySiteId(siteId);
        if(runMarkList!=null && runMarkList.size()>0){
            List<Map> runMarksTemplist = new ArrayList<>();
            for(RunMark item:runMarkList){
                Map runMarksTempMap = new HashMap();
                runMarksTempMap.put("name",item.getMarkName());
                runMarksTempMap.put("lon",item.getLon());
                runMarksTempMap.put("lat",item.getLat());
                runMarksTempMap.put("move_style",item.getMoveStyle());
                runMarksTempMap.put("id",item.getUid());
                runMarksTemplist.add(runMarksTempMap);
            }
            JSONArray  runMarksJsonArray =JSONArray.parseArray(JSON.toJSONString(runMarksTemplist,SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.WriteMapNullValue));
            JSONObject runMarksSynJson = new JSONObject();
            runMarksSynJson.put("sync_style","full");
            runMarksSynJson.put("full",runMarksJsonArray);
            mapsSynJson.put("run_marks",runMarksSynJson);

        }

        List<RunLine> runLineList = runLineMapper.getRunLineBySiteId(siteId);
        if(runLineList!=null && runLineList.size()>0){
            List<Map> runLineTemplist = new ArrayList<>();
            for(RunLine item:runLineList){
                Map runLineTempMap = new HashMap();
                runLineTempMap.put("mark_id1",item.getMarkId1());
                runLineTempMap.put("mark_id2",item.getMarkId2());
                runLineTempMap.put("id",item.getUid());
                runLineTemplist.add(runLineTempMap);
            }
            JSONArray  runLineJsonArray =JSONArray.parseArray(JSON.toJSONString(runLineTemplist,SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.WriteMapNullValue));
            JSONObject runLineSynJson = new JSONObject();
            runLineSynJson.put("sync_style","full");
            runLineSynJson.put("full",runLineJsonArray);

            mapsSynJson.put("run_lines",runLineSynJson);
        }

        List<PtzSet> ptzSetList = ptzSetMapper.selectListBySite(siteId);
        List<Map> ptzSetsTemplist = new ArrayList<>();
        for(PtzSet ptzSet:ptzSetList){
            Map ptzSetsTempMap = new HashMap();
            Map serviceParamMap = new HashMap();
            ptzSetsTempMap.put("id",ptzSet.getUid());
            ptzSetsTempMap.put("name",ptzSet.getDescription());
            ptzSetsTempMap.put("type",ptzSet.getPtzType());
            ptzSetsTempMap.put("mark_id",ptzSet.getMarkId());
            ptzSetsTempMap.put("robot_angle",ptzSet.getRobotAngle());
            ptzSetsTempMap.put("status",ptzSet.getStatus());
            ptzSetsTempMap.put("setted",ptzSet.getSetted());
            if(ptzSet.getPtzType()==4){
                ptzSetsTempMap.put("service_type",1);
            }else{
                ptzSetsTempMap.put("service_type",0);
            }
            serviceParamMap.put("preset_path","");
            ptzSetsTempMap.put("service_param",serviceParamMap);
            ptzSetsTemplist.add(ptzSetsTempMap);
        }
        JSONArray ptzSetsJsonArray =JSONArray.parseArray(JSON.toJSONString(ptzSetsTemplist, SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.WriteMapNullValue));
        JSONObject ptzJson = new JSONObject();
        ptzJson.put("sync_style","full");
        ptzJson.put("full",ptzSetsJsonArray);
        JSONObject reqData = new JSONObject();
        mapsSynJson.put("ptz_sets",ptzJson);

        List<MaintainArea> maintainAreaList = maintainAreaMapper.getAll(siteId);
        if(maintainAreaList!=null && maintainAreaList.size()>0) {
            List<Map> maintainAreaTemplist = new ArrayList<>();
            for (MaintainArea maintainArea : maintainAreaList) {
                Map maintainAreaTempMap = new HashMap();
                List<Map> pointList = new ArrayList<>();
                maintainAreaTempMap.put("id", maintainArea.getUid());
                maintainAreaTempMap.put("name", maintainArea.getName());
                pointList.add(transformPoint(maintainArea.getPoint1()));
                pointList.add(transformPoint(maintainArea.getPoint2()));
                pointList.add(transformPoint(maintainArea.getPoint3()));
                pointList.add(transformPoint(maintainArea.getPoint4()));
//                pointList.add(transformPoint(maintainArea.getPoint5()));
                maintainAreaTempMap.put("points", pointList);
                maintainAreaTemplist.add(maintainAreaTempMap);
            }
            JSONArray maintainAreaJsonArray = JSONArray.parseArray(JSON.toJSONString(maintainAreaTemplist, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue));
            JSONObject maintainAreaSynJson = new JSONObject();
            maintainAreaSynJson.put("sync_style", "full");
            maintainAreaSynJson.put("full", maintainAreaJsonArray);
            mapsSynJson.put("obstacles", maintainAreaSynJson);
        }

        List<ChargeRoom> chargeRoomList = chargeRoomMapper.findListBySiteId(siteId);
        if(chargeRoomList!=null && chargeRoomList.size()>0) {
            List<Map> chargeRoomTemplist = new ArrayList<>();
            for (ChargeRoom room : chargeRoomList) {
                Map chargeRoomTempMap = new HashMap();
                chargeRoomTempMap.put("id", room.getUid());
                chargeRoomTempMap.put("name", room.getName());
                String chargeRoomPoints=room.getCorners();
                List<Map> pointList =transformRoomPoint(chargeRoomPoints);
                chargeRoomTempMap.put("points", pointList);
                chargeRoomTemplist.add(chargeRoomTempMap);
            }
            JSONArray robotParamJsonArray = JSONArray.parseArray(JSON.toJSONString(chargeRoomTemplist, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue));
            JSONObject robotParamSynJson = new JSONObject();
            robotParamSynJson.put("sync_style", "full");
            robotParamSynJson.put("full", robotParamJsonArray);
            mapsSynJson.put("charge_rooms",robotParamSynJson);
        }
        String mapsSynDataString =mapsSynJson.toJSONString();
        String type="maps";
        Result  result= GeneralService.call(memRobot.getCh(),MsgNames.node_task_manage,MsgNames.service_data_syn,type,mapsSynDataString,5);
        if(result.getCode()==1){
            GeneralServiceAckMsg ackMsg=(GeneralServiceAckMsg)result.getData();
            if(ackMsg.getRet_code().equals("true")){
                return true;
            }
        }
        return false;
    }


    /**
     * 只同步ptz数据，全量
     * @param memRobot
     * @return
     */
    public boolean syncMarkPtzSetData(MemRobot memRobot){
        /*int syncType=(Integer)params.get("syncType");
        List<PtzSet> list=null;
        list = ptzSetMapper.selectListBySite(memRobot.getSiteId());
        Iterator<PtzSet> it = list.iterator();
        while(it.hasNext()) {
            PtzSet item = it.next();
            if(item.getStatus()!=1 && item.getSetted()!=1){
                it.remove();
            }
        }
        if(list==null || list.size()==0){
            return ResultUtil.build(0,"没有预置位需要同步",null);
        }
        if(syncType==2){
            return syncPreset.syncPresetToRobot(robotId,1,1,list);
        }else{
            boolean ret=syncPreset.syncPtzSetData(robotId,list,list.size());
            if(ret){
                return ResultUtil.build(1,"同步预置位成功",null);
            }else{
                return ResultUtil.build(-1,"同步预置位失败",null);
            }
        }*/
        return false;
    }

    public boolean syncParamsData(MemRobot memRobot){
        String robotId =memRobot.getRobotId();
        JSONObject paramsSynJson = new JSONObject();

        List<Map> sysParamList =  sysParamMapper.sysDataSyn();
        if(sysParamList!=null && sysParamList.size()>0) {
            List<Map> sysParamTemplist = new ArrayList<>();
            for (Map map : sysParamList) {
                Map sysParamtempMap = new HashMap();
                sysParamtempMap.put("id", map.get("key"));
                sysParamtempMap.put("name", map.get("name"));
                sysParamtempMap.put("value", map.get("value"));
                sysParamtempMap.put("desc", map.get("desc"));
                sysParamTemplist.add(sysParamtempMap);

            }
            JSONArray sysParamJsonArray = JSONArray.parseArray(JSON.toJSONString(sysParamTemplist, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue));
            JSONObject sysParamSynJson = new JSONObject();
            sysParamSynJson.put("sync_style", "full");
            sysParamSynJson.put("full", sysParamJsonArray);

            paramsSynJson.put("sys_params", sysParamSynJson);
        }
        List<Map> robotParamList = robotParamMapper.sysRobotParam(robotId);
        if(robotParamList!=null && robotParamList.size()>0) {
            List<Map> robotParamTemplist = new ArrayList<>();
            for (Map map : robotParamList) {
                Map robotParamTempMap = new HashMap();
                robotParamTempMap.put("id", map.get("key"));
                robotParamTempMap.put("name", map.get("name"));
                robotParamTempMap.put("value", map.get("value"));
                robotParamTempMap.put("desc", map.get("desc"));
                robotParamTemplist.add(robotParamTempMap);

            }
            JSONArray robotParamJsonArray = JSONArray.parseArray(JSON.toJSONString(robotParamTemplist, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue));
            JSONObject robotParamSynJson = new JSONObject();
            robotParamSynJson.put("sync_style", "full");
            robotParamSynJson.put("full", robotParamJsonArray);
            paramsSynJson.put("robot_params",robotParamSynJson);
        }
        String paramsData=paramsSynJson.toJSONString();

        String type="params";
        Result  result= GeneralService.call(memRobot.getCh(),MsgNames.node_task_manage,MsgNames.service_data_syn,type,paramsData,5);
        if(result.getCode()==1){
            GeneralServiceAckMsg ackMsg=(GeneralServiceAckMsg)result.getData();
            if(ackMsg.getRet_code().equals("true")){
                return true;
            }
        }
        return false;


    }

    private SentDataSynAckMsg toResultSentDataSynAck(JSONObject jsonObject) {
        SentDataSynAckMsg msg = new SentDataSynAckMsg();
        msg.setHeader(jsonObject.getJSONObject("header"));
        msg.setSender(jsonObject.getString("sender"));
        msg.setReceiver(jsonObject.getString("receiver"));
        msg.setTrans_id(jsonObject.getIntValue("trans_id"));
        msg.setTrans_id(jsonObject.getIntValue("robot_id"));
        msg.setRet_code(jsonObject.getString("ret_code"));
        msg.setRet_msg(jsonObject.getString("ret_msg"));
        msg.setData(jsonObject.getString("type"));
        msg.setData(jsonObject.getString("data"));
        return msg;
    }

    private Map<String,String> transformPoint(String point){
        Map map = new HashMap();
        String[] tempPoint =point.split(",");
        map.put("lon",Double.parseDouble(tempPoint[0]));
        map.put("lat",Double.parseDouble(tempPoint[1]));
        return map;
    }

    private List<Map>  transformRoomPoint(String point){
        List<Map> pointList = new ArrayList<>();
        String[] tempPoint =point.split(",");
        for (int i=0;i<4;i++){
            Map map = new HashMap();
            map.put("lon",Double.parseDouble(tempPoint[i*2+0]));
            map.put("lat",Double.parseDouble(tempPoint[i*2+1]));
            pointList.add(map);
        }
        return pointList;
    }

    private Map<String,Object> generateMapTaskPlan(Task task,java.util.Date date,String withinAdayTime) {
        String[] planTimeArray = withinAdayTime.split(";");//08:00-09:00;18:00-19:00;
        Map map = new HashMap();
        int status = 0;

        Date nowTime = new Date(System.currentTimeMillis());

        for (int i = 0; i < planTimeArray.length; i++) {
            String oneTime = planTimeArray[i];
            //创建一个task_plan
            String startAndEnd[] = oneTime.split("-");
            java.util.Date startDate = DateUtil.stringToDate(
                    DateUtil.dateToString(date, "yyyy.MM.dd") + " " + startAndEnd[0],
                    "yyyy.MM.dd HH:mm");
            java.util.Date endDate = DateUtil.stringToDate(
                    DateUtil.dateToString(date, "yyyy.MM.dd") + " " + startAndEnd[1],
                    "yyyy.MM.dd HH:mm");
            //原任务中名称中的日期被替换为 任务计划执行的日期
            String newName = StringUtil.trimSuffixDate(task.getName());
            //为了区别日内多段时间运行，在任务名称后增加了 -i
            //newName = newName + DateUtil.dateToString(date, "yyyyMMddHHmmss");
            newName = newName + DateUtil.dateToString(date, "yyyyMMdd");
            if (planTimeArray.length > 1) {
                newName = newName + "-" + i;
            }
            if(endDate.getTime() <= nowTime.getTime()){
                status = 7;
            }

            map.put("uid", task.getUid());
            map.put("task_id", task.getUid());
            map.put("create_time", "");
            map.put("name", newName);
            map.put("site_id", task.getSiteId());
            map.put("robot_id", task.getRobotId());
            map.put("plan_start_time", startDate);
            map.put("plan_end_time", endDate);
            map.put("user_id", task.getUserId());
            map.put("status", status);
            map.put("real_start_time", "");
            map.put("real_end_time", "");
            map.put("priority", "");
            map.put("audit_status", "");
            map.put("audit_user_id", "");
            map.put("audit_time", "");
        }
        return map;
    }

    private List<Map> getJobsByMonth(List<Map> resultList){
        for(Map map:resultList) {
            Date date=(Date) map.get("plan_start_time");
            String tempDate= DateUtil.dateToString(date,"yyyy-MM-dd");
            Date tranformDate = DateUtil.stringToDate(tempDate,"yyyy-MM-dd");
            map.replace("plan_start_time",tranformDate);
        }
        Map<Date,Integer> tempmap = new HashMap<>();
        for(Map map:resultList) {
            if(tempmap.containsKey(map.get("plan_start_time"))) {
                tempmap.put((Date) map.get("plan_start_time"), ((Integer)tempmap.get(map.get("plan_start_time"))).intValue()+1);
            }else {
                tempmap.put((Date) map.get("plan_start_time"),new Integer(1));
            }
        }
        List<Map> date = new ArrayList<>();
        for(Map.Entry<Date,Integer> entry:tempmap.entrySet()){
            Map map =new HashMap<>();
            Date mapKey = entry.getKey();
            Integer mapValue = entry.getValue();
            map.put("taskDate",mapKey);
            map.put("num",mapValue);
            date.add(map);
        }
        return date;
    }
}

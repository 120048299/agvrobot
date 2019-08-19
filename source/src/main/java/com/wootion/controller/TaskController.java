package com.wootion.controller;

import com.github.pagehelper.PageInfo;
import com.wootion.agvrobot.dto.UserBean;
import com.wootion.agvrobot.session.SessionRobot;
import com.wootion.agvrobot.utils.DateUtil;
import com.wootion.agvrobot.utils.FileUtil;
import com.wootion.commons.Constans;
import com.wootion.commons.JOB_STATUS;
import com.wootion.commons.Result;
import com.wootion.commons.TASK_TYPE;
import com.wootion.mapper.TaskPeriodMapper;
import com.wootion.mapper.TaskTypeMapper;
import com.wootion.model.Job;
import com.wootion.model.Site;
import com.wootion.model.Task;
import com.wootion.model.TaskPeriod;
import com.wootion.robot.MemRobot;
import com.wootion.robot.MemUtil;
import com.wootion.service.DevService;
import com.wootion.service.ITaskService;
import com.wootion.task.RobotTaskStatus;
import com.wootion.taskmanager.TaskControl;
import com.wootion.utiles.DataCache;
import com.wootion.utiles.ResultUtil;
import com.wootion.utiles.SessionManager;
import com.wootion.utiles.poi.ExportExcel;
import com.wootion.vo.PeriodInfo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/req_svr/task")
public class TaskController {
    //private TaskDaoImpl taskDao;

    @Autowired
    private ITaskService taskService;

    @Autowired
    private DevService devService;
    @Autowired
    TaskPeriodMapper taskPeriodMapper;
    @Autowired
    TaskTypeMapper taskTypeMapper;

    Logger logger = LoggerFactory.getLogger(TaskController.class);


      private String getRobotIp(HttpServletRequest request) {
          String token = request.getHeader(Constans.TOKEN);
          SessionRobot sessionRobot = (SessionRobot) SessionManager.getSessionEntity(token, Constans.SESSION_ROBOT);
          if (sessionRobot == null || sessionRobot.getRobot() == null) {
              return null;
          }
          String robotIp = sessionRobot.getRobot().getRobotIp();
          return robotIp;
      }

    private String getRobotId(HttpServletRequest request) {
        String token = request.getHeader(Constans.TOKEN);
        SessionRobot sessionRobot = (SessionRobot) SessionManager.getSessionEntity(token, Constans.SESSION_ROBOT);
        if (sessionRobot == null || sessionRobot.getRobot() == null) {
            return null;
        }
        String robotId = sessionRobot.getRobot().getUid();
        return robotId;
    }

    /**
     * todo wait
     * @param params
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/addTask")
    public Result addTask(@RequestBody Map params, HttpServletRequest request) {
        String token=request.getHeader(Constans.TOKEN);
        UserBean userBean = (UserBean) SessionManager.getSessionEntity(token,SessionManager.USER_BEAN);
        SessionRobot sessionRobot = (SessionRobot) SessionManager.getSessionEntity(token,Constans.SESSION_ROBOT);
        if (sessionRobot == null) {
            logger.error("sessionRobot is null ");
            return ResultUtil.failed("保存任务失败,没有选择机器人。");
        }
        if (sessionRobot.getRobot() == null) {
            logger.error("sessionRobot.getRobot() is null ");
            return ResultUtil.failed("保存任务失败,没有选择机器人。");
        }
        Site site=getRequestSite(request);
        Task task=new Task();
        String taskName=(String)params.get("taskName");
        boolean repeat=taskService.checkRepeatTaskName(taskName,site.getUid());
        if(repeat){
            return ResultUtil.failed("该任务名称已经存在，请修改。");
        }
        task.setName(taskName);
        task.setSiteId(site.getUid());
        task.setRobotId(sessionRobot.getUid());
        task.setDescription((String)params.get("taskDescription"));
        java.util.Date now=new java.util.Date();
        task.setCreateTime(now);
        task.setEditTime(now);
        task.setStatus(0);
        task.setMapTask(0);
        task.setSyncStatus(0);
        task.setEmergency(0);
        task.setUserId(userBean.getUserInfo().getUid());
        ArrayList<String> ptzIds=(ArrayList<String>)params.get("ptzIds");
        int ret=taskService.addTask(task,ptzIds);
        logger.info("savetask: ret= "+ret);
        if(ret<0){
            return ResultUtil.build(-1,"保存任务失败","");
        }else{
            DataCache.reload();
            String taskid = task.getUid();
            MemRobot memRobot = MemUtil.queryRobotById(sessionRobot.getUid());
            if(memRobot!=null && memRobot.isOnline()){
                boolean addTasksDataSynAck = taskService.addTasksDataSynAck(memRobot,taskid);
                if(addTasksDataSynAck){
                    return ResultUtil.build(0,"任务添加成功并同步到机器人。",task);
                }else{
                    return ResultUtil.build(1,"任务添加成功同步失败，需要手动同步。",task);
                }
            }else{
                //更新为未同步标志
                return ResultUtil.build(1,"任务添加成功同步失败，需要手动同步。",task);
            }
        }
    }

    
    @ApiOperation(value = "增加地图任务",notes = "从地图上下任务")
    @ResponseBody
    @PostMapping(value = "/addMapTask")
    public Result addRobotMapTask(@RequestBody Map params, HttpServletRequest request) {
        String token=request.getHeader(Constans.TOKEN);
        UserBean userBean = (UserBean) SessionManager.getSessionEntity(token,SessionManager.USER_BEAN);
        String robotIp=getRobotIp(request);
        if(robotIp==null){
            return ResultUtil.failed("保存任务失败,没有选择机器人。。");
        }
        String robotId = getRobotId(request);
        MemRobot memRobot = MemUtil.queryRobot(robotIp);
        boolean canStartTask=memRobot.canStartTask();
        if(!canStartTask){
            return ResultUtil.failed("机器人当前状态不允许发起任务");
        }
        ArrayList<String> ptzIds=(ArrayList<String>)params.get("ptzIds");
        Site site=getRequestSite(request);
        String siteName=site.getName();
        //创建task
        java.util.Date now =new java.util.Date();
        Task task=new Task();
        //added by btrmg for name of emergency task 2019.01.23
        /*if(memRobot.getEmergency() == 1){
            task.setName(siteName+"紧急定位"+params.get("taskName"));
        }//added end
        else{
            task.setName(siteName+params.get("taskName"));
        }*/
        task.setName((String) params.get("taskName"));
        task.setSiteId(site.getUid());
        task.setRobotId(robotId);
        task.setDescription((String)params.get("taskDescription"));
        task.setCreateTime(now);
        task.setEditTime(now);
        task.setStatus(0);
        task.setUserId(userBean.getUserInfo().getUid());
        task.setMapTask(1);
        task.setEmergency(memRobot.getEmergency());
        task.setSyncStatus(0);

        Result result=taskService.addMapTask(memRobot,task,ptzIds);
        logger.info("addMapTask statTaskImmediately: result= "+result);
        return result;
    }

    @ApiOperation(value = "修改任务",notes = "taskId需要传入")
    @ResponseBody
    @PostMapping(value = "/modifyTask")
    public Result modifyTask(@RequestBody Map params,  HttpServletRequest request) {
        String token=request.getHeader(Constans.TOKEN);
        UserBean userBean = (UserBean) SessionManager.getSessionEntity(token,SessionManager.USER_BEAN);
        SessionRobot sessionRobot = (SessionRobot) SessionManager.getSessionEntity(token,Constans.SESSION_ROBOT);
        String robotId = getRobotId(request);
        if (robotId == null) {
            logger.error("sessionRobot.getRobot() is null ");
            return ResultUtil.failed("保存任务失败,没有选择机器人。");
        }
        Site site=getRequestSite(request);
        String siteName=site.getName();

        String taskId=(String)params.get("taskId");
        Task task=taskService.getTask(taskId);
        if(task==null){
            return ResultUtil.failed("保存任务失败,任务不存在。");
        }
        if(task.getStatus()!=0){
            return ResultUtil.failed("请先去激活任务。");
        }

        //修改时，日期保存最新的
        String name;
        java.util.Date now =new java.util.Date();
        task.setName((String) params.get("taskName"));
        task.setDescription((String)params.get("taskDescription"));
        task.setEditTime(now);
        task.setUserId(userBean.getUserInfo().getUid());
        task.setStatus(0);
        int ret=taskService.modifyTask(task,(ArrayList<String>)params.get("ptzIds"));
        logger.info("modifyTask: ret= "+ret);
        if(ret<0){
            return ResultUtil.failed("保存任务失败");
        }else{
            DataCache.reload();
            String taskid = task.getUid();
            MemRobot memRobot = MemUtil.queryRobotById(sessionRobot.getUid());
            if(memRobot!=null && memRobot.isOnline()){
                boolean addTasksDataSynAck = taskService.addTasksDataSynAck(memRobot,taskid);
                if(addTasksDataSynAck){
                    return ResultUtil.build(0,"任务修改成功并同步到机器人。",task);
                }else
                    return ResultUtil.build(1,"任务修改成功同步失败，需要手动同步。",task);
            }
            return ResultUtil.build(1,"任务修改成功同步失败，需要手动同步。",task);
        }
    }

    @ApiOperation(value = "查询任务列表",notes = "根据页码和每页条数查询(任务编制页面)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskTypeId",value = "任务类型", required = true, dataType = "String",paramType = "query" ),
            @ApiImplicitParam(name = "taskName",value = "任务名称模糊查询", required = false, dataType = "String",paramType = "query" ),
            @ApiImplicitParam(name = "pageNum",value = "页码", required = true, dataType = "Integer",paramType = "query" ),
            @ApiImplicitParam(name = "pageSize",value = "页码", required = false, dataType = "Integer",paramType = "query",
                    defaultValue = "10")
    })
    @ResponseBody
    @PostMapping("/getTaskList")
    public PageInfo getTaskList(@RequestBody Map params,HttpServletRequest request){
        int pageNum=0;
        if(params.get("pageNum")!=null)
        {
            pageNum= (int) params.get("pageNum");
        }
        int pageSize=10;
        if(params.get("pageSize")!=null)
        {
            pageSize= (int) params.get("pageSize");
        }
        Site site=getRequestSite(request);
        params.put("siteId",site.getUid());
        String robotId = getRobotId(request);
        params.put("robotId",robotId);
        PageInfo<Map> pageInfo= taskService.getTaskList(params,pageNum, pageSize);
        return pageInfo;
    }


    /**
     * 增加条件 是否审核  auditStatus
     * @param params
     * @param request
     * @return
     */
    @ApiOperation(value = "查询任务实例列表",notes = "任务日历页面,巡检报告生成页面")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum",value = "页码", required = true, dataType = "Integer",paramType = "query" ),
            @ApiImplicitParam(name = "pageSize",value = "每页查询数", required = false, dataType = "Integer",paramType = "query",defaultValue = "10"),
            @ApiImplicitParam(name = "name",value = "任务计划名称", required = true, dataType = "String",paramType = "params" ),
            @ApiImplicitParam(name = "statuses",value = "状态", required = true, dataType = "Array",paramType = "params" ),
            @ApiImplicitParam(name = "fromDate",value = "查询开始时间", required = true, dataType = "String",paramType = "params" ),
            @ApiImplicitParam(name = "toDate",value = "查询结束时间", required = true, dataType = "String",paramType = "params" )

    })

    @ResponseBody
    @PostMapping("/getJobsForRobot")
    public PageInfo getJobsForRobot(@RequestBody Map params,HttpServletRequest request){
        String robotId=getRobotId(request);
        if(robotId==null){
            return null;
        }
        params.put("robotId",robotId);

        int pageNum=0;
        if(params.get("pageNum")!=null)
        {
            pageNum= (int) params.get("pageNum");
        }
        int pageSize=10;
        if(params.get("pageSize")!=null)
        {
            pageSize= (int) params.get("pageSize");
        }

        Site site=getRequestSite(request);
        params.put("siteId",site.getUid());
        //todo wait
        //PageInfo<Map> pageInfo= taskService.getJobsForRobot(params,pageNum,pageSize);
        PageInfo<Map> pageInfo=null;
                List<Map> list=pageInfo.getList();
        if(list!=null){
            for(Map map:list){
                Integer status=(Integer)map.get("status");
                map.put("status_name", JOB_STATUS.fromInt(status.intValue()).toStrValue());

                Integer auditStatus=(Integer)map.get("auditStatus");
                if(auditStatus==null ){
                    map.put("audit_status_name", "未审核");
                }else if(auditStatus==1){
                    map.put("audit_status_name", "已审核");
                }else{
                    map.put("audit_status_name", "未审核");
                }
                String taskTypeId=(String)map.get("task_type_id");
                map.put("task_type_name", TASK_TYPE.fromString(taskTypeId).toStrValue());
            }
        }
        return pageInfo;
    }

    /**
     * 增加条件 是否审核  auditStatus
     * @param params
     * @param request
     * @return
     */
    @ApiOperation(value = "查询任务计划列表",notes = "任务日历页面,巡检报告生成页面")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum",value = "页码", required = true, dataType = "Integer",paramType = "query" ),
            @ApiImplicitParam(name = "pageSize",value = "每页查询数", required = false, dataType = "Integer",paramType = "query",defaultValue = "10"),
            @ApiImplicitParam(name = "name",value = "任务计划名称", required = true, dataType = "String",paramType = "params" ),
            @ApiImplicitParam(name = "statuses",value = "状态", required = true, dataType = "Array",paramType = "params" ),
            @ApiImplicitParam(name = "fromDate",value = "查询开始时间", required = true, dataType = "String",paramType = "params" ),
            @ApiImplicitParam(name = "toDate",value = "查询结束时间", required = true, dataType = "String",paramType = "params" )

    })
    @ResponseBody
    @PostMapping("/getJobsList")
    public PageInfo getJobsList(@RequestBody Map params,HttpServletRequest request){

        int pageNum=0;
        if(params.get("pageNum")!=null)
        {
            pageNum= (int) params.get("pageNum");
        }
        int pageSize=10;
        if(params.get("pageSize")!=null)
        {
            pageSize= (int) params.get("pageSize");
        }

        Site site=getRequestSite(request);
        params.put("siteId",site.getUid());
        //todo wait
        //PageInfo<Map> pageInfo= taskService.getJobsForRobot(params,pageNum,pageSize);
        PageInfo<Map> pageInfo=null;
        List<Map> list=pageInfo.getList();
        if(list!=null){
            for(Map map:list){
                Integer status=(Integer)map.get("status");
                map.put("status_name", JOB_STATUS.fromInt(status.intValue()).toStrValue());

                Integer auditStatus=(Integer)map.get("auditStatus");
                if(auditStatus==null ){
                    map.put("audit_status_name", "未审核");
                }else if(auditStatus==1){
                    map.put("audit_status_name", "已审核");
                }else{
                    map.put("audit_status_name", "未审核");
                }
                String taskTypeId=(String)map.get("task_type_id");
                map.put("task_type_name", TASK_TYPE.fromString(taskTypeId).toStrValue());
            }
        }
        return pageInfo;
    }


    /**
     * 巡检报告生成  导出列表
     * @param params
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("/exportTaskPlanList")
    public Result exportTaskPlanList(@RequestBody Map params,HttpServletRequest request){
        Site site=getRequestSite(request);
        params.put("siteId",site.getUid());

        String robotId=getRobotId(request);
        if(robotId==null){
            return null;
        }
        params.put("robotId",robotId);
        //todo wait
        //List<Map> list= taskService.getJobsForRobot(params);
        List<Map> list=null;
        if(list==null || list.size()==0){
            ResultUtil.build(-1,"导出失败，没有数据。",null);
        }
        for(Map map:list){
            Integer status=(Integer)map.get("status");
            map.put("status_name", JOB_STATUS.fromInt(status.intValue()).toStrValue());

            Integer auditStatus=(Integer)map.get("auditStatus");
            if(auditStatus==null ){
                map.put("audit_status_name", "未审核");
            }else if(auditStatus==1){
                map.put("audit_status_name", "已审核");
            }else{
                map.put("audit_status_name", "未审核");
            }
            String taskTypeId=(String)map.get("task_type_id");
            map.put("task_type_name", TASK_TYPE.fromString(taskTypeId).toStrValue());
            java.util.Date  time=(java.util.Date)map.get("real_start_time");
            String timeStr = DateUtil.dateToString(time,"yyyy-MM-dd HH:mm");
            map.put("real_start_time_str",timeStr);
            time=(java.util.Date)map.get("real_end_time");
            timeStr = DateUtil.dateToString(time,"yyyy-MM-dd HH:mm");
            map.put("real_end_time_str",timeStr);
        }

        String fileName = "巡检报告生成任务报表"+FileUtil.dateRandom()+".xlsx";
        String basePath = FileUtil.getBasePath();
        String heads[]= {"任务名称","任务类型","任务状态","审核状态","任务开始时间","任务结束时间"};
        String keys[]={"name","task_type_name","status_name","audit_status_name","real_start_time_str","real_end_time_str"};
        int ret= ExportExcel.export(basePath+"report"+File.separator+fileName,"巡检报告生成任务",heads,keys,list);
        if(ret>=0){
            return ResultUtil.build(0,"导出成功",fileName);
        }else{
            return ResultUtil.failed();
        }
    }

     /**
     * 立即启动任务
     */
    @ResponseBody
    @GetMapping("startTaskImmediately/{taskId}")
    public Result startTaskImmediately(@PathVariable String taskId, HttpServletRequest request) {
        String robotIp=getRobotIp(request);
        Site site=getRequestSite(request);
        if(robotIp==null){
            return ResultUtil.failed("没有选择机器人。");
        }
        MemRobot memRobot = MemUtil.queryRobot(robotIp);
        String robotId = memRobot.getRobotId();
        boolean canStartTask=memRobot.canStartTask();
        if(!canStartTask){
            return ResultUtil.failed("机器人当前状态不允许发起任务");
        }
        Result result=taskService.startTaskImmediately(taskId,robotIp);
        logger.info("startTaskImmediately:"+result);
        return result;
    }

    /**
     * 没有用了
     * @param taskPeriod
     * @return
     */
    @ApiOperation(value = "设置任务周期",notes = "定期执行和周期执行弹出页面的保存.style不同 styleParam不同。" +
                                                "style为 隔天，保存间隔天数数字0,1,2...,0即每天; " +
                                                "每周 保存选择的周几：1,2,3,4,5,6,7 ;" +
                                                "每月保存选择的天，01,02,35,31," +
                                                "指定日期时：yyyy.mm.dd;yyyy.mm.dd;多个日期")
    @ResponseBody
    @PostMapping("/setTaskPeriod")
    public Result setTaskPeriod(@RequestBody TaskPeriod taskPeriod){
        Task task=taskService.getTask(taskPeriod.getTaskId());
        if(task==null){
            return ResultUtil.failed("任务不存在");
        }
        taskPeriod.setSiteId(task.getSiteId());
        int ret=taskService.setTaskPeriod(taskPeriod);
        if(ret<0){
            return ResultUtil.failed("设置任务周期失败");
        }else{
            return ResultUtil.success();
        }
    }

    @ResponseBody
    @PostMapping("/saveTaskPeriod")
    public Result setTaskPeriod(@RequestBody PeriodInfo periodInfo,HttpServletRequest request){
        String robotIp=getRobotIp(request);
        MemRobot memRobot = MemUtil.queryRobot(robotIp);
            if(periodInfo==null)
            {
                return ResultUtil.failed("没有接收到数据");
            }
            TaskPeriod taskPeriod=PeriodInfo.toPeriod(periodInfo);
            Task task=taskService.getTask(taskPeriod.getTaskId());
            if(task==null){
                return ResultUtil.failed("任务不存在");
            }

            if(task.getStatus()==1){
                return ResultUtil.failed("任务正在计划执行中，请先停止执行!");
            }
            taskPeriod.setSiteId(task.getSiteId());
            int ret=taskService.setTaskPeriod(taskPeriod);
            if(ret<0){
                logger.info("任务计划启动失败",taskPeriod,ret);
                return ResultUtil.failed("任务计划启动失败");
            }else{
//                return ResultUtil.build(0,"任务计划启动成功",null);
                String taskId = task.getUid();
                boolean sentTasksResult = taskService.sentTasksDataSynAck(memRobot, taskId);
                if (sentTasksResult) {
                    return ResultUtil.build(0,  "任务计划启动成功且任务同步成功。", "");
                }
                return ResultUtil.build(1, "同步任务失败，请检查机器人是否在线后重新同步", "");
            }
    }

    @ApiOperation(value = "查询周期设置",notes = "")
    @ApiImplicitParam(name="taskId", value = "任务Id", paramType = "String",required = true)
    @ResponseBody
    @GetMapping("/getTaskPeriod/{taskId}")
    public PeriodInfo getTaskPeriod(@PathVariable String taskId) {
        TaskPeriod taskPeriod= taskPeriodMapper.selectByTaskId(taskId);
        if(taskPeriod==null){
            PeriodInfo info=new PeriodInfo();
            info.setTaskId(taskId);
            info.setStyle(0);
            info.setPerDay("0");
            return info;
        }
        PeriodInfo info=PeriodInfo.toPeriodInfo(taskPeriod);
        return info;

    }

//    @ApiOperation(value = "删除周期设置",notes = "删除定期和周期的设置 ")
//    @ApiImplicitParam(name="taskId", value = "任务Id", paramType = "String",required = true)
//    @ResponseBody
//    @GetMapping("/deleteTaskPeriod/{taskId}")
//    public Result deleteTaskPeriod(@PathVariable String taskId) {
//        int ret=taskService.deleteTaskPeriod(taskId);
//        if(ret<0){
//            return ResultUtil.failed("删除周期和定期设置失败");
//        }else{
//            return ResultUtil.success();
//        }
//    }


    /**
     * 激活任务
     * @param taskId
     * @param request
     * @return
     */
    @ResponseBody
    @GetMapping("/activateTask/{taskId}")
    public Result activateTask(@PathVariable String taskId,HttpServletRequest request) {
        String robotIp=getRobotIp(request);
        MemRobot memRobot = MemUtil.queryRobot(robotIp);
        if(!memRobot.isOnline()){
            return ResultUtil.failed("停止执行任务失败,机器人不在线");
        }
        Result result=taskService.activateTask(taskId,robotIp);
        return  result;
    }

    /**
     * 去激活任务
     * @param taskId
     * @param request
     * @return
     */
    @ResponseBody
    @GetMapping("/stopTask/{taskId}")
    public Result stopTask(@PathVariable String taskId,HttpServletRequest request) {
        String robotIp=getRobotIp(request);
        MemRobot memRobot = MemUtil.queryRobot(robotIp);
        if(!memRobot.isOnline()){
            return ResultUtil.failed("停止执行任务失败,机器人不在线");
        }
        Result result=taskService.stopTask(taskId,robotIp);
        return  result;
    }


    /**
     * 删除任务,删除之前必须先去激活
     */
    @ResponseBody
    @GetMapping("/deleteTask/{taskId}")
    public Result deleteTask(@PathVariable String taskId,HttpServletRequest request) {
        String token=request.getHeader(Constans.TOKEN);
        String robotIp= getRobotIp(request);
        SessionRobot sessionRobot = (SessionRobot) SessionManager.getSessionEntity(token,Constans.SESSION_ROBOT);
        Result  result=taskService.deleteTask(taskId,robotIp);
        return  result;
/*
        MemRobot memRobot = MemUtil.queryRobotById(sessionRobot.getUid());
        if(!synStatus &&memRobot!=null && memRobot.isOnline()){
            int ret = taskService.sentDeleteTaskDataSynAck(memRobot,taskId);
            if(ret==-1){
                return ResultUtil.failed("删除任务失败, 该任务不存在或者已被删除!");
            }else if(ret==-2){
                return ResultUtil.failed("删除任务失败, 请先停止执行该任务!");
            }else if(ret==0){
                return ResultUtil.failed("同步失败，删除任务失败!");
            }else{
                return ResultUtil.success("删除任务且同步成功!");
            }
        }
        return ResultUtil.failed("同步失败，删除任务失败,请检查机器人是否在线!");*/

    }

    /**
     * 删除任务实例 (从任务日历页面操作)
     */
    @ResponseBody
    @GetMapping("/deleteJob/{jobId}")
    public Result deleteJob(@PathVariable String jobId, HttpServletRequest request) {
        String robotIp=getRobotIp(request);
        if(jobId==null || "".equals(jobId)){
          return ResultUtil.failed("没有选择任务实例");
        }
        Result result=taskService.cancelJob(jobId,robotIp);
        return result;
    }


    @ApiOperation(value = "暂停任务计划",notes = "暂停下发的任务(从任务日历页面操作)")
    @ApiImplicitParam(name="jobId", value = "任务计划Id", paramType = "String",required = true)
    @ResponseBody
    @GetMapping("/pauseJob/{jobId}")
    public Result pauseJob(@PathVariable String jobId,HttpServletRequest request) {
        String robotIp=getRobotIp(request);
        if(robotIp==null){
            return ResultUtil.failed("没有选择机器人。");
        }
        String token=request.getHeader(Constans.TOKEN);
        SessionRobot sessionRobot = (SessionRobot) SessionManager.getSessionEntity(token,Constans.SESSION_ROBOT);
        MemRobot memRobot = MemUtil.queryRobotById(sessionRobot.getUid());
        if(memRobot!=null && memRobot.isOnline()) {
            Result result = TaskControl.suspendJob(memRobot, 0);
            if (result.getCode() ==0) {
                return ResultUtil.success();
            } else {
                return ResultUtil.failed("暂停任务失败" + result.getMsg());
            }
        }else {
            return ResultUtil.failed("暂停任务失败,请检查机器人是否在线" );
        }
    }


    /**
     * 监控界面原地暂停机器人执行任务
     * @param request
     * @return
     */
    @ResponseBody
    @GetMapping("/pauseRobotJob")
    public Result pauseRobotJob(HttpServletRequest request) {
        String robotIp=getRobotIp(request);
        if(robotIp==null){
            return ResultUtil.failed("没有选择机器人。");
        }
        MemRobot memRobot = MemUtil.queryRobot(robotIp);
        if(memRobot==null ){
            return ResultUtil.build(-1,"没有机器人",null);
        }
        RobotTaskStatus robotTaskStatus=memRobot.getRobotTaskStatus();
        if(robotTaskStatus==null ||robotTaskStatus.getJob()==null){
            return ResultUtil.build(-2,"当前没有任务在运行",null);
        }
        Result result=TaskControl.suspendJob(memRobot,1);
        if(result.getCode()==0){
            memRobot.setStopInPlace(1);
            memRobot.setIdleStartTime(System.currentTimeMillis());
            return ResultUtil.success();
        }else{
            logger.info("原地暂停机器人失败,"+result.getMsg());
            return ResultUtil.failed("原地暂停机器人失败。");
        }
    }
    /**
     * 监控界面停止机器人执行任务,停止机器人原地，强制结束任务
     */
    @ResponseBody
    @GetMapping("/terminateRobotJob")
    public Result terminateRobotTaskPlan(HttpServletRequest request) {
        String robotIp=getRobotIp(request);
        if(robotIp==null){
            return ResultUtil.failed("没有选择机器人。");
        }
        MemRobot memRobot = MemUtil.queryRobot(robotIp);
        if(memRobot==null ){
            return ResultUtil.build(-1,"没有机器人",null);
        }
        RobotTaskStatus robotTaskStatus=memRobot.getRobotTaskStatus();
        if(robotTaskStatus==null ||robotTaskStatus.getJob()==null){
            return ResultUtil.build(-2,"当前没有任务在运行",null);
        }
        Result result=TaskControl.cancelJob(memRobot,robotTaskStatus.getJob().getUid(),1);
        if(result.getCode()==0){
            memRobot.setStopInPlace(2);
            memRobot.setIdleStartTime(System.currentTimeMillis());
            return ResultUtil.success();
        }else{
            logger.info("原地终止机器人失败,"+result.getMsg());
            return ResultUtil.failed("原地终止机器人失败。");
        }
    }
    //added end
    //取消原地终止状态：原地终止状态不自动发起任务，web上可下地图任务和立即启动任务（紧急定位也是如此）
    @ResponseBody
    @GetMapping("/cancelStopStatus")
    public Result cancelStopStatus(HttpServletRequest request) {
        String robotIp=getRobotIp(request);
        if(robotIp==null){
            return ResultUtil.failed("没有选择机器人。");
        }
        MemRobot memRobot = MemUtil.queryRobot(robotIp);
        if(memRobot==null ){
            return ResultUtil.build(-1,"没有机器人",null);
        }
        Result result=TaskControl.endWait(memRobot);
        if(result.getCode()==0){
            memRobot.setStopInPlace(0);
            memRobot.setIdleStartTime(System.currentTimeMillis());
            return ResultUtil.success();
        }else{
            logger.info("结束原地终止状态 失败,"+result.getMsg());
            return ResultUtil.failed("结束原地终止状态 失败。");
        }
    }

    @ResponseBody
    @PostMapping("/delayPauseTime")
    public Result delayPauseTime(HttpServletRequest request){
        String robotIp=getRobotIp(request);
        if(robotIp==null){
            return ResultUtil.failed("没有选择机器人。");
        }
        MemRobot memRobot = MemUtil.queryRobot(robotIp);
        if(memRobot==null ){
            return ResultUtil.build(-1,"没有机器人",null);
        }
        //新的时间开始计算
        memRobot.setIdleStartTime(System.currentTimeMillis());
        logger.info(" delayPauseTime setIdleStartTime "+memRobot.getIdleStartTime());
        return ResultUtil.success();
    }

    /**
     * 监控界面：恢复运行之前原地暂停的job.  或者 任务展示恢复暂停的任务
     *
     */
    @ResponseBody
    @PostMapping("/restartJob")
    public Result restartRobotJob(@RequestBody Map params,HttpServletRequest request) {
        String jobId=(String)params.get("jobId");
        int style=(Integer)params.get("style");
        String robotIp=getRobotIp(request);
        if(robotIp==null){
            return ResultUtil.failed("没有选择机器人。");
        }
        MemRobot memRobot = MemUtil.queryRobot(robotIp);
        if(memRobot==null ){
            return ResultUtil.build(-1,"没有机器人",null);
        }
        Result result=TaskControl.resumeJob(memRobot,jobId,style);
        if(result.getCode()==0){
            memRobot.setStopInPlace(0);
            return ResultUtil.success();
        }else{
            logger.info("恢复运行失败,jobId="+jobId+","+result.getMsg());
            return ResultUtil.failed("恢复运行失败。");
        }
    }

    /**
     * 充电返航,监控界面发起一键返航
     * @param request
     * @return
     */
    @ResponseBody
    @GetMapping("/startCharge")
    public Result startCharge(HttpServletRequest request) {
        logger.info("taskController startCharge 发起一键返航");
        String robotIp=getRobotIp(request);
        if(robotIp==null){
            return ResultUtil.failed("没有选择机器人。");
        }
        if(DataCache.getSysParamInt("robot.hasChargeRoom")==0){
            return ResultUtil.failed("操作失败。没有配置充电房。");
        }
        MemRobot memRobot=MemUtil.queryRobot(robotIp);
        if(!memRobot.isOnline()){
            return ResultUtil.failed("操作失败。机器人不在线。");
        }
        int ret = taskService.startCharge(robotIp);
        if(ret==-2){
            return ResultUtil.failed("发起一键返航失败,正在处理充电流程或者停止充电过程中。");
        }else if(ret==-3){
            return ResultUtil.failed("发起一键返航失败。机器人正在手柄控制中。");
        }else if(ret==-4){
            return ResultUtil.failed("发起一键返航失败。机器人处于后台遥控模式，请先释放后台遥控。");
        }else{
            return ResultUtil.success();
        }
    }


    /**
     * 监控界面发起结束一键返航（或者结束充电）
     * @param request
     * @return
     */
    @ResponseBody
    @GetMapping("/stopCharge")
    public Result stopCharge(HttpServletRequest request) {
        logger.info("taskController stopCharge 结束一键返航");
        String robotIp=getRobotIp(request);
        if(robotIp==null){
            return ResultUtil.failed("没有选择机器人。");
        }
        if(DataCache.getSysParamInt("robot.hasChargeRoom")==0){
            return ResultUtil.failed("操作失败。没有配置充电房。");
        }
        MemRobot memRobot=MemUtil.queryRobot(robotIp);
        if(!memRobot.isOnline()){
            return ResultUtil.failed("操作失败。机器人不在线。");
        }
        Result result = taskService.stopCharge(robotIp);
        if(result.getCode()!=0){
            return ResultUtil.failed("操作失败。");
        }else{
            return ResultUtil.success();
        }
    }

    /**
     * 日历展示数据查询方法
     *
     * @Author Luolin
     */

    @ApiOperation(value = "按月查询任务统计信息",notes = "任务日历页面：年月 yyyy-MM" )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "date",value = "查询年月", required = true, dataType = "String",paramType = "params" )
    })
    @ResponseBody
    @GetMapping("/getTaskStatisticsByMonth/{date}")
    public Result getTaskStatisticsByMonth(@PathVariable String date,HttpServletRequest request){
        Site site=getRequestSite(request);
        String robotId = getRobotId(request);
        List<?> list= taskService.getTaskPlanStatisticsByMonth(site.getUid(),robotId,date+"-01");
        return ResultUtil.success(list);

    }



    @ApiOperation(value = "查询等待审核的任务计划",notes = " ")
    @ResponseBody
    @GetMapping("/getTaskPlanWaitAudit")
    public List<Job> getTaskPlanWaitAudit(HttpServletRequest request){
        Site site = getRequestSite(request);
        List<Job> list = taskService.getTaskPlanWaitAudit(site.getUid());
        return list;
    }

    @GetMapping("/getAllTaskPlan")
    public List<Job> getAllTaskPlan(HttpServletRequest request){
        Site site = getRequestSite(request);
        List<Job> list = taskService.getAllTaskPlan(site.getUid());
        return list;
    }

    /**
     * 从请求session中读取siteId
     * @param request
     * @return
     */
    private Site getRequestSite(HttpServletRequest request){
        String token=request.getHeader(Constans.TOKEN);
        Site site = (Site) SessionManager.getSessionEntity(token,Constans.SESSION_SITE);
        if(site==null){
            return null;
        }
        return site;
    }


    @ResponseBody
    @PostMapping("/GetTaskListData")
    public PageInfo getTaskListData(@RequestBody Map params,HttpServletRequest request){
        int pageNum=0;
        if(params.get("pageNum")!=null)
        {
            pageNum= (int) params.get("pageNum");
        }
        int pageSize=10;
        if(params.get("pageSize")!=null)
        {
            pageSize= (int) params.get("pageSize");
        }
        Site site=getRequestSite(request);
        params.put("siteId",site.getUid());
        String robotId=getRobotId(request);
        params.put("robotId",robotId);
        PageInfo<Map> pageInfo= taskService.getTaskListData(params,pageNum,pageSize);
        return pageInfo;
    }


    @ResponseBody
    @PostMapping("/getMapTaskName")
    public Result getMapTaskName(HttpServletRequest request){
        String robotId=getRobotId(request);
        Site site=getRequestSite(request);
        String taskName=taskService.getMapTaskName(site,robotId);
        if(taskName==null){
            return ResultUtil.failed();
        }else{
            return ResultUtil.build(0,"",taskName);
        }
    }

    @ApiOperation(value = "立即同步任务",notes = "根据taskId，用户点击按钮以后立刻同步任务")
    @ResponseBody
    @GetMapping("synTask/{taskId}")
    public Result synTask(@PathVariable String taskId, HttpServletRequest request) {
        String robotIp=getRobotIp(request);
        Site site=getRequestSite(request);
        if(robotIp==null){
            return ResultUtil.failed("没有选择机器人。");
        }
        MemRobot memRobot = MemUtil.queryRobot(robotIp);
        String robotId = memRobot.getRobotId();
        if(memRobot!=null && memRobot.isOnline()){
            boolean addTasksDataSynAck = taskService.addTasksDataSynAck(memRobot,taskId);
            if(addTasksDataSynAck){
                return ResultUtil.build(0,robotId +"号机器人任务同步成功。","");
            }else
                return ResultUtil.build(-1,robotId +"同步任务失败，请检查机器人是否在线后重新同步。","");
        }
        return ResultUtil.build(-1,robotId +"同步任务失败，请检查机器人是否在线后重新同步。","");

    }


}


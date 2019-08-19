package com.wootion.taskmanager;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.wootion.Debug;
import com.wootion.agvrobot.utils.DateUtil;
import com.wootion.agvrobot.utils.FileUtil;
import com.wootion.agvrobot.utils.StringUtil;
import com.wootion.agvrobot.utils.UUIDUtil;
import com.wootion.commons.Result;
import com.wootion.mapper.*;
import com.wootion.model.*;
import com.wootion.protocols.robot.GeneralService;
import com.wootion.protocols.robot.msg.GeneralServiceAckMsg;
import com.wootion.protocols.robot.msg.GeneralTopicMsg;
import com.wootion.protocols.robot.msg.MsgNames;
import com.wootion.robot.ChargeInfo;
import com.wootion.robot.MemRobot;
import com.wootion.robot.MemUtil;
import com.wootion.robot.MemWeatherStatus;
import com.wootion.service.ITaskLogService;
import com.wootion.task.ReadScaleQueue;
import com.wootion.task.RobotTaskStatus;
import com.wootion.task.event.ReadNoticeEvent;
import com.wootion.task.event.TaskFinishedEvent;
import com.wootion.utiles.DataCache;
import com.wootion.utiles.SFtpUtil;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 处理任务执行状态和任务执行结果
 */
@Component
public class ResultSynThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(ResultSynThread.class);
    private static final String dateFormat="yyyy-MM-dd HH:mm:ss";
    private static LinkedBlockingQueue<Object> queque = new LinkedBlockingQueue<>();

    public static void addEvent(Object evt) {
        queque.add(evt);
    }
    private boolean online = true;
    private boolean isDealing = false;
    SFtpUtil sFtpUtil=new SFtpUtil();

    @Autowired
    TaskMapper taskMapper;
    @Autowired
    TaskPtzMapper taskPtzMapper;
    @Autowired
    PtzSetMapper ptzSetMapper;
    @Autowired
    TaskLogMapper  taskLogMapper;
    @Autowired
    JobMapper jobMapper;

    @Autowired
    ITaskLogService taskLogService;

    @Autowired
    JobPathSectionMapper jobPathSectionMapper;
    @Autowired
    JobPathSectionMarkMapper jobPathSectionMarkMapper;

    public static Object takeEvent() {
        try {
            return queque.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Autowired
    private SysParamMapper sysParamMapper;

    @Autowired
    private RobotParamMapper robotParamMapper;



    @Override
    public void run() {
        logger.info("ResultSynThread started!");
//        RosBridgeClient client = null;

        while (true){
            try{
                Thread.sleep(100);
                Object obj=takeEvent();
                if(obj instanceof TaskFinishedEvent){
                    queryTaskLog((TaskFinishedEvent)obj);
                    continue;
                }
                //任务状态消息（定时） 和 ptz状态消息（不定时）
                JSONObject jobj =(JSONObject) obj;
                String id =jobj.getString("id");
                String op = jobj.getString("op");
                String topic = jobj.getString("topic");
                String robot_ip = jobj.getString("robot_ip");
                if (MsgNames.topic_task_status.equals(topic)) {
                    GeneralTopicMsg taskStatusMsg= toGeneralTopicMsg(jobj.getJSONObject("msg"));
                    taskStatusMsg.setRobot_ip(robot_ip);
                    //System.out.println("get taskStatusMsg");
                    handleTaskStatus(taskStatusMsg);
                    continue;
                }
                if(MsgNames.topic_ptz_status.equals(topic)) {
                    GeneralTopicMsg ptzStatusMsg= toGeneralTopicMsg(jobj.getJSONObject("msg"));
                    ptzStatusMsg.setRobot_ip(robot_ip);
                    //System.out.println("get taskStatusMsg");
                    handlePtzStatus(ptzStatusMsg);
                    continue;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 当前ptzSet情况，开始和结束时从task_manager发送上来(只是为了显示用，不做结果处理)
     * @param msg
     */
    private void handlePtzStatus(GeneralTopicMsg msg) {
        MemRobot memRobot = MemUtil.queryRobot(msg.getRobot_ip());
        if (memRobot == null) {
            return;
        }
        RobotTaskStatus robotTaskStatus=memRobot.getRobotTaskStatus();
        if(robotTaskStatus==null){
            return ;
        }
        JSONObject ptzSetObj=JSONObject.parseObject(msg.getData());
        String id=ptzSetObj.getString("id");
        int status=ptzSetObj.getIntValue("status");
        String failedReason=ptzSetObj.getString("failed_reason");
        String startTime=ptzSetObj.getString("start_time");
        String endTime=ptzSetObj.getString("end_time");
        PtzSet ptzSet=DataCache.findPtzSetByUid(memRobot.getSiteId(),id);
        logger.debug("get ptzSet status "+ptzSet.toShortString());
        robotTaskStatus.setPtzSet(ptzSet);
    }

    private void handleTaskStatus(GeneralTopicMsg msg) {
        //logger.debug(String.format("********************************handleTaskStatus msg = %s",msg.toString()));
        // 如果对应操作机器人不存在，丢弃消息
        MemRobot memRobot = MemUtil.queryRobot(msg.getRobot_ip());
        if (memRobot == null) {
            return;
        }
        updateRobotTaskStatus(msg);
        if(isDealing){
            return;
        }
        try{
            JSONObject jobj=JSONObject.parseObject(msg.getData());
            //如果有变化
            int  taskCount=jobj.getIntValue("task_count");
            int  jobCount=jobj.getIntValue("job_count");
            //logger.debug("*******taskCount="+taskCount+" jobCount="+jobCount);
            if(taskCount>0 || jobCount>0){
                isDealing=true;
                List<Map> confirmTaskList=new ArrayList<>();
                if(taskCount>0){
                    Result result=queryTasks(memRobot.getCh());
                    if(result.getCode()==1){
                        List taskIdList= JSON.parseArray((String) result.getData());
                        for (int i = 0; taskIdList!=null && i < taskIdList.size(); i++) {
                            System.out.println(taskIdList.get(i).toString());
                            Map ret=synTask(memRobot.getCh(),taskIdList.get(i).toString());
                            if(ret!=null){
                                confirmTaskList.add(ret);
                            }
                        }
                    }
                }
                List <Map> confirmJobList=new ArrayList<>();
                if(jobCount>0){
                    Result result=queryJobs(memRobot.getCh());
                    if(result.getCode()==1 ) {
                        if(result.getData()!=null || !"".equals(result.getData() ) ){
                            List jobIdList= JSON.parseArray((String) result.getData());
                            for (int i = 0; jobIdList!=null && i < jobIdList.size(); i++) {
                                //System.out.println("jobId="+jobIdList.get(i).toString());
                                Map map=synJob(memRobot.getCh(), jobIdList.get(i).toString());
                                if(map!=null){
                                    confirmJobList.add(map);
                                }
                            }
                        }
                    }
                }
                Map <String,Object> confirmInfo=new HashMap<>();
                confirmInfo.put("tasks",confirmTaskList);
                confirmInfo.put("jobs",confirmJobList);
                if(confirmTaskList.size()>0 || confirmJobList.size()>0){
                    confirm(memRobot.getCh(),confirmInfo);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            isDealing=false;
        }
    }


    private Map synTask(Channel ch,String taskId){
        Result result=queryTask(ch,taskId);
        if(result.getCode()==1) {
            String syncTime=DateUtil.dateToString(new Date(),dateFormat);
            String retstr=(String)result.getData();
            JSONObject taskObj=JSONObject.parseObject(retstr);
            String id = taskObj.getString("id");
            int status = taskObj.getIntValue("status");
            logger.info("更新task "+taskId+"status="+status);
            taskMapper.updateStatus(id,status);
            //发送确认更新消息
            Map<String,String> confirmTask=new HashMap<>();
            confirmTask.put("id",id);
            confirmTask.put("report_time",syncTime);
            return confirmTask;
        }
        return null;
    }


    private Map synJob(Channel ch,String pJobId){
        Result result=queryJob(ch,pJobId);
        String syncTime=null;
        if(result.getCode()==1) {
            List<Map> ptzConfirmList=new ArrayList<>();
            String retstr=(String)result.getData();
            JSONObject jobObj=JSONObject.parseObject(retstr);
            //logger.debug("jobObj="+jobObj);
            java.util.Date reportTime = DateUtil.stringToDate(jobObj.getString("report_time"),dateFormat);
            syncTime=jobObj.getString("report_time");
            //System.out.println("job report_time="+syncTime);
            String id = jobObj.getString("id");
            String taskId = jobObj.getString("task_id");
            JSONArray pathMarksArray = jobObj.getJSONArray("path_marks");
            JSONArray pathMustMarksArray = jobObj.getJSONArray("path_must_marks");
            int pathIndex=-1;
            if(jobObj.containsKey("path_index")){
                pathIndex = jobObj.getIntValue("path_index");
            }
            logger.info("pathIndex="+pathIndex+"  pathMarks="+pathMarksArray);
            String[] pathMarks=null;
            if(pathMarksArray!=null){
                pathMarks=new String[pathMarksArray.size()];
                for(int i=0;i<pathMarksArray.size();i++){
                    pathMarks[i]=pathMarksArray.getString(i);
                }
            }
            List<Map>  allPathSection=new ArrayList<>();
            JSONArray hisPathObj = jobObj.getJSONArray("his_path");
            if(hisPathObj!=null){
                for(int i=0;i<hisPathObj.size();i++){
                    JSONObject pathObj =hisPathObj.getJSONObject(i);
                    int index = pathObj.getIntValue("index");
                    JSONArray marksArray = pathObj.getJSONArray("marks");
                    JSONArray mustMarksArray = pathObj.getJSONArray("must_marks");
                    List<String> markList=null;
                    if(marksArray!=null){
                        markList=marksArray.toJavaList(String.class);
                    }
                    List<String> mustMarkList=null;
                    if(mustMarksArray!=null){
                        mustMarkList=mustMarksArray.toJavaList(String.class);
                    }
                    Map data=new HashMap();
                    data.put("index",index);
                    data.put("markList",markList);
                    data.put("mustMarkList",mustMarkList);
                    allPathSection.add(data);
                }
            }

            List<String> markList=null;
            if(pathMarksArray!=null){
                markList=pathMarksArray.toJavaList(String.class);
            }
            List<String> mustMarkList=null;
            if(pathMustMarksArray!=null){
                mustMarkList=pathMustMarksArray.toJavaList(String.class);
            }
            Map data=new HashMap();
            data.put("index",pathIndex);
            data.put("markList",markList);
            data.put("mustMarkList",mustMarkList);
            allPathSection.add(data);

            int taskStatus = jobObj.getIntValue("task_status");

            java.util.Date lastReportTime = DateUtil.stringToDate(jobObj.getString("last_report_time"),dateFormat);
            java.util.Date lastUpdateTime= DateUtil.stringToDate(jobObj.getString("last_update_time"),dateFormat);
            java.util.Date createTime = DateUtil.stringToDate(jobObj.getString("create_time"),dateFormat);
            java.util.Date planStartTime = DateUtil.stringToDate(jobObj.getString("plan_start_time"),dateFormat);
            java.util.Date planEndTime = DateUtil.stringToDate(jobObj.getString("plan_end_time"),dateFormat);
            java.util.Date startTime = DateUtil.stringToDate(jobObj.getString("start_time"),dateFormat);
            java.util.Date endTime = DateUtil.stringToDate(jobObj.getString("end_time"),dateFormat);
            String endReason = jobObj.getString("end_reason");
            //新的job,需要记录
            Job newJob=parseJob(jobObj);
            if(newJob==null){
                logger.error("同步新的job 解析失败");
                return null ;
            }

            Job job=jobMapper.select(id);
            Task task=taskMapper.select(taskId);
            if(task==null){
                //不同步的情况，告诉task_manager,丢弃
                Map <String,Object> confirmJob=new HashMap<>();
                confirmJob.put("id",id);
                confirmJob.put("report_time",syncTime);
                logger.error("同步新的job ，task 不存在 ："+taskId);
                return confirmJob;
            }
            String name=buildJobName(task.getName(),newJob.getCreateTime());
            newJob.setName(name);
            if(job==null){
                newJob.setSiteId(task.getSiteId());
                newJob.setRobotId(task.getRobotId());
                int ret=jobMapper.insert(newJob);
                if(ret==1){
                    logger.info("同步新的job到服务端：成功");
                }else{
                    logger.error("同步新的job到服务端：写入数据失败");
                }
                DataCache.reload();
            }else{
                jobMapper.update(newJob);
                MemRobot memRobot=MemUtil.queryRobotById(task.getRobotId());
                if(memRobot!=null){
                    RobotTaskStatus robotTaskStatus=memRobot.getRobotTaskStatus();
                    if(robotTaskStatus!=null){
                        if(robotTaskStatus.getJob().getUid().equals(newJob.getUid())){
                            robotTaskStatus.setPath(allPathSection);
                            saveJobPath(newJob.getUid(),allPathSection);
                        }
                    }
                }
            }

            JSONArray ptzSets=jobObj.getJSONArray("ptz_sets");
            if(ptzSets!=null){
                //返回列表中为未同步的，循环更新到数据库
                for(int i=0;i<ptzSets.size();i++){
                    JSONObject ptzsetObj = ptzSets.getJSONObject(i);
                    Result ret=handlePtzSet(id,ptzsetObj);
                    if(ret.getCode()==1){
                        Map<String,String> confirmPtz=new HashMap<>();
                        confirmPtz.put("id",(String)ret.getData());
                        String ptzReportTime=ptzsetObj.getString("report_time");
                        confirmPtz.put("report_time",ptzReportTime);
                        ptzConfirmList.add(confirmPtz);
                    }
                }
            }
            Map <String,Object> confirmJob=new HashMap<>();
            confirmJob.put("id",id);
            confirmJob.put("report_time",syncTime);
            confirmJob.put("ptz_sets",ptzConfirmList);
            return confirmJob;
        }
        return null;
    }

    /**
     * 查询待上报的task id数组
     * @return
     */
    private Result queryTasks(Channel ch){
        String type="query_tasks";
        Result  result= GeneralService.call(ch,MsgNames.node_task_manage,MsgNames.service_result_query,type,"{}",5);
        if(result.getCode()!=1){
            return new Result(-1,"查询接口query_tasks失败:"+result.getMsg(),null);
        }
        GeneralServiceAckMsg ackMsg=(GeneralServiceAckMsg)result.getData();
        if(!ackMsg.getRet_code().equals("true")){
            return new Result(-1,"查询接口query_tasks失败"+ackMsg.getRet_msg(),null);
        }
        String retMsgStr=ackMsg.getData();
        return new Result(1,"",retMsgStr);
    }

    /**
     * 查询待上报的task
     * @return
     */
    private Result queryTask(Channel ch,String taskId){
        String type="query_task";
        Map req=new HashMap<String,String>();
        req.put("id",taskId);
        String data=JSONObject.toJSONString(req);
        Result  result= GeneralService.call(ch,MsgNames.node_task_manage,MsgNames.service_result_query,type,data,5);
        if(result.getCode()!=1){
            return new Result(-1,"查询接口query_task失败:"+result.getMsg(),null);
        }
        GeneralServiceAckMsg ackMsg=(GeneralServiceAckMsg)result.getData();
        if(!ackMsg.getRet_code().equals("true")){
            return new Result(-1,"查询接口query_task失败"+ackMsg.getRet_msg(),null);
        }
        String retMsgStr=ackMsg.getData();
        //String retMsgStr="{\"report_time\": '2019-06-13 12:00:01',\"id\":1 ,\"status\": 0}";
        return new Result(1,"",retMsgStr);
    }


    /**
     * 查询待上报的job id数组
     * @return
     */
    private Result queryJobs(Channel ch){
        String type="query_jobs";
        Result  result= GeneralService.call(ch,MsgNames.node_task_manage,MsgNames.service_result_query,type,"{}",5);
        if(result.getCode()!=1){
            return new Result(-1,"查询接口query_jobs失败:"+result.getMsg(),null);
        }
        GeneralServiceAckMsg ackMsg=(GeneralServiceAckMsg)result.getData();
        if(!ackMsg.getRet_code().equals("true")){
            return new Result(-1,"查询接口query_jobs失败"+ackMsg.getRet_msg(),null);
        }
        String retMsgStr=ackMsg.getData();
        return new Result(1,"",retMsgStr);
    }

    /**
     * 查询待上报的job
     * @return
     */
    private Result queryJob(Channel ch,String taskId){
        String type="query_job";
        Map req=new HashMap<String,String>();
        req.put("id",taskId);
        String data=JSONObject.toJSONString(req);
        Result  result= GeneralService.call(ch,MsgNames.node_task_manage,MsgNames.service_result_query,type,data,5);
        if(result.getCode()!=1){
            return new Result(-1,"查询接口query_job失败:"+result.getMsg(),null);
        }
        GeneralServiceAckMsg ackMsg=(GeneralServiceAckMsg)result.getData();
        if(!ackMsg.getRet_code().equals("true")){
            return new Result(-1,"查询接口query_job失败:"+ackMsg.getRet_msg(),null);
        }
        String retMsgStr=ackMsg.getData();
        //String retMsgStr="{\"report_time\": '2019-06-13 12:00:01',\"id\":1 ,\"status\": 0}";
        return new Result(1,"",retMsgStr);
    }


    private Result handlePtzSet( String jobId,JSONObject ptzsetObj ){
        logger.debug("ptzsetObj="+ptzsetObj);
        String id = ptzsetObj.getString("id");
        int status = ptzsetObj.getIntValue("status");
        String failedReason = ptzsetObj.getString("failed_reason");
        java.util.Date startTime = DateUtil.stringToDate(ptzsetObj.getString("start_time"),dateFormat);
        java.util.Date endTime = DateUtil.stringToDate(ptzsetObj.getString("end_time"),dateFormat);
        java.util.Date lastReportTime = DateUtil.stringToDate(ptzsetObj.getString("last_report_time"),dateFormat);
        java.util.Date lastUpdateTime = DateUtil.stringToDate(ptzsetObj.getString("last_update_time"),dateFormat);
        if(status==0 ){
            //还未结束
            return new Result(-1,"还未到需要同步的时间",id);
        }
        PtzSet ptzSet=ptzSetMapper.select(id);
        logger.info("do service end:"+ptzSet.toShortString());
        Job job=jobMapper.select(jobId);
        if(ptzSet==null || job==null){
            //发送已经同步过
            return new Result(1,"任务或者ptzSet不存在，不需要了",id);
        }
        JSONObject resultObj = ptzsetObj.getJSONObject("result");
        String siteId=ptzSet.getSiteId();
        //应该只有一条记录
        TaskLog taskLog= taskLogMapper.selectByJobPtz(jobId,id);
        if(taskLog==null){
            //新建tasklog记录
            taskLog=new TaskLog();
            taskLog.setTaskId(job.getTaskId());
            taskLog.setJobId(jobId);
            taskLog.setPtzSetId(ptzSet.getUid());
            taskLog.setRobotId(job.getRobotId());
            taskLog.setSiteId(siteId);
            taskLog.setBeginTime(startTime);
            taskLog.setFinishTime(endTime);
            if(status==1){
                taskLog.setStatus(1);
                taskLog.setMemo("找表成功");
                if(resultObj==null){
                    logger.error("找表成功,但返回result为空"+ptzSet.toShortString());
                    taskLog.setStatus(-1);
                    taskLog.setMemo("找表失败");
                }
            }else if(status==2){//找表失败
                taskLog.setStatus(-1);
                taskLog.setMemo("找表失败");
            }
            taskLog.setMemo(failedReason);
            taskLogMapper.insert(taskLog);
            //找表成功，发送读表
            if(status==1 && resultObj!=null){
                //同步文件
                logger.info("找表成功:"+ptzSet.toShortString());
                FindScaleResult findScaleResult=new FindScaleResult();
                findScaleResult.setTaskLogId(taskLog.getUid());
                findScaleResult.setStatus(0);
                findScaleResult.setFindResult(resultObj.toJSONString());
                logger.info("write find scale result to db: ",resultObj.toJSONString());
                findScaleResult.setFindScaleTime(new java.util.Date());
                //findScaleResultMapper.insert(findScaleResult);
                //todo wait 驱鸟结果
            }
            queryTaskLog(taskLog.getRobotId(),taskLog.getJobId());
            //同步成功
            return new Result(1,"success",id);
        }
        return new Result(1,"已经同步过，不需要再同步",id);
    }


    /**
     * 发送确认消息
     * @return
     */
    private Result confirm(Channel ch,Map map){
        String type="report_confirm";
        String data=JSONObject.toJSONString(map);
        logger.debug("**********confirm "+data);
        Result  result= GeneralService.call(ch,MsgNames.node_task_manage,MsgNames.service_result_query,type,data,5);
        if(result.getCode()!=1){
            return new Result(-1,"查询接口confirm失败:"+result.getMsg(),null);
        }
        GeneralServiceAckMsg ackMsg=(GeneralServiceAckMsg)result.getData();
        if(!ackMsg.getRet_code().equals("true")){
            return new Result(-1,"查询接口confirm失败"+ackMsg.getRet_msg(),null);
        }
        return new Result(1,"",null);
    }

    private GeneralTopicMsg toGeneralTopicMsg(JSONObject jsonObject) {
        GeneralTopicMsg msg = new GeneralTopicMsg();
        msg.setHeader(jsonObject.getJSONObject("header"));
        msg.setSender(jsonObject.getString("sender"));
        msg.setReceiver(jsonObject.getString("receiver"));
        msg.setTrans_id(jsonObject.getIntValue("trans_id"));
        msg.setData(jsonObject.getString("data"));
        return msg;
    }



    private GeneralServiceAckMsg toResultQueryAck(JSONObject jsonObject) {
        GeneralServiceAckMsg msg = new GeneralServiceAckMsg();
        msg.setHeader(jsonObject.getJSONObject("header"));
        msg.setSender(jsonObject.getString("sender"));
        msg.setReceiver(jsonObject.getString("receiver"));
        msg.setTrans_id(jsonObject.getIntValue("trans_id"));
        msg.setRet_code(jsonObject.getString("ret_code"));
        msg.setRet_msg(jsonObject.getString("ret_msg"));
        msg.setData(jsonObject.getString("data"));
        return msg;
    }



    private  void updateRobotTaskStatus(GeneralTopicMsg msg){
        MemRobot memRobot = MemUtil.queryRobot(msg.getRobot_ip());
        if (memRobot == null) {
            return;
        }
        RobotTaskStatus robotTaskStatus=memRobot.getRobotTaskStatus();
        try {
            JSONObject jobj = JSONObject.parseObject(msg.getData());
            int taskMode=jobj.getIntValue("task_mode");
            int runStatus=jobj.getIntValue("run_status");
            //System.out.println(taskMode);
            int remoteStop=jobj.getIntValue("remote_stop");
            memRobot.setEmergency(taskMode);
            JSONObject robotJobObj=jobj.getJSONObject("robot_job");
            if(runStatus==1){
                if(robotJobObj==null){
                    memRobot.setStopInPlace(2);
                }else{
                    memRobot.setStopInPlace(1);
                }
            }else if(runStatus==2){
                memRobot.setStopInPlace(0);
                memRobot.setRobotTaskStatus(null);
                return;
            }else{
                memRobot.setStopInPlace(0);
            }
            if(robotJobObj==null){
                memRobot.setRobotTaskStatus(null);
                memRobot.setChargeFlowStatus(0);
                return;
            }
            //System.out.println("当前运行任务  :"+robotJobObj.toString());
            String jobId=robotJobObj.getString("id");
            Job newJob = parseJob(robotJobObj);
            if(jobId.startsWith("charge")) {
                newJob.setName("充电返航");
                newJob.setSiteId(memRobot.getSiteId());
                newJob.setRobotId(memRobot.getRobotId());
                robotTaskStatus = new RobotTaskStatus();
                robotTaskStatus.setJob(newJob);
                JSONArray pathMarksArray = robotJobObj.getJSONArray("path_marks");
                String[] pathMarks=null;
                if(pathMarksArray!=null){
                    pathMarks=new String[pathMarksArray.size()];
                    for(int i=0;i<pathMarksArray.size();i++){
                        pathMarks[i]=pathMarksArray.getString(i);
                    }
                }
                List<Map>  allPathSection=new ArrayList<>();
                List<String> markList=null;
                if(pathMarksArray!=null){
                    markList=pathMarksArray.toJavaList(String.class);
                }
                int pathIndex = robotJobObj.getIntValue("path_index");
                Map data=new HashMap();
                data.put("index",pathIndex);
                data.put("markList",markList);
                allPathSection.add(data);
                robotTaskStatus.setPath(allPathSection);
                memRobot.setRobotTaskStatus(robotTaskStatus);
                memRobot.setChargeFlowStatus(1);
                return;
            }else{
                memRobot.setChargeFlowStatus(0);
            }
            String taskId=robotJobObj.getString("task_id");
            if(jobId==null || "".equals(jobId) || taskId==null || "".equals(taskId)){
                return ;
            }
            if(robotTaskStatus==null || !jobId.equals(robotTaskStatus.getJob().getUid()) ){
                //新的job,需要记录
                Job job=jobMapper.select(jobId);
                Task task=taskMapper.select(taskId);
                if(task==null){
                    return ;
                }
                if(job==null){
                    /*String name=buildJobName(task.getName(),newJob.getCreateTime());
                    newJob.setName(name);
                    newJob.setSiteId(task.getSiteId());
                    newJob.setRobotId(task.getRobotId());
                    jobMapper.insert(newJob);
                    DataCache.reload();
                    job=newJob;*/
                    return;
                }
                robotTaskStatus=new RobotTaskStatus();
                robotTaskStatus.setJob(job);
                robotTaskStatus.setTask(task);
                List<TaskPtz> taskPtzList=taskPtzMapper.selectListByTask(task.getUid());
                int realCount=0;
                for(TaskPtz item:taskPtzList){
                    PtzSet ptzSet=DataCache.findPtzSetByUid(item.getSiteId(),item.getPtzSetId());
                    if(ptzSet!=null && ptzSet.getPtzType()==4){
                        realCount++;
                    }
                }
                robotTaskStatus.setTotal(realCount);
                //在首次显示任务时,查询出结果日志
                memRobot.setRobotTaskStatus(robotTaskStatus);
                queryTaskLog(memRobot.getRobotId(),job.getUid());
                memRobot.pushTaskLogAllFinised();
                return ;
            }
            //如果不是新的，只更新状态
            int jobStatus=robotJobObj.getIntValue("status");
            //logger.info("jobId= "+jobId + "jobStatus= "+jobStatus);
            jobMapper.updateStatus(jobId,jobStatus);

            robotTaskStatus.getJob().setStatus(jobStatus);
            memRobot.pushTaskLogAllFinised();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("解析任务状态task_status异常",e.toString());
        }
    }

    private String buildJobName(String taskName,Date date){
        String name=taskName;
        // XXX任务20190705-1
        if(name.indexOf("-")>0){
            name=name.substring(0,name.indexOf("-"));
        }
        name=StringUtil.trimSuffixDate(name);
        return name+DateUtil.dateToString(date,"yyyyMMdd");
    }

    private Job parseJob(JSONObject jobObj){
        try{
            String id = jobObj.getString("id");
            int type = jobObj.getIntValue("type");
            String taskId = jobObj.getString("task_id");
            String taskType = jobObj.getString("task_type");
            int taskStatus = jobObj.getIntValue("task_status");
            int status = jobObj.getIntValue("status");
            int priority = jobObj.getIntValue("priority");
            java.util.Date createTime = DateUtil.stringToDate(jobObj.getString("create_time"),dateFormat);
            java.util.Date planStartTime = DateUtil.stringToDate(jobObj.getString("plan_start_time"),dateFormat);
            if(planStartTime==null){
                planStartTime=createTime;
            }
            java.util.Date planEndTime = DateUtil.stringToDate(jobObj.getString("plan_end_time"),dateFormat);
            java.util.Date startTime = DateUtil.stringToDate(jobObj.getString("start_time"),dateFormat);
            java.util.Date endTime = DateUtil.stringToDate(jobObj.getString("end_time"),dateFormat);
            String endReason = jobObj.getString("end_reason");
            Job job = new Job();
            job.setUid(id);
            job.setTaskId(taskId);
            job.setCreateTime(createTime);
            job.setPlanStartTime(planStartTime);
            job.setPlanEndTime(planEndTime);
            job.setRealStartTime(startTime);
            job.setRealEndTime(endTime);
            job.setEndReason(endReason);
            //user_id
            //priority
            job.setStatus(status);
            return job;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private  void queryTaskLog(TaskFinishedEvent event){
        queryTaskLog(event.getRobotId(),event.getJobId());
    }
    private void queryTaskLog(String robotId,String jobId){
        MemRobot memRobot = MemUtil.queryRobotById(robotId);
        if (memRobot == null) {
            return;
        }
        RobotTaskStatus robotTaskStatus=memRobot.getRobotTaskStatus();
        if(robotTaskStatus!=null){
            Map<String,String> params=new HashMap();
            params.put("selectedJobId",jobId);
            PageInfo<Map> list = taskLogService.selectTaskLogList(params,0,1000);
            robotTaskStatus.setTaskLogList(list);
        }
        //通知前端读取列表
        memRobot.pushTaskLogAllFinised();
    }


    private void saveJobPath(String jobId,List<Map> allPathSection){

        List<JobPathSection> list=jobPathSectionMapper.select(jobId);
        if(list!=null && list.size()>0){
            for(JobPathSection section:list){
                jobPathSectionMarkMapper.delete(section.getUid());
            }
            jobPathSectionMapper.delete(jobId);
        }

        if(allPathSection!=null && allPathSection.size()>0){
            for(int i=0;i<allPathSection.size();i++){
                Map sectionPath =allPathSection.get(i);
                int index = (Integer) sectionPath.get("index");
                List<String> markList = (List)sectionPath.get("markList");
                List<String>  mustMarkList = (List)sectionPath.get("mustMarkList");
                if(markList!=null){
                    List<Map> marks=new ArrayList<>();
                    for( int j=0;j<=index;j++) {
                        String markId=markList.get(j);
                        int must=0;
                        if(mustMarkList!=null){
                            for( int k=0;k<mustMarkList.size();k++) {
                                if(markId.equals(mustMarkList.get(k) ) ){
                                    must=1;
                                    break;
                                }
                            }
                        }
                        Map data=new HashMap();
                        data.put("markId",markId);
                        data.put("must",must);
                        marks.add(data);
                    }
                    JobPathSection jobPathSection=new JobPathSection();
                    jobPathSection.setUid(UUIDUtil.getUUID());
                    jobPathSection.setJobId(jobId);
                    jobPathSection.setSectionOrder(i);
                    jobPathSectionMapper.insert(jobPathSection);
                    jobPathSectionMarkMapper.insertBatch(jobPathSection.getUid(),marks);
                }
            }
        }
    }


/*
    private void saveJobPath(String jobId,String[] pathMarks,int finishedPathIndex,String mustPathMarks,List<List<Map>> hisPath){
        int i=0;
        List<JobPathSection> list=jobPathSectionMapper.select(jobId);
        if(list!=null && list.size()>0){
            for(JobPathSection section:list){
                jobPathSectionMarkMapper.delete(section.getUid());
            }
            jobPathSectionMapper.delete(jobId);
        }

        if(hisPath!=null && hisPath.size()>0){
            for(;i<hisPath.size();i++){
                List<Map> markList =hisPath.get(i);
                JobPathSection jobPathSection=new JobPathSection();
                jobPathSection.setUid(UUIDUtil.getUUID());
                jobPathSection.setJobId(jobId);
                jobPathSection.setSectionOrder(i);
                jobPathSectionMapper.insert(jobPathSection);
                jobPathSectionMarkMapper.insertBatch(jobPathSection.getUid(),markList);
            }
        }
        if(pathMarks!=null && pathMarks.length>0){
            JobPathSection jobPathSection=new JobPathSection();
            jobPathSection.setUid(UUIDUtil.getUUID());
            jobPathSection.setJobId(jobId);
            jobPathSection.setSectionOrder(i+1);
            jobPathSectionMapper.insert(jobPathSection);
            List<Map> markIdList=new ArrayList<>();
            for(int j=0;j<=finishedPathIndex && j<pathMarks.length;j++){
                markIdList.add(pathMarks[j]);
            }
            jobPathSectionMarkMapper.insertBatch(jobPathSection.getUid(),markList);
        }

    }*/
}

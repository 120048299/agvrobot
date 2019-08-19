package com.wootion.controller;

import com.github.pagehelper.PageInfo;
import com.wootion.agvrobot.session.SessionRobot;
import com.wootion.agvrobot.utils.DateUtil;
import com.wootion.agvrobot.utils.FileUtil;
import com.wootion.agvrobot.utils.PictureUtil;
import com.wootion.commons.*;
import com.wootion.dao.IDao;
import com.wootion.mapper.*;
import com.wootion.model.*;
import com.wootion.service.*;
import com.wootion.utiles.DataCache;
import com.wootion.utiles.ResultUtil;
import com.wootion.utiles.SessionManager;
import com.wootion.utiles.poi.ExportSvc;
import com.wootion.utiles.poi.TblCell;
import com.wootion.utiles.poi.XlsxExportSvcImpl;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

@RestController
@RequestMapping("/req_svr/tasklog")
public class TaskLogController {

    //编辑用户登录名
    private static final String editUser = "2";

    @Autowired
    private IDao iDao;
    @Autowired
    ITaskLogService taskLogService;
    @Autowired
    ITaskService taskService;
    @Autowired
    IPtzsetService ptzsetService;
    @Autowired
    UserService userService;
    @Autowired
    IMapService mapService;
    @Autowired
    private SiteMapper siteMapper;
    @Autowired
    private PtzSetMapper ptzSetMapper;
    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    TaskLogMapper taskLogMapper;
    Logger logger = LoggerFactory.getLogger(TaskLogController.class);

    @Autowired
    JobMapper jobMapper;


    /**
     * 结果浏览页面  查询任务结果 ,根据页码和每页条数查询。（按巡检点查询，时间yyyy.MM.dd hh:mm:ss）
     * fromDate
     * toDate
     * ptzSetId
     * pageNum
     * pageSize
     */
    @ResponseBody
    @PostMapping("getTaskLogList")
    public PageInfo getTaskLogList(@RequestBody Map params,HttpServletRequest request){
        String siteId=getRequestSiteId(request);
        String robotId = getRobotId(request);
        params.put("siteId",siteId);
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
        if(params.get("fromDate")!=null && "".equals(params.get("fromDate")) ){
            params.remove("fromDate");
        }
        if(params.get("toDate")!=null && "".equals(params.get("toDate")) ){
            params.remove("toDate");
        }

        /*PageInfo<Map> pageInfo = taskLogService.selectTaskLogList(params,pageNum, pageSize);
         List<Map> list = pageInfo.getList();
        for(Map map:list){
           Job job =(Job) map.get("job");
            map.put("taskName", job.getName());
            PtzSet ptzSet=(PtzSet) map.get("ptzSet");
            map.put("description", ptzSet.getDescription());
            map.put("planName", job.getName());
            RegzObject regzObject = (RegzObject)map.get("regzObject");
            if (regzObject!=null) {
                map.put("regzObjectName", "[" + regzObject.getUid() + "]" + regzObject.getName());
            }
            RegzSpot regzSpot=(RegzSpot)map.get("regzSpot");
            if(regzSpot!=null){
                map.put("opsTypeName", OPS_TYPE.fromInt(regzSpot.getOpsType()).toStrValue());
                map.put("saveTypeName", SAVE_TYPE.fromInt(regzSpot.getSaveType()).toStrValue());
                map.put("meterTypeName", METER_TYPE.fromInt(regzSpot.getMeterType()).toStrValue());
                map.put("spotName",regzSpot.getSpotName());
                map.put("opsType",regzSpot.getOpsType());
            }
            int execStatus = (Integer)map.get("exec_status");
            map.put("execStatusName", TASK_EXEC_STATUS.fromInt(execStatus).toStrValue());
            int auditStatus = (Integer)map.get("audit_status");
            if(auditStatus==0){
                map.put("auditStatusStr", "待审核");
            }else{
                map.put("auditStatusStr", "已审核");
            }

            String resultStr=(String)map.get("resultStr");
            String alarmLevelName=(String)map.get("alarmLevelName");
            map.put("resultAlarmInfo",resultStr+alarmLevelName);
        }

        return pageInfo;
        */
        return null;
    }



    @ResponseBody
    @GetMapping("getTaskLogInfo/{taskLogId}")
    public Map getTaskLogInfo(@PathVariable String taskLogId){
        Map resultMap=new HashMap();
        /*TaskLog taskLog= (TaskLog)taskLogService.getTaskLog(taskLogId);
        resultMap.put("taskLog",taskLog);
        List<TaskResult> resultList = taskLogService.getTaskResultByLogId(taskLogId);
        resultMap.put("resultList",resultList);
        logger.debug("taskLogId=",taskLogId);

        AlarmLog alarmLog = alarmLogMapper.getAlarmLogByLogId(taskLogId);
        resultMap.put("alarmLog",alarmLog);
        Job job =DataCache.findJob(taskLog.getSiteId(),taskLog.getJobId());
        resultMap.put("taskPlan", job);
        PtzSet ptzSet=DataCache.findPtzSetByUid(taskLog.getSiteId(),taskLog.getPtzSetId());
        resultMap.put("ptzSet",ptzSet);
        List<PtzSetField> ptzSetFields= ptzSet.getFieldList();
        resultMap.put("ptzSetFields",ptzSetFields);

        RegzSpot regzSpot= DataCache.findRegzSpot(ptzSet.getRegzSpotId());
        resultMap.put("regzSpot",regzSpot);

        RegzObject regzObject = DataCache.findRegzObject(ptzSet.getRegzObjectId());
        resultMap.put("regzObject",regzObject);
        List<RegzObjectField> regzObjectFields=regzObject.getFieldList();
        resultMap.put("regzObjectFields",regzObjectFields);

        //阈值信息
        *//*Map params=new HashMap();
        params.put("ptzSetId",ptzSet.getUid());
        List<AlarmCode> alarmCodes=alarmService.getAlarmCodes(params);
        resultMap.put("alarmCodes",alarmCodes);*//*

        AlarmInfo alarmCodes = alarmService.getAlarmInfo(taskLog.getPtzSetId());
        resultMap.put("alarmCodes",alarmCodes);

        Map<String, TaskResult> taskResultMap = TaskLogServiceImpl.transTaskResultListToMap(resultList);
        Map<String, PtzSetField> ptzSetFieldMap = TaskLogServiceImpl.transPtzSetFieldListToMap(ptzSetFields);
        String resultStr=TaskLogServiceImpl.buildResultStr(taskLog.getForeignFile(), taskLog.getWeatherTemp(), taskResultMap,ptzSetFieldMap,regzObjectFields,regzObject);
        resultMap.put("resultStr",resultStr);
        List<Map> fieldResultList=TaskLogServiceImpl.buildFieldResultList(taskResultMap,ptzSetFieldMap,regzObjectFields);
        resultMap.put("fieldResultList",fieldResultList);

        // 增加预置标记图片地址
        String basePath=ptzSet.getImageProcessPath().substring(ptzSet.getImageProcessPath().indexOf("preset"));
        resultMap.put("presetImgFile", File.separator + basePath + File.separator + "zoom0_mark.jpg");
        resultMap.put("presetInfraredFile", File.separator + basePath + File.separator + "zoom0_thermal_mark.jpg");
*/
        return resultMap;
    }

    /**
     * 从请求session中读取siteId
     * @param request
     * @return
     */
    private String getRequestSiteId(HttpServletRequest request){
        String token=request.getHeader(Constans.TOKEN);
        Site site = (Site) SessionManager.getSessionEntity(token,Constans.SESSION_SITE);
        if(site==null){
            return null;
        }
        return site.getUid();
    }


    /**
     * 巡检报告生成 ,页面查看一个任务计划的报告
     *
     * @param params taskPlanId
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("viewTaskPlanReport")
    public Result viewTaskPlanReport(@RequestBody Map params,HttpServletRequest request){
        String siteId=getRequestSiteId(request);
        params.put("siteId",siteId);
        String taskPlanId=(String)params.get("taskPlanId");
        if(taskPlanId==null || "".equals(taskPlanId)){
            logger.error("查看任务报告时，接收到参数taskPlanId 为空");
            return ResultUtil.failed("查看巡检报告失败");
        }
        Map reportInfo = this.getTaskPlanReportInfo(siteId,taskPlanId);
        if(reportInfo==null){
            return ResultUtil.failed("读取巡检报告失败,任务还没有开始执行。");
        }
        return ResultUtil.build(0,"导出成功",reportInfo);
    }


    /**
     * 巡检报告生成 ,输出一个任务计划的报告
     *
     * @param params taskPlanId
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("exportTaskPlanReport")
    public Result exportTaskPlanReport(@RequestBody Map params,HttpServletRequest request){
        String siteId=getRequestSiteId(request);
        params.put("siteId",siteId);
        String taskPlanId=(String)params.get("taskPlanId");
        if(taskPlanId==null || "".equals(taskPlanId)){
            logger.error("导出任务报告时，接收到参数taskPlanId 为空");
            return ResultUtil.failed("导出巡检报告失败");
        }
        Map reportInfo = this.getTaskPlanReportInfo(siteId,taskPlanId);
        if(reportInfo==null){
            return ResultUtil.failed("导出巡检报告失败,任务还没有开始执行。");
        }

        String fileName = "巡检报告生成"+FileUtil.dateRandom()+".xlsx";
        int ret = this.generatePlanReportExcel(FileUtil.getBasePath()+"report"+File.separator+fileName,reportInfo);
        if(ret>=0){
            return ResultUtil.build(0,"导出成功",fileName);
        }else{
            return ResultUtil.failed();
        }
    }

    private Map getTaskPlanReportInfo(String siteId, String taskPlanId){
//        Job job = taskService.getTaskPlan(taskPlanId);
        Job job = jobMapper.select(taskPlanId);
        Site site = siteMapper.findByUid(siteId);

        Map taskPlanInfo = new HashMap();
        java.util.Date  startTime= job.getRealStartTime();
        if(startTime==null){
            return null;
        }
        String startTimeStr = DateUtil.dateToString(startTime,"yyyy-MM-dd HH:mm");
        java.util.Date  endTime= job.getRealEndTime();
        String endTimeStr="";
        if(endTime!=null){
            endTimeStr = DateUtil.dateToString(endTime,"yyyy-MM-dd HH:mm");
        }
        if(endTime==null){
            endTime= new java.util.Date();
        }
        String timeSpan = DateUtil.timeSpan(startTime,endTime);
        String taskTime=String.format("开始时间: %s ,结束时间: %s,总时长: %s",startTimeStr,endTimeStr,timeSpan);
        taskPlanInfo.put("taskTime",taskTime);

        String taskStatusStr = JOB_STATUS.fromInt(job.getStatus()).toStrValue();
        taskPlanInfo.put("taskStatusStr",taskStatusStr);


        List<Map> list = taskLogService.selectTaskLogOfPlan(taskPlanId);

        if(job.getStatus()==JOB_STATUS.CANCELLED.getValue()){
            //终止点位为最后一个执行的 .
            if(list.size()>0){
                Map lastItem=list.get(list.size()-1);
                PtzSet ptzSet = (PtzSet)lastItem.get("ptzSet");
                Dev dev= DataCache.findDev(siteId,(String)lastItem.get("dev_id"));
                String lastName= dev.getName()+" "+ptzSet.getDescription();
                taskPlanInfo.put("taskStatusStr",taskStatusStr+",终止点位为"+lastName);
            }
        }

        //added by btrmg for paint runMark from RobotEvent 2018.11.09
        List<Map>  roboteventlist = null;//robotEventLogService.selectRobotEventLog(taskPlanId);
        //select * from robot_event_log where task_plan_id=#{taskPlanId} order by log_time

        //added end
        //拆分未4个list:失败,告警，识别异常，正常
        List<Map> listFailed = new ArrayList<>();
        List<Map> listAlarm = new ArrayList<>();
        List<Map> listAbnormal = new ArrayList<>();
        List<Map> listNormal = new ArrayList<>();

        int total=0;

        if(list!=null){
            total=list.size();
            for(Map item:list) {
                //识别类型  点位名称 识别结果 告警等级 识别时间
                RegzSpot regzSpot = (RegzSpot)item.get("regzSpot");
                Dev dev= DataCache.findDev(siteId,(String)item.get("dev_id"));
                String opsTypeName = OPS_TYPE.fromInt(regzSpot.getOpsType()).toStrValue();
                item.put("ops_type_name",opsTypeName);
                item.put("regz_spot_name",dev.getName()+" "+regzSpot.getSpotName());

                item.put("devName",dev.getName());
                PtzSet ptzSet = (PtzSet)item.get("ptzSet");
                item.put("description",dev.getName()+" "+ptzSet.getDescription());
                Area area = DataCache.findArea(siteId,ptzSet.getAreaId());
                item.put("areaName",area.getName());

                //执行失败的信息
                Integer execFailReason=(Integer)item.get("status");
                if(execFailReason!=2){
                    java.util.Date date=(java.util.Date)item.get("begin_time");
                    String tempTimeStr = DateUtil.dateToString(date,"yyyy-MM-dd HH:mm");
                    item.put("finish_time",tempTimeStr );
                    listFailed.add(item);
                    continue;
                }

                Integer auditStatus= (Integer)item.get("audit_status");
                Integer alarmLevel;
                if(auditStatus==1){
                    //审核后的告警
                    alarmLevel=(Integer)item.get("audit_alarm_level");
                }else{
                    //未审核的告警
                    alarmLevel=(Integer)item.get("alarm_level");
                }
                String alarmLevelStr="";
                if(alarmLevel!=null && alarmLevel>0){
                    alarmLevelStr = ALARM_LEVEL.fromInt(alarmLevel).toStrValue();
                }
                item.put("alarmLevelStr",alarmLevelStr);
                java.util.Date date=(java.util.Date)item.get("finish_time");
                String tempTimeStr = DateUtil.dateToString(date,"yyyy-MM-dd HH:mm");
                item.put("finish_time",tempTimeStr );

                if(alarmLevel!=null && alarmLevel>0){
                    listAlarm.add(item);
                }
                Integer abnormal=(Integer)item.get("audit_regz_abnormal");
                if(abnormal==1){
                    listAbnormal.add(item);
                }else{
                    listNormal.add(item);
                }

                //以第一条的环境为任务环境
                if(taskPlanInfo.get("envStr")==null){
                    Float weather_temp=(Float)item.get("weather_temp");
                    String weatherTemp = "";
                    if(weather_temp!=null){
                        weatherTemp = String.format("%3.2f",weather_temp);
                    }
                    Float weather_hum=(Float)item.get("weather_hum");
                    String weatherHum = "";
                    if(weather_hum!=null){
                        weatherHum  = String.format("%3.2f",weather_hum);
                    }
                    Float weather_wind_speed=(Float)item.get("weather_wind_speed");
                    String windSpeed = "";
                    if(weather_wind_speed!=null){
                        windSpeed = String.format("%3.2f",weather_wind_speed);
                    }
                    String envStr="温度: "+ weatherTemp +"℃，湿度: "+weatherHum+"%RH，风速: "+windSpeed+"m/s";
                    taskPlanInfo.put("envStr",envStr);
                }
            }
        }

        //重新排序告警 本次新增的在前，以前存在的在后，其次按时间先后排序
        Collections.sort(listAlarm, new Comparator<Map>() {
            public int compare(Map item1, Map item2) {
                int diff = (Integer) item1.get("is_new_alarm") - (Integer) item2.get("is_new_alarm");
                if (diff > 0) {
                    return -1;
                } else if (diff < 0) {
                    return 1;
                } else {
                    Date date1=DateUtil.stringToDate((String)item1.get("begin_time"),"yyyy-MM-dd HH:mm");
                    Date date2=DateUtil.stringToDate((String)item2.get("begin_time"),"yyyy-MM-dd HH:mm");
                    if(date1.after(date2)){
                        return 1;
                    }else{
                        return -1;
                    }
                }
            }
        });


        taskPlanInfo.put("total",total);
        String outFileName = "taskRoute"+ FileUtil.dateRandom()+".jpg";
        Map reportInfo=new HashMap();
        reportInfo.put("taskPlan", job);
        reportInfo.put("taskPlanInfo",taskPlanInfo);
        reportInfo.put("listFailed",listFailed);
        reportInfo.put("listAlarm",listAlarm);
        reportInfo.put("listAbnormal",listAbnormal);
        reportInfo.put("listNormal",listNormal);
        return reportInfo;
    }


    /**
     * 生成excel文件
     * @param fileName
     * @param reportInfo
     * @return
     */
    private  int generatePlanReportExcel(String fileName,Map reportInfo){
        Job job = (Job)reportInfo.get("taskPlan");
        Map taskPlanInfo  = (Map) reportInfo.get("taskPlanInfo");
        List<Map> listFailed = (List<Map>) reportInfo.get("listFailed");
        List<Map> listAlarm = (List<Map>) reportInfo.get("listAlarm");
        List<Map> listAbnormal = (List<Map>) reportInfo.get("listAbnormal");
        List<Map> listNormal = (List<Map>) reportInfo.get("listNormal");

        List [] heads = new List[5];
        List list1 = new ArrayList ();
        list1.add(new TblCell("11","任务名称", 1, 1));
        list1.add(new TblCell("12", job.getName(), 8, 1));
        heads[0]=list1;

        list1 = new ArrayList ();
        list1.add(new TblCell("11","任务时间", 1, 1));
        list1.add(new TblCell("11",(String)taskPlanInfo.get("taskTime"), 8, 1));
        heads[1]=list1;

        list1 = new ArrayList ();
        list1.add(new TblCell("11","任务状态", 1, 1));
        list1.add(new TblCell("12",(String)taskPlanInfo.get("taskStatusStr"), 8, 1));
        heads[2]=list1;

        list1 = new ArrayList ();
        list1.add(new TblCell("11","巡检点", 1, 1));
        String pointStr="本次任务共巡视"+ taskPlanInfo.get("total") +"点位，正常点位"+listNormal.size()+"个，告警点位"+
                listAlarm.size()+"个，识别异常点位"+listAbnormal.size()+"个";
        list1.add(new TblCell("12",pointStr, 8, 1));
        heads[3]=list1;

        list1 = new ArrayList ();
        list1.add(new TblCell("11","环境信息", 1, 1));
        list1.add(new TblCell("12",(String)taskPlanInfo.get("envStr"), 8, 1));
        heads[4]=list1;

        ExportSvc exportSvc = new XlsxExportSvcImpl(job.getName()+"巡检报告");
        File file = new File(fileName);
        exportSvc.writeTblHead(heads, 1);

        String rowData[]= {"告警点位"};
        int colSpan   []= {9};
        exportSvc.writeRow(5,rowData,colSpan,1);

        String titleAlarm[] = {"识别类型","点位名称","识别结果","告警等级","识别时间","见光图片","红外图片","音视频","异物"};
        int titleAlarmSpan[] = {1,1,1,1,1,1,1,1,1};
        exportSvc.writeRow(6,titleAlarm,titleAlarmSpan,1);

        int rowIndex=7;
        String resultData[]=new String[5];
        int resultSpan[] = {1,1,1,1,1};
        if(listAlarm!=null){
            for (Map item :listAlarm){
                resultData[0] = (String)item.get("ops_type_name");
                resultData[1] = (String)item.get("description");
                resultData[2] = (String)item.get("resultStr");
                resultData[3] = (String)item.get("alarmLevelStr");
                resultData[4] = (String)item.get("finish_time");
                XSSFRow row=exportSvc.writeRow(rowIndex,resultData,resultSpan,1,100);

                String imgFile=(String)item.get("img_file");
                if(imgFile!=null){
                    String tempFile=FileUtil.getBasePath()+"report"+File.separator+"temp.jpg";
                    int zoomResult=PictureUtil.zoomImage(FileUtil.getUserHome()+imgFile,tempFile,960,540);
                    if(zoomResult==0){
                        exportSvc.exportPic(tempFile,rowIndex,5,rowIndex+1,6,25);
                    }
                }
                imgFile=(String)item.get("infrared_file");
                if(imgFile!=null){
                    String tempFile=FileUtil.getBasePath()+"report"+File.separator+"temp.jpg";
                    int zoomResult=PictureUtil.zoomImage(FileUtil.getUserHome()+imgFile,tempFile,960,540);
                    if(zoomResult==0){
                        exportSvc.exportPic(tempFile,rowIndex,6,rowIndex+1,7,25);
                    }
                }
               /*
                String temp=(String)item.get("video_file");
                if(temp==null || "".equals(temp)){
                    temp=(String)item.get("audio_file");
                }
                resultData[7] = temp;*/
                imgFile=(String)item.get("foreign_file");
                if(imgFile!=null){
                    String tempFile=FileUtil.getBasePath()+"report"+File.separator+"temp.jpg";
                    int zoomResult=PictureUtil.zoomImage(FileUtil.getUserHome()+imgFile,tempFile,960,540);
                    if(zoomResult==0){
                        exportSvc.exportPic(tempFile,rowIndex,8,rowIndex+1,9,25);
                    }
                }
                rowIndex++;
            }
        }

        resultData=new String[5];
        rowData[0]= "异常点位";
        colSpan[0]= 9;
        exportSvc.writeRow(rowIndex,rowData,colSpan,1);
        rowIndex++;

        String titleAbnormal[] = {"识别类型","点位名称","识别结果","审核结果","识别时间","见光图片","红外图片","音视频","异物"};
        int titleAbnormalSpan[] = {1,1,1,1,1,1,1,1,1};
        exportSvc.writeRow(rowIndex,titleAbnormal,titleAbnormalSpan,1);
        rowIndex++;

        if(listAbnormal!=null){
            for (Map item :listAbnormal){
                resultData[0] = (String)item.get("ops_type_name");
                resultData[1] = (String)item.get("description");
                resultData[2] = (String)item.get("resultStr");
                resultData[3] = (String)item.get("auditResultStr");
                resultData[4] = (String)item.get("finish_time");
                exportSvc.writeRow(rowIndex,resultData,resultSpan,1,100);

                String imgFile=(String)item.get("img_file");
                if(imgFile!=null){
                    String tempFile=FileUtil.getBasePath()+"report"+File.separator+"temp.jpg";
                    int zoomResult=PictureUtil.zoomImage(FileUtil.getUserHome()+imgFile,tempFile,960,540);
                    if(zoomResult==0){
                        exportSvc.exportPic(tempFile,rowIndex,5,rowIndex+1,6,25);
                    }
                }
                imgFile=(String)item.get("infrared_file");
                if(imgFile!=null){
                    String tempFile=FileUtil.getBasePath()+"report"+File.separator+"temp.jpg";
                    int zoomResult=PictureUtil.zoomImage(FileUtil.getUserHome()+imgFile,tempFile,960,540);
                    if(zoomResult==0){
                        exportSvc.exportPic(tempFile,rowIndex,6,rowIndex+1,7,25);
                    }
                }

               /* String temp=(String)item.get("video_file");
                if(temp==null || "".equals(temp)){
                    temp=(String)item.get("audio_file");
                }
                resultData[7] = temp;
                */
                imgFile=(String)item.get("foreign_file");
                if(imgFile!=null){
                    String tempFile=FileUtil.getBasePath()+"report"+File.separator+"temp.jpg";
                    int zoomResult=PictureUtil.zoomImage(FileUtil.getUserHome()+imgFile,tempFile,960,540);
                    if(zoomResult==0){
                        exportSvc.exportPic(tempFile,rowIndex,8,rowIndex+1,9,25);
                    }
                }
                rowIndex++;
            }
        }


        rowData[0]= "正常点位";
        colSpan[0]= 9;
        exportSvc.writeRow(rowIndex,rowData,colSpan,1);
        rowIndex++;
        exportSvc.writeRow(rowIndex,titleAlarm,titleAlarmSpan,1);
        rowIndex++;
        if(listNormal!=null){
            for (Map item :listNormal){
                resultData[0] = (String)item.get("ops_type_name");
                resultData[1] = (String)item.get("description");
                resultData[2] = (String)item.get("resultStr");
                resultData[3] = (String)item.get("alarmLevelStr");
                resultData[4] = (String)item.get("finish_time");
                exportSvc.writeRow(rowIndex,resultData,resultSpan,1,100);
                String imgFile=(String)item.get("img_file");
                if(imgFile!=null){
                    String tempFile=FileUtil.getBasePath()+"report"+File.separator+"temp.jpg";
                    int zoomResult=PictureUtil.zoomImage(FileUtil.getUserHome()+imgFile,tempFile,960,540);
                    if(zoomResult==0){
                        exportSvc.exportPic(tempFile,rowIndex,5,rowIndex+1,6,25);
                    }
                }
                imgFile=(String)item.get("infrared_file");
                if(imgFile!=null){
                    String tempFile=FileUtil.getBasePath()+"report"+File.separator+"temp.jpg";
                    int zoomResult=PictureUtil.zoomImage(FileUtil.getUserHome()+imgFile,tempFile,960,540);
                    if(zoomResult==0){
                        exportSvc.exportPic(tempFile,rowIndex,6,rowIndex+1,7,25);
                    }
                }
               /* String temp=(String)item.get("video_file");
                if(temp==null || "".equals(temp)){
                    temp=(String)item.get("audio_file");
                }*/
                imgFile=(String)item.get("foreign_file");
                if(imgFile!=null){
                    String tempFile=FileUtil.getBasePath()+"report"+File.separator+"temp.jpg";
                    //int zoomResult=PictureUtil.zoomImage(FileUtil.PICCTURE_DIR+imgFile,tempFile,960,540);
                    //if(zoomResult==0){
                        exportSvc.exportPic(FileUtil.getUserHome()+imgFile,rowIndex,8,rowIndex+1,9,25);
                    //}
                }
                rowIndex++;
            }
        }

        rowData[0]= "失败点位";
        colSpan[0]= 4;
        exportSvc.writeRow(rowIndex,rowData,colSpan,1);
        rowIndex++;

        String titleFailed[] = {"识别类型","点位名称","识别时间","失败原因"};
        int titleFailedSpan[] = {1,1,1,1};
        exportSvc.writeRow(rowIndex,titleFailed,titleFailedSpan,1);
        rowIndex++;
        String failedData[]=new String[4];
        if(listFailed!=null){
            for (Map item :listFailed){
                failedData[0] = (String)item.get("ops_type_name");
                failedData[1] = (String)item.get("description");
                failedData[2] = (String)item.get("finish_time");
                failedData[3] = (String)item.get("exec_fail_memo");
                exportSvc.writeRow(rowIndex,failedData,titleFailedSpan,1,200);
                rowIndex++;
            }
        }

        exportSvc.setRegionStyle(0);

        rowData[0]= "巡视路线图";
        colSpan[0]= 9;
        exportSvc.writeRow(rowIndex,rowData,colSpan,1);
        rowIndex++;
        //插入路线图
        String pathImage=job.getPathImage();
        if(pathImage!=null && !"".equals(pathImage)){
            exportSvc.exportPic(pathImage,rowIndex,1,rowIndex+15,7);
        }
        exportSvc.writeFile(file);
        return 0;
    }



    private void formatReportData(String siteId, List<Map> list){
        for(Map map:list){
            Dev dev=DataCache.findDev(siteId,(String)map.get("dev_id"));
            Area area = DataCache.findArea(siteId,(String)map.get("area_id"));
            map.put("areaName",area.getName());
            map.put("devName",dev.getName());
            DevType devType=DataCache.findDevType(dev.getDevTypeId());
            map.put("devType",devType.getName());
            DevType smallDevType=DataCache.findDevType((String)map.get("smallDevTypeId"));
            map.put("smallDevType",smallDevType.getName());
            //spotName
            Integer ops_type = (Integer)map.get("ops_type");
            map.put("opsType", OPS_TYPE.fromInt(ops_type.intValue()).toStrValue());
            Integer meter_type = (Integer)map.get("meter_type");
            if(meter_type!=null){
                map.put("meterType", METER_TYPE.fromInt(meter_type.intValue()).toStrValue());
            }else{
                map.put("meterType","");
            }

            Integer alarmlevel = (Integer)map.get("alarm_level");
            if(alarmlevel!=null){
                map.put("alarmlevelName", ALARM_LEVEL.fromInt(alarmlevel.intValue()).toStrValue());

            }else{
                map.put("alarmlevelName", "");
            }
            Integer saveType = (Integer)map.get("saveType");
            if(saveType!=null){
                map.put("saveTypeName", SAVE_TYPE.fromInt(saveType.intValue()).toStrValue());
            }else{
                map.put("saveTypeName", "");
            }

            //当设备类型为辅助设施FZSS时,外观类型 其实就是设备小类型
            if(dev.getUid().equals("FZSS")){
                map.put("outLookType",smallDevType.getName());
            }else{
                map.put("outLookType","");
            }

            //阈值信息 汇总
            //todo wait
          /*  String alarmCodeInfo ="";
            Map params1=new HashMap();
            params1.put("regzSpotId",map.get("regz_spot_id"));
            List<AlarmCode> alarmCodeList = alarmService.getAlarmCodes(params1);
            if(alarmCodeList!=null && alarmCodeList.size()>0){
                for(AlarmCode alarmCode:alarmCodeList){
                    String alarmLevelName=ALARM_LEVEL.fromInt(alarmCode.getAlarmLevel()).toStrValue();
                    alarmCodeInfo += alarmLevelName +":" +alarmCode.getExpression()+";  ";
                }
            }
            map.put("alarmCodeInfo",alarmCodeInfo);*/

            //识别状态
            int abnormal=(Integer)map.get("audit_regz_abnormal");
            if(abnormal==1){
                map.put("auditAbnormal","异常");
            }else{
                map.put("auditAbnormal","正常");
            }
            //识别结果
            //审核结果
            //识别时间
            java.util.Date  time=(java.util.Date)map.get("begin_time");
            String timeStr = DateUtil.dateToString(time,"yyyy-MM-dd HH:mm");
            map.put("beginTime",timeStr);
            //告警等级
            if((Integer)map.get("audit_alarm_level")!=null){
                String auditAlarmLevel = ALARM_LEVEL.fromInt((Integer)map.get("audit_alarm_level")).toStrValue();
                map.put("auditAlarmLevel",auditAlarmLevel);
            }else {
//                map.put("auditAlarmLevel", "无数据");
                if((Integer)map.get("alarm_level")!=null){
                    Integer alarmLevel = ((Integer) map.get("alarm_level"));
                    map.put("auditAlarmLevel", ALARM_LEVEL.fromInt(alarmLevel).toStrValue());
                } else {
                    map.put("auditAlarmLevel", "正常");
                }
            }


            //四个文件

            //三个环境数据
            Double weather_temp=(Double)map.get("weather_temp");
            String weatherTemp = "";
            if(weather_temp!=null){
                weatherTemp = String.format("%3.2f",weather_temp);
            }
            map.put("weatherTemp",weatherTemp);

            Double weather_hum=(Double)map.get("weather_hum");
            String weatherHum = "";
            if(weather_hum!=null){
                weatherHum  = String.format("%3.2f",weather_hum);
            }
            map.put("weatherHum",weatherHum);

            Double weather_wind_speed=(Double)map.get("weather_wind_speed");
            String windSpeed = "";
            if(weather_wind_speed!=null){
                windSpeed = String.format("%3.2f",weather_wind_speed);
            }
            map.put("windSpeed",windSpeed);

        }

    }


 /*  wait todo confirm
    @ResponseBody
    @PostMapping(value = "/updateFortaskLog")
    public Result updateForTaskLog(@RequestBody Map condition) {
        String ids = (String) condition.get("uid");
        String reviewer = (String) condition.get("reviewer");
        String[] temp= ids.split(",");
        List<String> idList = new ArrayList<>();
        for (String id:temp){
            idList.add(id);
        }
        HashMap map = new HashMap();
        map.put("idList",idList);
        map.put("reviewer",reviewer);

        Boolean result = taskLogService.updateForTaskLog(map);
        if (result) {
            DataCache.reload();
            return ResultUtil.build(0,"更新数据成功",null);
        } else {
            return ResultUtil.build(1,"更新数据失败",null);
        }
    }
*/

    public static int getIndex(String[] arr, String value) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(value)) {
                return i;
            }
        }
        return -1;//如果未找到返回-1
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
}

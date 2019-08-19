package com.wootion.controller;

import com.wootion.agvrobot.session.SessionRobot;
import com.wootion.agvrobot.utils.CommonTree;
import com.wootion.agvrobot.utils.NumberUtil;
import com.wootion.commons.Constans;
import com.wootion.commons.Result;
import com.wootion.dao.IDao;
import com.wootion.dao.ITaskDao;
import com.wootion.mapper.*;
import com.wootion.model.*;
import com.wootion.robot.DataSynThread;
import com.wootion.robot.MemRobot;
import com.wootion.robot.MemUtil;
import com.wootion.robot.MemWeatherStatus;
import com.wootion.service.DevService;
import com.wootion.service.ITaskService;
import com.wootion.service.impl.TaskService;
import com.wootion.task.CameraController;
import com.wootion.task.TerraceController;
import com.wootion.task.event.SentDataSyncEvent;
import com.wootion.utiles.DataCache;
import com.wootion.utiles.ResultUtil;
import com.wootion.utiles.SessionManager;
import com.wootion.vo.RobotParamVO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.wootion.vo.DevTreeNode;

@Controller
@RequestMapping("/req_svr/dev")
public class DevController {

    /**
     * todo 区域 是放在设备表的  单独建立区域表
     *
     */

    Logger logger = LoggerFactory.getLogger(DevController.class);
    @Autowired
    private DevService devService;

    @Autowired
    private SiteMapper siteMapper;
    @Autowired
    private RobotMapper robotMapper;

    @Autowired
    private RobotParamMapper robotParamMapper;

    @Autowired
    private SysParamMapper sysParamMapper;

    @Autowired
    SysAlarmConfigMapper sysAlarmConfigMapper;
    @Autowired
    PtzSetMapper ptzSetMapper;
    @Autowired
    DevMapper devMapper;
    @Autowired
    AreaMapper areaMapper;
    @Autowired
    ITaskService taskService;

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
     * 查询所有站点
     */
    @ResponseBody
    @GetMapping("/getSiteList")
    public Result getSiteList(){
        List<Site> list = siteMapper.findAll();
        return ResultUtil.success(list);
    }

   /* *//**
     * 客户端选择站点。客户端大部分操作都与站点有关，除了：用户，人员，权限，设备类型等少数表外。
     * 这个可以坐在登录的同时
     * @param siteId
     * @param request
     * @return
     *//*
    @ResponseBody
    @GetMapping("/update/sessionSite/{siteId}")
    public Result updateSessionSite(@PathVariable String siteId, HttpServletRequest request){
        String token=request.getHeader(Constans.TOKEN);
        Dev dev = devService.getDev(siteId);
        if(dev==null){
            return ResultUtil.failed("站点不存在:"+siteId);
        }
        Site site=new Site(dev);
        SessionManager.addOrUpdateSessionEntity(token,Constans.SESSION_SITE,site);
        return ResultUtil.success(site);
    }*/


   /**
    * @Auther: zhangjiqiang
    * @Description:
    * @Date: Create in 下午3:12 18-4-23
    * @return
    */
    @ResponseBody
    @GetMapping("/robots")
    public Result getAllRobots(HttpServletRequest request) {
        String siteId=getRequestSiteId(request);
        List<Robot> list = robotMapper.findListBySiteId(siteId);
        return ResultUtil.success(list);
    }

    @ResponseBody
    @PostMapping("/getAllRobots")
    public List<Robot> getAllRobotsInfo(HttpServletRequest request) {
        List<Robot> list = robotMapper.findAll();
        for(Robot robot: list)
        {
            if(robot.getStatus() == 0) {
                robot.setStatusString("停用");
            }
            else{
                robot.setStatusString("启用");
            }

            if(robot.getThermalType() == 0){
                robot.setThermalTypeString("flir设备");
            }
            else{
                robot.setThermalTypeString("guide设备");
            }
            robot.setSiteName(siteMapper.getSiteNameById(robot.getSiteId()));
        }
        return list;
    }

    @ResponseBody
    @PostMapping("/getSiteInfo")
    public List<Site> getSiteInfo(HttpServletRequest request) {
        List<Site> list = siteMapper.findAll();
        return list;
    }

    @ResponseBody
    @PostMapping("/updateRobotSite")
    public Result updateRobotSite(@RequestBody Map params,HttpServletRequest request) {
        Robot robot = new Robot();
        robot.setUid((String) params.get("uid"));
        robot.setSiteId((String) params.get("siteId"));
        robotMapper.updateRobotSite(robot);
        return ResultUtil.build(0,"修改成功",null);
    }

    @ResponseBody
    @PostMapping("/updateRobotStatus")
    public Result updateRobotStatus(@RequestBody Map params,HttpServletRequest request) {
        Robot robot = new Robot();
        robot.setUid((String) params.get("uid"));
        int status = (int) params.get("status");
        robot.setRobotIp((String)params.get("robotIp"));
        robot.setStatus(status);
        robotMapper.updateRobotStatus(robot);
        // todo wait 设置机器人节点 task_manager 原地待命
        MemUtil.deleteMemRobotByIp(robot.getRobotIp());

        if(status == 1)//启用
        {
            robot=robotMapper.select(robot.getUid());
            Site site=siteMapper.findByUid(robot.getSiteId());
            MemUtil.syncMemRobot(robot,site);
        }
        //todo wait 5000
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        return ResultUtil.build(0,"修改成功",null);
    }

    private String findParam(List<RobotParam> robotParamList,String key){
        for(RobotParam item:robotParamList){
            if(item.getKey().equals(key)){
                return item.getValue();
            }
        }
        return null;
    }

    @ResponseBody
    @GetMapping("/getRobotParam")
    public Result getRobotParam(HttpServletRequest request) {
        RobotParamVO robotParamVO=new RobotParamVO();
        String robotId=getRobotId(request);
        robotParamVO.setRobotId(robotId);
        List<RobotParam> list = robotParamMapper.getRobotParam(robotId);
        if(list ==null){
            robotParamVO.setSpeed(0.8);
            robotParamVO.setTerraceX(0);
            robotParamVO.setTerraceY(0);
            robotParamVO.setTerraceDisX(0);
            robotParamVO.setTerraceDisY(0);
            robotParamVO.setControlMode(1);
            robotParamVO.setInfraredUsed(1);
            robotParamVO.setImageUsed(1);
            robotParamVO.setWiperUsed(0);
            robotParamVO.setAvoidanceUsed(0);
            robotParamVO.setLightingUsed(0);
            robotParamVO.setChargeRoomUsed(0);
            robotParamVO.setRobotStatusUsed(0);
            robotParamVO.setWheelDiameter(0);
            robotParamVO.setDisWheelAndCenter(0);

            robotParamVO.setBatteryMin(5);
            robotParamVO.setRadarDisAlarm(1);
            robotParamVO.setStopStyle(0);
            robotParamVO.setWarnStyle(0);

        }else{
            robotParamVO.setSpeed(Double.parseDouble(findParam(list,"speed")));
            robotParamVO.setTerraceX(Double.parseDouble(findParam(list,"terraceX")));
            robotParamVO.setTerraceY(Double.parseDouble(findParam(list,"terraceY")));
            robotParamVO.setTerraceDisX(Double.parseDouble(findParam(list,"terraceDisX")));
            robotParamVO.setTerraceDisY(Double.parseDouble(findParam(list,"terraceDisY")));
            robotParamVO.setControlMode(Integer.parseInt(findParam(list,"controlMode")));
            robotParamVO.setInfraredUsed(Integer.parseInt(findParam(list,"infraredUsed")));
            robotParamVO.setImageUsed(Integer.parseInt(findParam(list,"imageUsed")));
            robotParamVO.setWiperUsed(Integer.parseInt(findParam(list,"wiperUsed")));
            robotParamVO.setAvoidanceUsed(Integer.parseInt(findParam(list,"avoidanceUsed")));
            robotParamVO.setLightingUsed(Integer.parseInt(findParam(list,"lightingUsed")));
            robotParamVO.setChargeRoomUsed(Integer.parseInt(findParam(list,"chargeRoomUsed")));
            robotParamVO.setRobotStatusUsed(Integer.parseInt(findParam(list,"robotStatusUsed")));
            robotParamVO.setWheelDiameter(Double.parseDouble(findParam(list,"wheelDiameter")));
            robotParamVO.setDisWheelAndCenter(Double.parseDouble(findParam(list,"disWheelAndCenter")));

            robotParamVO.setBatteryMin(Double.parseDouble(sysParamMapper.findByUid("1001").getValue()));
            robotParamVO.setRadarDisAlarm(Double.parseDouble(sysParamMapper.findByUid("1031").getValue()));
            robotParamVO.setStopStyle(Integer.parseInt(sysParamMapper.findByUid("1029").getValue()));
            robotParamVO.setWarnStyle(Integer.parseInt(sysParamMapper.findByUid("1028").getValue()));
        }

        return ResultUtil.success(robotParamVO);
    }

    @ResponseBody
    @PostMapping("/saveRobotSysParam")
    public Result saveRobotSysParam (@RequestBody Map params, HttpServletRequest request){
        String batteryMin = String.valueOf(params.get("batteryMin"));
        String warnStyle =  String.valueOf(params.get("warnStyle"));
        String stopStyle =  String.valueOf(params.get("stopStyle"));
        String radarDisAlarm =  String.valueOf(params.get("radarDisAlarm"));
        sysParamMapper.updateByUid("1001",batteryMin);
        //同时修改告警条件
        SysAlarmConfig sysAlarmConfig=new SysAlarmConfig();
        sysAlarmConfig.setAlarmCode("0102");
        sysAlarmConfig.setAlarmExp("X<"+batteryMin);
        sysAlarmConfigMapper.updateAlarmExp(sysAlarmConfig);
        sysParamMapper.updateByUid("1028",warnStyle);
        sysParamMapper.updateByUid("1029",stopStyle);
        sysParamMapper.updateByUid("1031",radarDisAlarm);
        DataCache.reload();
        String robotId=this.getRobotId(request);
        MemRobot memRobot=MemUtil.queryRobotById(robotId);
        taskService.syncParamsData(memRobot);
        return ResultUtil.build(0,"修改成功",null);
    }

    /**
     * 只修改 。增加为初始化脚本的事情
     * @param vo
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("/saveRobotSelfParam")
    public Result saveRobotSelfParam (@RequestBody RobotParamVO vo,HttpServletRequest request){
        String robotId=getRobotId(request);

        robotParamMapper.updateRobotParam(robotId,"speed",String.valueOf(vo.getSpeed()));
        robotParamMapper.updateRobotParam(robotId,"wheelDiameter",String.valueOf(vo.getWheelDiameter()));
        robotParamMapper.updateRobotParam(robotId,"disWheelAndCenter",String.valueOf(vo.getDisWheelAndCenter()));
        robotParamMapper.updateRobotParam(robotId,"terraceX",String.valueOf(vo.getTerraceX()));
        robotParamMapper.updateRobotParam(robotId,"terraceY",String.valueOf(vo.getTerraceY()));
        robotParamMapper.updateRobotParam(robotId,"terraceDisX",String.valueOf(vo.getTerraceDisX()));
        robotParamMapper.updateRobotParam(robotId,"terraceDisY",String.valueOf(vo.getTerraceDisY()));
        robotParamMapper.updateRobotParam(robotId,"controlMode",String.valueOf(vo.getControlMode()));
        robotParamMapper.updateRobotParam(robotId,"infraredUsed",String.valueOf(vo.getInfraredUsed()));
        robotParamMapper.updateRobotParam(robotId,"imageUsed",String.valueOf(vo.getImageUsed()));
        robotParamMapper.updateRobotParam(robotId,"wiperUsed",String.valueOf(vo.getWiperUsed()));
        //robotParamMapper.updateRobotParam(robotId,"avoidanceUsed",String.valueOf(vo.getAvoidanceUsed()));
        robotParamMapper.updateRobotParam(robotId,"lightingUsed",String.valueOf(vo.getLightingUsed()));
        robotParamMapper.updateRobotParam(robotId,"chargeRoomUsed",String.valueOf(vo.getChargeRoomUsed()));
        robotParamMapper.updateRobotParam(robotId,"robotStatusUsed",String.valueOf(vo.getRobotStatusUsed()));

        MemRobot memRobot=MemUtil.queryRobotById(robotId);
        taskService.syncParamsData(memRobot);
        return ResultUtil.build(0,"修改成功",null);
        /*robotParam.setUid(UUIDUtil.getUUID());
        robotParamMapper.insert(robotParam);
        return ResultUtil.build(0,"保存成功",null);
        */


    }

    @ResponseBody
    @GetMapping("/queryRobotsInfo")
    public Result queryRobotsInfo(HttpServletRequest request) {
        String robotId=getRobotId(request);
        ReportRobotInfo reportRobotInfo = new ReportRobotInfo();
        MemRobot memRobot = MemUtil.queryRobotById(robotId);
        //以下赋值转换，如果没有的，目前采用初始化值进行
        reportRobotInfo.setRobotTemperature(memRobot.getRobotInfo().getTemperature()[0]);//机身温度
        TerraceController terraceController=new TerraceController();
        double terraceHorizontal= terraceController.getPanAngle(memRobot.getRobotIp());
        double terraceTilt= terraceController.getTiltAngle(memRobot.getRobotIp());
        reportRobotInfo.setTerraceHorizontal(terraceHorizontal);//云台水平位置
        reportRobotInfo.setTerraceVertical(terraceTilt);//云台垂直位置
        CameraController cameraController=new CameraController();
        double zoomLevel=cameraController.getZoomLevel(memRobot);
        reportRobotInfo.setOpticalZoom(zoomLevel);//相机倍数
        reportRobotInfo.setRobotSpeed(memRobot.getRobotInfo().getVelocity_x()); //机器人速度
        //reportRobotInfo.setWirelessTower((int)memRobot.getRobotInfo().getWifi_strength());//无线通信信号
        int bits[]= NumberUtil.intToBitArray(memRobot.getRobotInfo().getSensor_status());
        reportRobotInfo.setControlSystem(80);//控制系统
        reportRobotInfo.setChargeSystem(80);//充电系统
        reportRobotInfo.setImagePickup(bits[0]); //可见光摄像
        reportRobotInfo.setInfraredPickup(bits[1]);//红外摄像
        reportRobotInfo.setBatteryQuantity(memRobot.getRobotInfo().getBattery_quantity());//当前电池电量
        reportRobotInfo.setLeftWheelSpeed(memRobot.getRobotInfo().getVelocity_x());//左轮速度
        reportRobotInfo.setRightWheelSpeed(memRobot.getRobotInfo().getVelocity_x());//右轮速度
        //按通常情况下充电功率920W,电压48V
        reportRobotInfo.setExternalPowerCurrent(19.15);//外供电源电流
        reportRobotInfo.setExternalPowerVoltage(48);//外供电源电压
        reportRobotInfo.setCharge(memRobot.getRobotInfo().getBattery_voltage()); //充电 ，用电池电压
        if(memRobot.getCharging()==0){
            reportRobotInfo.setChargeStatus("充电中"); //充电状态
        }else{
            reportRobotInfo.setChargeStatus("放电中"); //充电状态
        }
        reportRobotInfo.setTotalMileage(60); //运行里程
        reportRobotInfo.setTotalDefects(robotParamMapper.getTotalDefects()); //发现缺陷数
        reportRobotInfo.setTotalInspectiDevs(robotParamMapper.getTotalInspectiDevs()); //巡检总设备数
        reportRobotInfo.setTotalRunTime(robotParamMapper.getTotalRunTime()/3600.0);//运行时间
        if(DataCache.getSysParamInt("ros.hasWeatherStation")==1){
            //MemWeatherStatus weatherStatus= WeatherStationMonitor.getWeatherUtil(memRobot.getSiteId()).weatherStatus;
            //todo wait
            MemWeatherStatus weatherStatus= new MemWeatherStatus();
            reportRobotInfo.setEnvTemperature(weatherStatus.getTemp()); //环境温度
            reportRobotInfo.setEnvHumidity(weatherStatus.getHum());     //湿度
            reportRobotInfo.setWindSpeed(weatherStatus.getWindSpeed()); //风速
        }else{
            reportRobotInfo.setEnvTemperature(DataCache.getSysParamFloat("demo.weatherTemp")); //环境温度
            reportRobotInfo.setEnvHumidity(DataCache.getSysParamFloat("demo.weatherHum"));     //湿度
            reportRobotInfo.setWindSpeed(DataCache.getSysParamFloat("demo.weatherWindSpeed")); //风速
        }
        return ResultUtil.success(reportRobotInfo);
    }

    @ResponseBody
    @GetMapping("/update/sessionRobot/{robotId}")
    public Result updateSessionRobot(@PathVariable String robotId, HttpServletRequest request){
        String token=request.getHeader(Constans.TOKEN);
        String siteId=getRequestSiteId(request);
        SessionRobot sessionRobot = devService.findSessionRobot(siteId,robotId);
        SessionManager.addOrUpdateSessionEntity(token,Constans.SESSION_ROBOT,sessionRobot);
        return ResultUtil.success(sessionRobot);
    }




    @ApiOperation(value = "查询设备区域",notes = " ")
    @ResponseBody
    @GetMapping("/getAreaList")
    public List<Area> getAreaList(HttpServletRequest request){
        String siteId = getRequestSiteId(request);
        List<Area> list = areaMapper.selectAll(siteId);
        return list;
    }

    @ApiOperation(value = "查询设备区域",notes = " ")
    @ResponseBody
    @GetMapping("/getBayAreaList")
    public List<Area> getBayAreaList(HttpServletRequest request){
        String siteId = getRequestSiteId(request);
        List<Area> list = areaMapper.selectAll(siteId);
        return list;
    }


    /**
     * 树上不显示设备类型 ：  站点--区域--设备
     *
     * showSmallDev 0 否 1 是
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("/saveDev")
    public Result saveDev (@RequestBody Map params,HttpServletRequest request){
        String siteId = getRequestSiteId(request);
        if(siteId==null){
            logger.error("修改失败,站点id为空");
            return ResultUtil.failed("修改失败,站点id为空");
        }
        String name=(String) params.get("name");
        String parentNodeType = (String) params.get("parentNodeType");//上一级的节点类型 .如果是area，则添加设备
        String parentId = (String) params.get("parentId");
        String devTypeId = (String) params.get("devTypeId");
        String uid = (String) params.get("uid");//如果有uid则是修改
        String code=(String) params.get("code");
        if(uid!=null && !"".equals(uid)){
            //update
            int ret=0;
            if ( "1".equals(parentNodeType)){
                Area item=new Area();
                item.setUid(uid);
                item.setName(name);
                ret=areaMapper.update(item);
            }else{
                Dev dev = new Dev();
                if (code!=null && !"".equals(code)) {
                    Dev exitDev = this.devService.getDevByCode(code);
                    if (exitDev!=null && !uid.equals(exitDev.getUid())) {
                        return ResultUtil.failed("修改失败");
                    }
                    dev.setCode(code);
                }
                dev.setUid(uid);
                dev.setName(name);
                dev.setDevTypeId(devTypeId);
                ret= this.devService.updateDev(dev);
            }
            if (ret==1) {
                DataCache.reload();
                return ResultUtil.build(0,"修改成功",null);
            } else {
                return ResultUtil.failed("修改失败");
            }
        }



        if ( "1".equals(parentNodeType)){ //添加area
            Area record=new Area();
            record.setName(name);
            record.setParams("");
            record.setSiteId(siteId);
            int orderNum=areaMapper.getMaxOrderNumber(siteId);
            orderNum++;
            record.setOrderNumber(orderNum);
            int ret=areaMapper.insert(record);
            if (ret==1) {
                DataCache.reload();
                return ResultUtil.build(0,"保存成功",record);
            } else {
                return ResultUtil.failed("保存失败");
            }
        } else{// else if("3".equals(parentNodeType)){ //添加设备
            Dev saveDev = new Dev();
            if (code!=null && !"".equals(code)) {
                Dev exitDev = this.devService.getDevByCode(code);
                if (exitDev!=null) {
                    return ResultUtil.failed("修改失败");
                }
                saveDev.setCode(code);
            }
            saveDev.setName(name);
            saveDev.setParentId(parentId);
            saveDev.setDevTypeId(devTypeId);
            saveDev.setStatus(1);
            saveDev.setIsSystem(0);
            saveDev.setParams("");
            saveDev.setSiteId(siteId);
            int orderNum=devMapper.getMaxOrderNumber(siteId);
            orderNum++;
            saveDev.setOrderNumber(orderNum);
            Dev dev = devService.addDev(saveDev);
            if (dev!=null) {
                DataCache.reload();
                return ResultUtil.build(0,"保存成功",dev);
            } else {
                return ResultUtil.failed("保存失败");
            }
        }
    }


    /**
     *
     * @param
     * @return
     */
    @ResponseBody
    @GetMapping("/deleteDev/{id}")
    public Result deleteDev(@PathVariable String id){
        devService.deleteDev(id);
        DataCache.reload();
        return ResultUtil.build(0,"删除成功",null);
    }





    /**
     * 从请求session中读取siteId
     * @param request
     * @return
     */
    private String getRequestSiteId(HttpServletRequest request){
        String token=request.getHeader(Constans.TOKEN);
        Site  site = (Site) SessionManager.getSessionEntity(token,Constans.SESSION_SITE);
        if(site==null){
            return null;
        }
        return site.getUid();
    }


    /**
     * 从请求session中读取site
     * @param request
     * @return
     */
    private Site getRequestSite(HttpServletRequest request){
        String token=request.getHeader(Constans.TOKEN);
        Site  site = (Site) SessionManager.getSessionEntity(token,Constans.SESSION_SITE);
        return site;
    }


}


package com.wootion.controller;


import com.wootion.Debug;
import com.wootion.agvrobot.session.SessionRobot;
import com.wootion.commons.Constans;
import com.wootion.commons.Result;
import com.wootion.mapper.PtzSetMapper;
import com.wootion.mapper.SysParamMapper;
import com.wootion.robot.MemRobot;
import com.wootion.robot.MemUtil;
import com.wootion.service.ITaskService;
import com.wootion.utiles.ResultUtil;
import com.wootion.utiles.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 调试用类
 */
@RestController
@RequestMapping("/req_svr/debug")
public class DebugController {
    Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private SysParamMapper sysParamMapper;
    @Autowired
    private PtzSetMapper ptzSetMapper;
    @Autowired
    ITaskService taskService;

    /**
     * 调试：同步系统参数，runMark，runLine，区域等
     * @param params
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/syncData")
    public Result syncData(@RequestBody Map params,  HttpServletRequest request) {
        int syncType=(Integer)params.get("syncType");
        String robotId=this.getRobotId(request);
        MemRobot memRobot=MemUtil.queryRobotById(robotId);
        boolean result=false;
        if(syncType==1){
            result=taskService.syncParamsData(memRobot);
        }else  if(syncType==2){
            result=taskService.syncMapData(memRobot);
        }else  if(syncType==3){
            result=taskService.syncFullTask(memRobot);
        }
        if(result){
            return ResultUtil.success();
        }else{
            return ResultUtil.failed("同步失败");
        }
    }



    @ResponseBody
    @GetMapping(value = "/queryDebugParam")
    public Result queryDebugParam() {
        Map map=new HashMap();
        map.put("haveRobotFtp",Debug.haveRobotFtp);
        map.put("sendMoveCmd",Debug.sendMoveCmd);
        return ResultUtil.build(0,"ok",map);
    }

    @ResponseBody
    @PostMapping(value = "/setDebugParam")
    public Result setDebugParam(@RequestBody Map params) {
        Debug.haveRobotFtp=Integer.parseInt((String)params.get("haveRobotFtp"));
        Debug.sendMoveCmd=Integer.parseInt((String)params.get("sendMoveCmd"));
        return ResultUtil.build(0,"success",null);
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
    private String getRobotIp(HttpServletRequest request) {
        String token = request.getHeader(Constans.TOKEN);
        SessionRobot sessionRobot = (SessionRobot) SessionManager.getSessionEntity(token, Constans.SESSION_ROBOT);
        if (sessionRobot == null || sessionRobot.getRobot() == null) {
            return null;
        }
        String robotIp = sessionRobot.getRobot().getRobotIp();
        return robotIp;
    }
}

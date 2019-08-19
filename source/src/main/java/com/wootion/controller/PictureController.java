package com.wootion.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wootion.agvrobot.session.SessionRobot;
import com.wootion.agvrobot.utils.DateUtil;
import com.wootion.agvrobot.utils.FileUtil;
import com.wootion.commons.Constans;
import com.wootion.commons.Result;
import com.wootion.mapper.SnappedPictureMapper;
import com.wootion.model.Site;
import com.wootion.model.SnappedPicture;
import com.wootion.robot.MemRobot;
import com.wootion.robot.MemUtil;
import com.wootion.task.CameraController;
import com.wootion.task.InfraredController;
import com.wootion.utiles.ResultUtil;
import com.wootion.utiles.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 抓图和查询
 */
@Controller
@RequestMapping("/req_svr/snapPic")
public class PictureController {

    @Autowired
    SnappedPictureMapper snappedPictureMapper;


    @ResponseBody
    @GetMapping(value = "/snapPicture/{snapSource}")
    public Result snapPicture(@PathVariable int snapSource, HttpServletRequest request) {
        String siteId = getRequestSiteId(request);
        String robotIp=getRobotIp(request);
        if(robotIp==null){
            return ResultUtil.failed("没有选择机器人。");
        }
        MemRobot memRobot= MemUtil.queryRobot(robotIp);
        if(memRobot==null){
            return ResultUtil.failed("没有机器人。");
        }
        System.out.println("snapSource="+snapSource);
        String cmd="capture_picture";
        java.util.Date now=new java.util.Date();
        String strDate= DateUtil.dateToString(now,"yyyyMMdd");
        String strNow=DateUtil.dateToString(now,"yyyyMMddHHmmss");
        String picPath=null;
        Result result=null;
        String fileName;
        String srcFileName;
        if(snapSource==0){
            CameraController cameraController=new CameraController();
            fileName="/pic_"+strNow+".jpg";
            srcFileName=FileUtil.getUserHome()+"preset"+fileName;
            result= cameraController.cameraAction(memRobot,cmd,srcFileName);
        }else{
            InfraredController infraredController=new InfraredController();
            fileName="/infra_"+strNow+".jpg";
            srcFileName=FileUtil.getUserHome()+"preset"+fileName;
            result= infraredController.doAction(robotIp,cmd,srcFileName);
        }

        if(result.getCode()!=0){
            return ResultUtil.build(-1,"摄像机截图失败",null);
        }
        //复制图片:从机器人上的picture copy到服务器的picture目录
        String toFileName=FileUtil.getUserHome()+"picture/"+strDate+fileName;
        int copyRet= FileUtil.copyFile(srcFileName,toFileName);
        if(copyRet!=1){
            return ResultUtil.build(-1,"截图失败：复制图片文件失败",null);
        }
        FileUtil.deleteFile(srcFileName);

        SnappedPicture snappedPicture=new SnappedPicture();
        snappedPicture.setCreateTime(now);
        snappedPicture.setFileName("/picture/"+strDate+fileName);
        snappedPicture.setIsInfra(snapSource);
        snappedPicture.setSiteId(siteId);
        int ret=snappedPictureMapper.insert(snappedPicture);
        if(ret==1){
            return ResultUtil.success();
        }else
        {
            return ResultUtil.failed();
        }
    }


    @ResponseBody
    @GetMapping(value = "/getHistorySnapped")
    public Result getHistoryVideoList(HttpServletRequest request,
                                      @RequestParam(value = "pageNum", required = false) Integer pageNum,
                                      @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                      @RequestParam(value = "fromDate", required = true) String fromDate,
                                      @RequestParam(value = "toDate", required = true) String toDate) {

        String token = request.getHeader(Constans.TOKEN);
        String siteId = getRequestSiteId(request);
        if (pageNum!=null && pageSize!=null) {
            PageHelper.startPage(pageNum, pageSize);
        }
        if (fromDate==null || toDate==null) {
            return ResultUtil.failed("请输入开始日期和结束日期");
        }

        List<SnappedPicture> list = snappedPictureMapper.findList(siteId, fromDate + " 00:00:00", toDate + " 23:59:59");
        if(list!=null){
            for (SnappedPicture item : list) {
                item.setPicTypeName(item.getIsInfra()==1?"红外":"可见光");
            }
        }
        PageInfo pageInfo = new PageInfo(list);
        return ResultUtil.success(pageInfo);
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
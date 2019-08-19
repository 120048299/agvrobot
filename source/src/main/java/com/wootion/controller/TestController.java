package com.wootion.controller;

import com.wootion.agvrobot.dto.UserBean;
import com.wootion.agvrobot.session.SessionRobot;
import com.wootion.agvrobot.utils.FileUtil;
import com.wootion.agvrobot.utils.PictureUtil;
import com.wootion.commons.Constans;
import com.wootion.commons.Result;
import com.wootion.config.SysParam;
import com.wootion.model.Dev;
import com.wootion.task.ForeignReadScale;
import com.wootion.utiles.ResultUtil;
import com.wootion.utiles.SessionManager;
import com.wootion.utiles.poi.ExportSvc;
import com.wootion.utiles.poi.TblCell;
import com.wootion.utiles.poi.XlsxExportSvcImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/req_svr/test")
public class TestController {

    @Autowired
    private SysParam sysParam;

    Logger logger = LoggerFactory.getLogger(TestController.class);

    @ResponseBody
    @GetMapping("getForeignDetectOrgPics")
    public List<Map> getForeignDetectOrgPics(HttpServletRequest request) {
        String token=request.getHeader(Constans.TOKEN);
        UserBean userBean = (UserBean) SessionManager.getSessionEntity(token,SessionManager.USER_BEAN);
        if(userBean==null){
            logger.info("to auditTaskPlan, not login.");
            return null;
        }
        String orgPath = FileUtil.getPresetPath("detect")+"20190101_000000";
        File file = new File(orgPath);
        File[] fs = file.listFiles();
        List<Map> list = new ArrayList<>();
        for(File f:fs) {
            if(!f.isDirectory()) {
                String name = f.getName();
                String suffix = name.substring(name.lastIndexOf(".") + 1);
                if (suffix.equalsIgnoreCase("png") || suffix.equalsIgnoreCase("jpg")) {
                    String path=(f.getPath()).substring((f.getPath()).indexOf("preset"));
                    Map<String, Object> pic = new HashMap<>();
                    pic.put("picName", name);
                    pic.put("picPath", path);
                    pic.put("picStatus", 0);
                    list.add(pic);
                }
            }
        }
        return list;
    }

    @ResponseBody
    @PostMapping("resetForeignDetect")
    public Result resetForeignDetect(HttpServletRequest request) {
        String token=request.getHeader(Constans.TOKEN);
        UserBean userBean = (UserBean) SessionManager.getSessionEntity(token,SessionManager.USER_BEAN);
        if(userBean==null){
            logger.info("to auditTaskPlan, not login.");
            return null;
        }
        String foreignDetectExistPath = FileUtil.getForeignDetectPath("detect") + sysParam.foreignDetectExistPath;
        String foreignDetectNonePath = FileUtil.getForeignDetectPath("detect") + sysParam.foreignDetectNonePath;
        // 重建归档目录
        FileUtil.delFolder(foreignDetectExistPath);
        FileUtil.delFolder(foreignDetectNonePath);
        FileUtil.createDir(foreignDetectExistPath);
        FileUtil.createDir(foreignDetectNonePath);
        return ResultUtil.success();
    }

    @ResponseBody
    @PostMapping("foreignDetect")
    public Map foreignDetect(HttpServletRequest request, @RequestBody Map params) {
        String token=request.getHeader(Constans.TOKEN);
        UserBean userBean = (UserBean) SessionManager.getSessionEntity(token,SessionManager.USER_BEAN);
        if(userBean==null){
            logger.info("to auditTaskPlan, not login.");
            return null;
        }
        String picName = (String)params.get("picName");
        String robotIp=this.getRobotIp(request);
        String processPath = FileUtil.getPresetPath("detect")+"20190101_000000";
        Map ret = ForeignReadScale.detectForeign(picName, processPath, robotIp);
        Map result = params;
        if (ret!=null) {
            String foreignPic = (String)ret.get("foreignPic");
            String fileName = foreignPic.substring(foreignPic.lastIndexOf(File.separator)+1);
            Integer hasForeign = (Integer)ret.get("hasForeign");
            String srcFile= FileUtil.getUserHome()+foreignPic;
            String archivePath = FileUtil.getForeignDetectPath("detect") + sysParam.foreignDetectNonePath;
            if(hasForeign==1) {
                archivePath = FileUtil.getForeignDetectPath("detect") + sysParam.foreignDetectExistPath;
            }
            FileUtil.copyFile(srcFile, archivePath + File.separator + fileName);
            result.put("hasForeign", hasForeign);
            result.put("foreignPic", foreignPic);
            result.put("picStatus", 21);
        } else {
            result.put("picStatus", 22);
        }
        return result;
    }

    @ResponseBody
    @PostMapping("exportDetectResult")
    public Result exportDetectResult(HttpServletRequest request, @RequestBody Map params) {
        String token=request.getHeader(Constans.TOKEN);
        UserBean userBean = (UserBean) SessionManager.getSessionEntity(token,SessionManager.USER_BEAN);
        if(userBean==null){
            logger.info("to auditTaskPlan  ,not login.");
            return null;
        }

        String fileName = "图片异物检测结果"+ FileUtil.dateRandom()+".xlsx";
        int ret = this.generateDetectResultReportExcel(FileUtil.getBasePath()+"report"+File.separator+fileName,params);
        if(ret>=0){
            return ResultUtil.build(0,"导出成功",fileName);
        }else{
            return ResultUtil.failed();
        }
    }

    /**
     * 图片异物检测结果Excel
     * @param fileName
     * @param params
     * @return
     */
    private  int generateDetectResultReportExcel(String fileName,Map params){
        List<Map> pics = (List<Map>)params.get("pics");

        ExportSvc exportSvc = new XlsxExportSvcImpl("图片异物检测结果");
        File file = new File(fileName);
        List [] heads = new List[1];
        List list = new ArrayList ();
        list.add(new TblCell("1",  "图片异物检测结果", 10, 1));
        heads[0]=list;
        {
            String rowData[] = {
                    "序号", "文件名", "原始图片", "结果图片", "检测结果", "归档路径", "开始时间", "结束时间"
            };
            int colSpan[] = {1,1,2,2,1,1,1,1};
            exportSvc.writeRow(2,rowData,colSpan,1);
        }
        int succNum = 0;
        int failNum = 0;
        int existNum = 0;
        int noneNum = 0;
        String maxTime = "0000-00-00 00:00:00";
        String minTime = "9999-12-31 59:59:59";
        for(int i = 0; i < pics.size(); i++)
        {
            Map pic = pics.get(i);
            {
                String beginTime =  (String) pic.get("beginTime");
                String endTime =  (String) pic.get("endTime");
                if (beginTime.compareTo(minTime)<0) {
                    minTime = beginTime;
                }
                if (endTime.compareTo(maxTime)>0) {
                    maxTime = endTime;
                }
                Integer picStatus = (Integer) pic.get("picStatus");
                Integer hasForeign = (Integer) pic.get("hasForeign");
                String picPath =  (String) pic.get("picPath");
                String foreignPic =  (String) pic.get("foreignPic");
                String picStatusText = "未知";
                switch (picStatus) {
                    case 0:
                        picStatusText = "待检测";
                        break;
                    case 1:
                        picStatusText = "检测中";
                        break;
                    case 21:
                        picStatusText = "检测成功" + (hasForeign==1?"[有异物]":"[无异物]");
                        break;
                    case 22:
                        picStatusText = "检测失败";
                }
                String archivePath = "未归档";
                if (picStatus==21) {
                    succNum++;
                    if (hasForeign==1) {
                        existNum++;
                        archivePath = FileUtil.getForeignDetectPath("detect") + sysParam.foreignDetectExistPath;
                    } else {
                        noneNum++;
                        archivePath = FileUtil.getForeignDetectPath("detect") + sysParam.foreignDetectNonePath;
                    }
                } else {
                    failNum++;
                }
                String rowData[] = {
                        String.valueOf(i+1), (String) pic.get("picName"),
                        "原始图片", "结果图片",
                        picStatusText, archivePath,
                        beginTime, endTime
                };
                int colSpan[] = {1,1,2,2,1,1,1,1};
                exportSvc.writeRow(3+i,rowData,colSpan,1, 120);
                if(picPath!=null){
//                    String tempFile=FileUtil.getBasePath()+"report"+File.separator+"temp1.jpg";
                    String srcFile= FileUtil.getUserHome()+picPath;
//                    int zoomResult= PictureUtil.zoomImage(srcFile,tempFile,320,120);
//                    if(zoomResult==0){
                        exportSvc.exportPic(srcFile,3+i,2,4+i,4);
//                    }
                }
                if(foreignPic!=null){
//                    String tempFile=FileUtil.getBasePath()+"report"+File.separator+"temp2.jpg";
                    String srcFile= FileUtil.getUserHome()+foreignPic;
//                    int zoomResult=PictureUtil.zoomImage(srcFile,tempFile,320,120);
//                    if(zoomResult==0){
                        exportSvc.exportPic(srcFile,3+i,4,4+i,6);
//                    }
                }
            }
        }

        String diffTimes = getDistanceTime(minTime, maxTime);
        exportSvc.writeTblHead(heads, 1, 44);
        {
            String rowData[] = {
                    "检测总数", pics.size() + "张",
                    "成功总数", succNum + "张（其中："+existNum+"张 有异物，"+noneNum+"张 无异物）",
                    "失败总数", failNum+"张",
                    "检测时间", diffTimes
            };
            int colSpan[] = {1,1,2,2,1,1,1,1};
            exportSvc.writeRow(1,rowData,colSpan,1);
        }
        exportSvc.writeFile(file);
        return 0;
    }



    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     * @param str1 时间参数 1 格式：1990-01-01 12:00:00
     * @param str2 时间参数 2 格式：2009-01-01 12:00:00
     * @return String 返回值为：xx天xx小时xx分xx秒
     */
    public static String getDistanceTime(String str1, String str2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff ;
            if(time1<time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff/1000-day*24*60*60-hour*60*60-min*60);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String result = sec + "秒";
        if (min>0) {
            result = min + "分" + result;
        }
        if (hour>0) {
            result = hour + "小时" + result;
        }
        if (day>0) {
            result = day + "天" + result;
        }
        return result;
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

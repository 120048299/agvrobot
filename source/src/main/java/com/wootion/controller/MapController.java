package com.wootion.controller;

import com.wootion.agvrobot.session.SessionRobot;
import com.wootion.commons.Constans;
import com.wootion.commons.Result;
import com.wootion.mapper.*;
import com.wootion.model.*;
import com.wootion.robot.MemRobot;
import com.wootion.robot.MemUtil;
import com.wootion.service.IMapService;
import com.wootion.service.ITaskService;
import com.wootion.task.MoveByNav;
import com.wootion.task.map2.Coordinate;
import com.wootion.utiles.DataCache;
import com.wootion.utiles.ResultUtil;
import com.wootion.utiles.RunMarkUtil;
import com.wootion.utiles.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Logger;

@Controller
@RequestMapping("/req_svr/map")
public class MapController {

    private static final Logger logger = Logger.getLogger(MapController.class.getName());

    @Autowired
    private IMapService mapService;

    @Autowired
    private SiteMapper siteMapper;
    @Autowired
    RunLineMapper runLineMapper;
    @Autowired
    RunMarkMapper runMarkMapper;
    @Autowired
    DevMapper devMapper;
    @Autowired
    AreaMapper areaMapper;
    @Autowired
    PtzSetMapper ptzSetMapper;
    @Autowired
    ITaskService taskService;

    private String geoFeatureCollection = "{\"type\": \"FeatureCollection\"," +
            "                 \"features\": [ %s ]}";
    private String geoFeature = "{\"type\": \"Feature\"," +
            "                     \"properties\": { \"name\": \"%s\"}," +
            "                     \"geometry\": %s}";
    private String geoPoint = "{\"type\": \"Point\"," +
            "        \"coordinates\": [" +
            "        \"%f\"," +
            "        \"%f\"" +
            "        ]}";



   /* public void getMap() {


        String geojson = "";
        List<RunMark> runMarkList = mapService.getFiltedRunMarkAndRunLine();
        List<String> featureList = new ArrayList<>();

        for (RunMark runMark: runMarkList) {
            if (runMark.getLat() != null && runMark.getLon() != null) {
                featureList.add(getFeature(runMark));
            }
        }

        resultJson = String.format(this.geoFeatureCollection, String.join(", ", featureList));
        System.out.println("all RunMark " + resultJson);
        replySuccess2();

    }
*/
    @ResponseBody
    @GetMapping("/getRunMarks")
    public Result getRunMarks(HttpServletRequest request) {
        String siteId=getRequestSiteId(request);
        Site site = siteMapper.findByUid(siteId);
        List<RunMark> pointList = new ArrayList<>();
        List<RunMark> runMarkList = mapService.getRunMarkListBySiteId(siteId);
        for (RunMark runMark: runMarkList) {
            if (runMark.getLon() != null && runMark.getLat() != null) {
                double[] pt = Coordinate.nav2Web(runMark.getLon(), runMark.getLat(),site.getScale());
                runMark.setMapLon(pt[0]);
                runMark.setMapLat(pt[1]);
                pointList.add(runMark);
            }
        }
        return ResultUtil.success(pointList);
    }



    private String getFeature(RunMark runMark) {
        String point = String.format(this.geoPoint, runMark.getLat(), runMark.getLon());
        String feature = String.format(this.geoFeature, runMark.getUid(), point);
        System.out.println("feature: " + feature);
        return feature;
    }

    @ResponseBody
    @GetMapping("/getRunLineList")
    public Result getRunLineList(HttpServletRequest request) {
        String siteId=getRequestSiteId(request);
        Site site = siteMapper.findByUid(siteId);
        List<Map> list = runLineMapper.getRunlineListBySiteId(siteId);
        for (Map data : list) {
            double lon= Double.parseDouble(String.valueOf(data.get("lon"))) ;
            double lan= Double.parseDouble(String.valueOf(data.get("lat"))) ;
            double[] pt = Coordinate.nav2Web(lon, lan, site.getScale());
            data.put("lon", pt[0]);
            data.put("lat", pt[1]);
        }
        return ResultUtil.success(list);
    }


    /**
     * 地图点选，查询框选后的点位 .如果巡检点未设置或者未启用，则选择不出来。
     */
    @ResponseBody
    @PostMapping("/getSpotList")
    public List<?> getPtzSpot(@RequestBody Map params) {
        ArrayList<String> markIdList=(ArrayList<String>) params.get("markIdList");
        if(markIdList==null || markIdList.size()==0){
            return null;
        }
        // todo wait
        //List<?> list =ptzSetMapper.getPtzSpotList(markIdList);
        //return list;
        return null;

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

    @ResponseBody
    @PostMapping(value = "/addMapRunLine")
    public Result addMapRunLine(@RequestBody Map params,HttpServletRequest request) {
        String siteId = getRequestSiteId(request);
        ArrayList<String> runMarkIdList=( ArrayList<String>) params.get("runMarkIds");
        boolean result = mapService.addMapRunLine(runMarkIdList, siteId);
        if (result) {
            DataCache.reload();
            return ResultUtil.build(0,"成功添加线段",null);
        } else {
            return ResultUtil.build(1,"添加线段失败",null);
        }
    }

    /**
     * xuanzhong de duoge runMark,yi zui yuan de liangge dian wei zhixian ,zhongjiandian duiqi
     * @param params
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/alignRunMarks")
    public Result alignRunMarks(@RequestBody Map params,HttpServletRequest request) {
        String siteId = getRequestSiteId(request);
        ArrayList<String> runMarkIdList=( ArrayList<String>) params.get("runMarkIds");
        int type=(int) params.get("type");
        if(type==1 && runMarkIdList.size()<3){
            return ResultUtil.build(-1,"至少要选择3个点",null);
        } else if((type==2 || type==3)  && runMarkIdList.size()<2){
            return ResultUtil.build(-1,"至少要选择2个点",null);
        }
        mapService.alignRunMarks(siteId,runMarkIdList, type);
        DataCache.reload();
        return ResultUtil.build(0,"对齐成功",null);
    }

    @ResponseBody
    @PostMapping(value = "/deleteMapRunLine")
    public Result deleteMapRunLine(@RequestBody Map params,HttpServletRequest request) {
        String siteId = getRequestSiteId(request);
        ArrayList<Map> lineList =( ArrayList<Map>) params.get("lineList");
        int result = mapService.deleteMapRunLine(lineList, siteId);
        if (result >0) {
            DataCache.reload();
            return ResultUtil.build(0,"总共"+lineList.size()+"条"+"成功删除"+result+"条",null);
        } else {
            return ResultUtil.build(1,"删除连线失败",null);
        }
    }

    @ResponseBody
    @PostMapping(value = "/deleteMapRunMark")
    public Result deleteMapRunMark(@RequestBody Map params,HttpServletRequest request) {
        String siteId = getRequestSiteId(request);
        String ids = (String) params.get("RunMarkID");
        String[] temp= ids.split(",");
        List<String> idList = new ArrayList<>();
        for (String id:temp){
            idList.add(id);
        }
        int result = mapService.deleteMapRunMark(idList);
        if (result >0) {
            DataCache.reload();
            return ResultUtil.build(0,"总共"+idList.size()+"点位"+"成功删除"+result+"点位",null);
        } else {
            return ResultUtil.build(1,"删除点位失败",null);
        }
    }

    @ResponseBody
    @PostMapping(value = "/updateDevParams")
    public Result updateDevParams(@RequestBody Map params,HttpServletRequest request) {
        String siteId = getRequestSiteId(request);
        Site site = siteMapper.findByUid(siteId);
        ArrayList temp=(ArrayList) params.get("params");
        double x=parseDoubleOrInteger(temp.get(0));
        double y=parseDoubleOrInteger(temp.get(1));
        double[] tempP = Coordinate.web2Nav(x, y, site.getScale());
//        double[] tempP = Coordinate.web2Nav((double) temp.get(0), (double) temp.get(1));
        String str = String.format("%s%s%s",tempP[0],",",tempP[1]);
        String uid=(String) params.get("uid");
        Dev dev=devMapper.select(uid);
        if(dev!=null){
            dev.setParams(str);
            int ret=devMapper.update(dev);
            if (ret==1) {
                return ResultUtil.build(0,"成功更新设备坐标",null);
            } else {
                return ResultUtil.build(1,"更新设备坐标失败",null);
            }
        }
        logger.info("更新设备坐标失败,没有找到设备:uid="+uid);
        return ResultUtil.build(1,"更新设备坐标失败,没有找到设备",null);
    }


    /**
     * 设置 区域,bay   矩形边线  .clear=1 为清除
     * @param params
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/updateAreaBayBorder")
    public Result updateAreaBayBorder(@RequestBody Map params,HttpServletRequest request) {
        String siteId = getRequestSiteId(request);
        Site site = siteMapper.findByUid(siteId);
        String uid=(String) params.get("uid");
        if(uid==null || "".equals(uid)){
            return ResultUtil.build(1,"保存失败,参数错误，区域未选择。",null);
        }
        Area area=areaMapper.select(uid);
        if (area==null ) {
            logger.info("保存失败,参数错误，区域或不存在。uid="+uid);
            return ResultUtil.build(1,"保存失败,参数错误，区域不存在。",null);
        }
        if(params.get("clear")!=null  ){
            area.setParams("");
        }else{
            ArrayList<ArrayList<ArrayList>> coor=(ArrayList) params.get("params");
            ArrayList<ArrayList> points=coor.get(0);
            String pointStr="";
            for ( int i=0;i<5;i++){
                ArrayList point = points.get(i);
                double x=parseDoubleOrInteger(point.get(0));
                double y=parseDoubleOrInteger(point.get(1));
                double[] tempP1 = Coordinate.web2Nav(x, y, site.getScale());
                pointStr += String.format("%.2f,%.2f",tempP1[0],tempP1[1]);
                if(i<4){
                    pointStr += "," ;
                }
            }
            area.setParams(pointStr);
        }
        int ret=areaMapper.update(area);
        if (ret==1) {
            return ResultUtil.build(0,"保存成功",null);
        } else {
            return ResultUtil.build(1,"保存失败",null);
        }
    }


    private double parseDoubleOrInteger(Object x){
        return Double.parseDouble(x.toString());
    }
    @ResponseBody
    @PostMapping("/addMaintainOrobstacle")
    public Result addMaintainOrobstacle(@RequestBody Map params,HttpServletRequest request){
        String siteId=getRequestSiteId(request);
        String robotId=getRobotId(request);
        Site site = siteMapper.findByUid(siteId);
        int maintainType = (Integer)params.get("maintainType");
        String name=(String) params.get("name");
        ArrayList<ArrayList<ArrayList>> coor=(ArrayList) params.get("coor");
        double x=parseDoubleOrInteger(coor.get(0).get(0).get(0));
        double y=parseDoubleOrInteger(coor.get(0).get(0).get(1));
        double[] tempP1 = Coordinate.web2Nav(x, y, site.getScale());
        String p1 = String.format("%.2f,%.2f",tempP1[0],tempP1[1]);

         x=parseDoubleOrInteger(coor.get(0).get(1).get(0));
         y=parseDoubleOrInteger(coor.get(0).get(1).get(1));
        double[] tempP2 = Coordinate.web2Nav(x, y, site.getScale());
        String p2 = String.format("%.2f,%.2f",tempP2[0],tempP2[1]);

        x=parseDoubleOrInteger(coor.get(0).get(2).get(0));
        y=parseDoubleOrInteger(coor.get(0).get(2).get(1));
        double[] tempP3 = Coordinate.web2Nav(x, y, site.getScale());
        String p3 = String.format("%.2f,%.2f",tempP3[0],tempP3[1]);

        x=parseDoubleOrInteger(coor.get(0).get(3).get(0));
        y=parseDoubleOrInteger(coor.get(0).get(3).get(1));
        double[] tempP4 = Coordinate.web2Nav(x, y, site.getScale());
        String p4 = String.format("%.2f,%.2f",tempP4[0],tempP4[1]);

        x=parseDoubleOrInteger(coor.get(0).get(4).get(0));
        y=parseDoubleOrInteger(coor.get(0).get(4).get(1));
        double[] tempP5 = Coordinate.web2Nav(x, y, site.getScale());
        String p5 = String.format("%.2f,%.2f",tempP5[0],tempP5[1]);
        System.out.println("points="+p1+","+p2+","+p3+","+p4+","+p5);
        Date date=new Date();
        Timestamp timeStamp = new Timestamp(date.getTime());
        String notes =(String) params.get("notes");
        MaintainArea maintainArea = new MaintainArea();
        maintainArea.setName(name);
        maintainArea.setModifyTime(timeStamp);
        maintainArea.setPoint1(p1);
        maintainArea.setPoint2(p2);
        maintainArea.setPoint3(p3);
        maintainArea.setPoint4(p4);
        maintainArea.setPoint5(p5);
        maintainArea.setMaintainType(maintainType);
        maintainArea.setMemo(notes);
        maintainArea.setSiteId(siteId);
        boolean checkName=mapService.checkMaintainName(name);
        if(!checkName){
            return ResultUtil.build(1,"名字重复，请重使用新的名字",null);
        }
        int ret=mapService.addMaintainArea(maintainArea,robotId);
        MemRobot memRobot=MemUtil.queryRobotById(robotId);
        if (ret==1 && maintainType==0 ) {
            DataCache.reload();
            taskService.syncMapData(memRobot);
            return ResultUtil.build(0,"添加避障功能成功",null);
        } else if (ret==1 && maintainType==1){
            DataCache.reload();
            taskService.syncMapData(memRobot);
            return ResultUtil.build(0,"添加检修区域成功",null);
        }else if (ret!=1 && maintainType==0){
            return ResultUtil.build(1,"添加避障功能失败",null);
        }else {
            return ResultUtil.build(1,"添加检修区域失败",null);
        }

    }

    @ResponseBody
    @PostMapping("/addChargeRoom")
    public Result addChargeRoom(@RequestBody Map params,HttpServletRequest request){
        String siteId=getRequestSiteId(request);
        String robotId=getRobotId(request);
        Site site = siteMapper.findByUid(siteId);
        String name=(String) params.get("name");
        ArrayList<ArrayList<ArrayList>> coor=(ArrayList) params.get("coor");
        double x=parseDoubleOrInteger(coor.get(0).get(0).get(0));
        double y=parseDoubleOrInteger(coor.get(0).get(0).get(1));
        double[] tempP1 = Coordinate.web2Nav(x, y, site.getScale());
        String p1 = String.format("%.2f,%.2f",tempP1[0],tempP1[1]);

        x=parseDoubleOrInteger(coor.get(0).get(1).get(0));
        y=parseDoubleOrInteger(coor.get(0).get(1).get(1));
        double[] tempP2 = Coordinate.web2Nav(x, y, site.getScale());
        String p2 = String.format("%.2f,%.2f",tempP2[0],tempP2[1]);

        x=parseDoubleOrInteger(coor.get(0).get(2).get(0));
        y=parseDoubleOrInteger(coor.get(0).get(2).get(1));
        double[] tempP3 = Coordinate.web2Nav(x, y, site.getScale());
        String p3 = String.format("%.2f,%.2f",tempP3[0],tempP3[1]);

        x=parseDoubleOrInteger(coor.get(0).get(3).get(0));
        y=parseDoubleOrInteger(coor.get(0).get(3).get(1));
        double[] tempP4 = Coordinate.web2Nav(x, y, site.getScale());
        String p4 = String.format("%.2f,%.2f",tempP4[0],tempP4[1]);

        x=parseDoubleOrInteger(coor.get(0).get(4).get(0));
        y=parseDoubleOrInteger(coor.get(0).get(4).get(1));
        double[] tempP5 = Coordinate.web2Nav(x, y, site.getScale());
        String p5 = String.format("%.2f,%.2f",tempP5[0],tempP5[1]);
        System.out.println("points="+p1+","+p2+","+p3+","+p4+","+p5);
        String notes =(String) params.get("notes");
        String points=p1+","+p2+","+p3+","+p4;
        String addr = (String) params.get("addr");
        ChargeRoom chargeRoom = new ChargeRoom();
        Random r = new Random();
        Integer temp=r.nextInt(100000);
        String code=temp.toString();
        chargeRoom.setName(name);
        chargeRoom.setSiteId(siteId);
        chargeRoom.setCode(code);
        chargeRoom.setStatus(0);
        chargeRoom.setDescription(notes);
        chargeRoom.setCorners(points);
        chargeRoom.setAddr(addr);

        boolean checkName=mapService.checkChargeRoomName(siteId);
        if(!checkName){
            return ResultUtil.build(2,"请先删除充电房区域，再添加",null);
        }
        int ret=mapService.addChargeRoom(chargeRoom);
        if (ret>0 ) {
            DataCache.reload();
            return ResultUtil.build(0,"添加充电房区域成功",null);
        } else {
            return ResultUtil.build(1,"添加充电房区域失败",null);
        }

    }
    @ResponseBody
    @GetMapping("/getMaintainOrobstacle")
    public Result getMaintainOrobstacle(HttpServletRequest request) {
        String siteId=getRequestSiteId(request);
        Site site = siteMapper.findByUid(siteId);
        List<Map> mapMaintainOrobstacle = mapService.getMaintainOrobstacle(siteId);
        if(mapMaintainOrobstacle!=null && mapMaintainOrobstacle.size()>0){
            for(Map map:mapMaintainOrobstacle){
                String p1 = (String)map.get("point1");
                String[] temp1=p1.split(",");
                double[] dp1=Coordinate.nav2Web(Double.parseDouble(temp1[0]),Double.parseDouble(temp1[1]), site.getScale());
                String p2 = (String)map.get("point2");
                String[] temp2=p2.split(",");
                double[] dp2=Coordinate.nav2Web(Double.parseDouble(temp2[0]),Double.parseDouble(temp2[1]), site.getScale());
                String p3 = (String)map.get("point3");
                String[] temp3=p3.split(",");
                double[] dp3=Coordinate.nav2Web(Double.parseDouble(temp3[0]),Double.parseDouble(temp3[1]), site.getScale());
                String p4 = (String)map.get("point4");
                String[] temp4=p4.split(",");
                double[] dp4=Coordinate.nav2Web(Double.parseDouble(temp4[0]),Double.parseDouble(temp4[1]), site.getScale());
                String p5 = (String)map.get("point5");
                String[] temp5=p5.split(",");
                double[] dp5=Coordinate.nav2Web(Double.parseDouble(temp5[0]),Double.parseDouble(temp5[1]), site.getScale());
                Integer maintainType =(Integer)map.get("maintain_type");
                if (maintainType ==0){
                    map.put("lineColor","#f9e600");
                    map.put("fillColor","#f9e600");
                }else{
                    map.put("lineColor","#FF0000");
                    map.put("fillColor","#FF0000");
                }
                List coor = new ArrayList();
                coor.add(dp1);
                coor.add(dp2);
                coor.add(dp3);
                coor.add(dp4);
                coor.add(dp5);
                map.put("coor",coor);
            }
        }
        return ResultUtil.success(mapMaintainOrobstacle);
    }
    @ResponseBody
    @GetMapping("/getChargeRoom")
    public Result getChargeRoom(HttpServletRequest request) {
        String siteId=getRequestSiteId(request);
        Site site = siteMapper.findByUid(siteId);
        List<Map> chargeRooms = mapService.getChargeRoom(siteId);
        if(chargeRooms!=null && chargeRooms.size()>0){
            for(Map map:chargeRooms){
                String points = (String) map.get("corners");
                String[] tempPoint =points.split(",");
                double[] dp1=Coordinate.nav2Web(Double.parseDouble(tempPoint[0]),Double.parseDouble(tempPoint[1]), site.getScale());
                double[] dp2=Coordinate.nav2Web(Double.parseDouble(tempPoint[2]),Double.parseDouble(tempPoint[3]), site.getScale());
                double[] dp3=Coordinate.nav2Web(Double.parseDouble(tempPoint[4]),Double.parseDouble(tempPoint[5]), site.getScale());
                double[] dp4=Coordinate.nav2Web(Double.parseDouble(tempPoint[6]),Double.parseDouble(tempPoint[7]), site.getScale());
                double[] dp5=Coordinate.nav2Web(Double.parseDouble(tempPoint[0]),Double.parseDouble(tempPoint[1]), site.getScale());
                map.put("lineColor","#25F92B");
                map.put("fillColor","#172DF9");
                List coor = new ArrayList();
                coor.add(dp1);
                coor.add(dp2);
                coor.add(dp3);
                coor.add(dp4);
                coor.add(dp5);
                map.put("coor",coor);
            }
        }
        return ResultUtil.success(chargeRooms);
    }

    @ResponseBody
    @PostMapping(value = "/deleteChargeRoom")
    public Result deleteChargeRoom(@RequestBody Map params,HttpServletRequest request) {
        int result = mapService.deleteChargeRoom((String) params.get("uid"));
        if (result ==1) {
            DataCache.reload();
            return ResultUtil.build(0,"成功删除区域",null);
        } else if(result ==-3){
            return ResultUtil.build(2,"固定区域无法删除",null);
        }else{
            return ResultUtil.build(1,"删除区域失败",null);
        }
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

    @ResponseBody
    @PostMapping(value = "/deleteMaintainOrobstacle")
    public Result deleteMaintainOrobstacle(@RequestBody Map params,HttpServletRequest request) {
        String robotId=this.getRobotId(request);
        int result = mapService.deleteMaintainOrobstacle((String) params.get("uid"),robotId);
        if (result ==1) {
            DataCache.reload();
            MemRobot memRobot=MemUtil.queryRobotById(robotId);
            taskService.syncMapData(memRobot);
            return ResultUtil.build(0,"成功删除区域",null);
        } else{
            return ResultUtil.build(1,"删除区域失败",null);
        }
    }

    @ResponseBody
    @GetMapping(value = "/getAreaList")
    public Result getAreaList(HttpServletRequest request){
        String siteId = getRequestSiteId(request);
        Site site = siteMapper.findByUid(siteId);
        List<Map> list=new ArrayList<>();
        List<Area> areaList = areaMapper.selectAll(siteId);
        for(Area item:areaList) {
            Map map = new HashMap();
            if (item.getParams() != null && !"".equals(item.getParams())) {
                map.put("uid", item.getUid());
                map.put("name", item.getName());
                map.put("siteId", item.getSiteId());
                map.put("params", item.getParams());
                list.add(map);
            }
        }
        if(list!=null && list.size()>0){
            Iterator<Map> it = list.iterator();
            while (it.hasNext()){
                Map map=it.next();
                String p1 = (String)map.get("params");
                String[] temp=p1.split(",");
                if(temp.length!=10){
                    it.remove();
                    continue;
                }
                /*String devTypeID =(String)map.get("dev_type_id");
                //wait todo
                if(devTypeID.equals("2")){
                    map.put("lineColor","#cc6100");
                    double[] dp5=Coordinate.nav2Web(Double.parseDouble(temp[8]),Double.parseDouble(temp[9]),site.getScale());
                    String str = String.format("(%s%s%s%s",dp5[0],",",dp5[1],")");
                    map.put("zPoint",str);
                    map.put("zIndex","6");
                }else if(devTypeID.equals("3")){
                    map.put("lineColor","#13c5cc");
                    map.put("zIndex","16");

                } else{
                    ////todo confirm
                    map.put("lineColor","#13c5cc");
                    map.put("zIndex","16");
                }*/
                map.put("lineColor","#13c5cc");
                map.put("zIndex","16");
                double[] dp1=Coordinate.nav2Web(Double.parseDouble(temp[0]),Double.parseDouble(temp[1]),site.getScale());
                double[] dp2=Coordinate.nav2Web(Double.parseDouble(temp[2]),Double.parseDouble(temp[3]),site.getScale());
                double[] dp3=Coordinate.nav2Web(Double.parseDouble(temp[4]),Double.parseDouble(temp[5]),site.getScale());
                double[] dp4=Coordinate.nav2Web(Double.parseDouble(temp[6]),Double.parseDouble(temp[7]),site.getScale());
                double[] dp5=Coordinate.nav2Web(Double.parseDouble(temp[8]),Double.parseDouble(temp[9]),site.getScale());
                List coor = new ArrayList();
                coor.add(dp1);
                coor.add(dp2);
                coor.add(dp3);
                coor.add(dp4);
                coor.add(dp5);
                map.put("coor",coor);
            }
         }
        return ResultUtil.success(list);

    }


    @ResponseBody
    @PostMapping(value = "/getNavPos")
    public Result getNavPos(@RequestBody Map params, HttpServletRequest request) {
        String siteId = getRequestSiteId(request);
        Site site = siteMapper.findByUid(siteId);
        double x=parseDoubleOrInteger(params.get("x"));
        double y=parseDoubleOrInteger(params.get("y"));
        double pos[]=Coordinate.web2Nav(x,y,site.getScale());
        return ResultUtil.build(0,"succ",pos);
    }


    @ResponseBody
    @GetMapping(value = "/queryPtzSetByMark/{markId}")
    public Result queryPtzSetByMark(@PathVariable String markId) {
        List<PtzSet> ptzSetList=ptzSetMapper.findPtzSetListByMarkId(markId);
        if(ptzSetList ==null ){
            return ResultUtil.build(-1,"没有查询到数据",null);
        }
        for(PtzSet item:ptzSetList){
            if(item.getPtzType()!=4){
               return ResultUtil.build(0,"",item);
            }
        }
        return ResultUtil.build(-1,"没有查询到数据",null);
    }

    /**
     * 地图编辑增加 地标点或者修改地标点，传入坐标为导航坐标.
     * 修改时不能修改类型
     * @param params
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/saveRunMark")
    public Result updateRunMark(@RequestBody Map params,HttpServletRequest request) {
        String uid=(String)params.get("uid");
        String siteId=getRequestSiteId(request);
        String robotId=getRobotId(request);
        MemRobot memRobot= MemUtil.queryRobotById(robotId);
        if(robotId==null || memRobot==null ){
            return ResultUtil.build(1,"操作失败，机器人未连接",null);
        }
        String name=(String)params.get("name");
        double x=parseDoubleOrInteger(params.get("x"));
        double y=parseDoubleOrInteger(params.get("y"));
        int ptzType=Integer.parseInt(params.get("ptzType").toString());
        int moveStyle=Integer.parseInt(params.get("moveStyle").toString());
        float robotAngle=memRobot.getRobotInfo().getOrientation();
        if(uid==null){
            Result result=mapService.addRunMark(siteId,x,y,name,ptzType,robotAngle,moveStyle);
            DataCache.reload();
            return result;
        }
        //以下为修改
        RunMark runMark=runMarkMapper.select(uid);
        if(runMark==null){
            return ResultUtil.build(1,"更新地标点失败，无此地标点。",null);
        }
        RunMark sameRunMark = RunMarkUtil.findTooCloseRunMark(x,y,siteId,uid);
        if(sameRunMark!=null){
            return ResultUtil.build(-1,"更新地标点失败，已经存在一个很近的地标点。",null);
        }
        //检查名称重复
        List<RunMark> runMarkList = DataCache.getRunMarkList(siteId);
        if(runMarkList==null){
            return null;
        }
        for (RunMark exitRunMark : runMarkList) {
            if(exitRunMark.getMarkName().equals(name) && !exitRunMark.getUid().equals(uid)){
                return ResultUtil.build(-2,"更新地标点失败，已经存在这个名称的地标点，请修改名称。",null);
            }
        }
        int oldPtzType=0;
        List<PtzSet> ptzSetList=ptzSetMapper.findPtzSetListByMarkId(runMark.getUid());
        for(PtzSet item:ptzSetList){
            if(item.getPtzType()!=4){
                oldPtzType=item.getPtzType();
                break;
            }
        }
        if(oldPtzType!=ptzType){
            return ResultUtil.build(-1,"更新地标点失败,不能修改类型",null);
        }
        runMark.setLon(x);
        runMark.setLat(y);
        runMark.setMarkName(name);
        runMark.setMoveStyle(moveStyle);
        int result = runMarkMapper.update(runMark);
        if (result!=1) {
            return ResultUtil.build(-1,"更新地标点失败",null);
        } else {
            DataCache.reload();
            return ResultUtil.build(0,"更新地标点成功",null);
        }
    }

    /**
     * 地图编辑 选中两个点，n等分点坐标作为插入点
     * @param params
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/batchAddRunMark")
    public Result batchAddRunMark(@RequestBody Map params,HttpServletRequest request) {
        String siteId=getRequestSiteId(request);
        int ptzType =Integer.parseInt((String)params.get("ptzType"));
        String markId1 = (String)params.get("uid1");
        String markId2 = (String)params.get("uid2");
        int n =Integer.parseInt((String)params.get("n"));
        if (n<1) {
            return ResultUtil.build(-1,"至少要插入1个点",null);
        }
        int result = mapService.batchAppRunMark(markId1, markId2, n, ptzType, siteId);
        if (result >0) {
            DataCache.reload();
            return ResultUtil.build(0,"总共"+n+"个"+"成功添加"+result+"个",null);
        } else {
            return ResultUtil.build(1,"添加失败",null);
        }
    }


    @ResponseBody
    @PostMapping(value = "/manualMove")
    public Result manualMove(@RequestBody Map params,HttpServletRequest request) {
        String robotId=getRobotId(request);
        String siteId = getRequestSiteId(request);
        Site site = siteMapper.findByUid(siteId);
        double x=Double.parseDouble(params.get("fx").toString());
        double y=Double.parseDouble(params.get("fy").toString());
        double[] finialP = Coordinate.web2Nav(x, y,site.getScale());
        int ret=0;
        double xFrom=Double.parseDouble(params.get("xFrom").toString());
        double yFrom=Double.parseDouble(params.get("yFrom").toString());
        ret= MoveByNav.directMove(robotId,finialP[0],finialP[1],xFrom,yFrom);
        if(ret<0){
            return ResultUtil.build(-1,"发送指令失败",null);
        }else{
            return ResultUtil.success();
        }
    }

    @ResponseBody
    @PostMapping(value = "/rotateRobot")
    public Result rotateRobot(@RequestBody Map params,HttpServletRequest request) {
        String robotId=getRobotId(request);
        double angle=Double.parseDouble(params.get("angle").toString());
        int ret=0;
        ret=MoveByNav.directRotate(robotId,angle);
        if(ret<0){
            return ResultUtil.build(-1,"发送指令失败",null);
        }else{
            return ResultUtil.success();
        }
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



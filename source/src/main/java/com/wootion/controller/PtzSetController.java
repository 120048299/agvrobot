package com.wootion.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wootion.agvrobot.session.SessionRobot;
import com.wootion.agvrobot.utils.CommonTree;
import com.wootion.agvrobot.utils.FileUtil;
import com.wootion.agvrobot.utils.Tree;
import com.wootion.commons.*;
import com.wootion.mapper.PtzSetMapper;
import com.wootion.model.*;
import com.wootion.robot.MemRobot;
import com.wootion.robot.MemUtil;
import com.wootion.service.IPtzsetService;
import com.wootion.service.ITaskService;
import com.wootion.task.EventQueue;
import com.wootion.task.event.AdjustTerraceEvent;
import com.wootion.utiles.DataCache;
import com.wootion.utiles.ResultUtil;
import com.wootion.utiles.SessionManager;
import com.wootion.utiles.poi.ExportExcel;
import com.wootion.utiles.poi.RowStyle;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("req_svr/ptzset")
public class PtzSetController {

    @Autowired
    IPtzsetService ptzsetService;

    @Autowired
    ITaskService taskService;

    @Autowired
    PtzSetMapper ptzSetMapper;

    Logger logger = LoggerFactory.getLogger(LoginController.class);

    /**
     * 添加巡检点 ：  把标准点位添加到设备上。 (不带位置和机器人参数）
     * 一次添加一个设备，多个点位
     * @param params
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/savePtzSet")
    public Result savePtzSet(@RequestBody Map params, HttpServletRequest request) {
        Site site=getRequestSite(request);
        String robotId=getRobotId(request);
        String uid= (String)params.get("uid");
        String areaId= (String)params.get("areaId");
        String description=(String)params.get("description");
        int scan=Integer.parseInt( params.get("scan").toString());
        int status=Integer.parseInt( params.get("status").toString());
        int ret=0;
        if(uid==null ||"".equals(uid)) {
            Result result = ptzsetService.addPtzSet(site.getUid(),robotId,areaId,description,  scan,status);
            if(result.getCode()==1){
                PtzSet ptzSet=(PtzSet)result.getData();
                uid=ptzSet.getUid();
                ret=1;
            }else{
                return result;
            }
        }else{
            //修改
            ret = ptzsetService.updatePtzSet(uid,site.getUid(), robotId,areaId,description,scan,status);
        }
        if(ret<0){
            if(ret==-2){
                return ResultUtil.failed("保存失败:此设备下已经存在此名称的巡检点位，请输入其他名称。");
            }else{
                return ResultUtil.failed("保存失败");
            }
        }else{
            DataCache.reload();
            return ResultUtil.build(0,"保存成功",null);
        }
    }

    //todo wait
    @ResponseBody
    @PostMapping("/getPtzListByDevId")
    public PageInfo  getPtzListByDevId(@RequestBody Map params,HttpServletRequest request) {
      /*  String devId = (String) params.get("devId");
        int pageNum = (Integer) params.get("pageNum");
        int pageSize = (Integer) params.get("pageSize");
        String searchText = (String) params.get("searchText");
        PageInfo pageInfo=ptzsetService.getPtzListByDevId(pageNum, pageSize, devId);
        List<Map> list=pageInfo.getList();
        if(list!=null){
            for(Map map:list){
                Integer opsType=(Integer)map.get("ops_type");
                map.put("opsTypeName", OPS_TYPE.fromInt(opsType.intValue()).toStrValue());
                if(opsType==1){
                    Integer meterType=(Integer)map.get("meter_type");
                    map.put("meterTypeName", METER_TYPE.fromInt(meterType.intValue()).toStrValue());
                }
                RegzObject regzObject=DataCache.findRegzObject((String)map.get("regz_object_id"));
                map.put("regzObjectName",regzObject.getName());
                RegzObjectType regzObjectType=DataCache.findRegzObjectType((String)map.get("regz_object_type"));
                map.put("regzObjectTypeName",regzObjectType.getName());
                Integer status=(Integer)map.get("status");
                if(status==0){
                    map.put("statusText", "禁用");
                }else{
                    map.put("statusText", "启用");
                }
                Integer setted=(Integer)map.get("setted");
                if(setted==0){
                    map.put("settedText", "未设置");
                }else{
                    map.put("settedText", "已设置");
                }
            }
        }
        return  pageInfo;*/
      return null;
    }

    @ResponseBody
    @GetMapping("deletePtzSet/{ptzsetIds}")
    public Result deletePtzSet(@PathVariable List<String> ptzsetIds) {
        ptzsetService.deletePtzSet(ptzsetIds);
        DataCache.reload();
        return ResultUtil.success();
    }


    /**
     * 为了过滤。如果没有筛选条件则，返回空
     * 区域 大设备类型 识别类型 三者为必须项目，and关系。
     * 表计类型选择 从属于 识别类型==表计;
     * 外观类型选择 重属于 大设备类型==辅助设备,FZSB
     * @param params
     * @param request
     * @return
     */
    @ApiOperation(value = "查询ptz列表）",notes = " 根据任务编制页面 上方选项 发起的查询条件，查询满足条件的ptzId;or 根据任务id查询。" )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "areaList",value = "地区id数组", required = false, dataType = "String[]"),
            @ApiImplicitParam(name = "bigDevTypeList",value = "设备大类id数组", required = false, dataType = "String[]"),
            @ApiImplicitParam(name = "opsTypeList",value = "识别类型数组", required = false, dataType = "int[]"),
            @ApiImplicitParam(name = "meterList",value = "表计类型数组", required = false, dataType = "String[]"),
            //@ApiImplicitParam(name = "spotName",value = "点位名称,模糊查询", required = false, dataType = "String"),
            @ApiImplicitParam(name = "taskId",value = "任务Id", required = false, dataType = "String"),
    })
    @ResponseBody
    @PostMapping("/queryPtzList")
    public List<Map> getPtzList(@RequestBody Map params,HttpServletRequest request){
      /*  String siteId=getRequestSiteId(request);
        params.put("siteId",siteId);
        String taskTypeId=(String)params.get("taskTypeId");
        //如果传入了taskId,则查询task对应的点位
        if(params.get("taskId")!=null){
            List<Map> list=ptzsetService.queryPtzList(params);
            if(list==null ||list.size()==0){
                return new ArrayList<>();
            }
            return list;
        }

        //主条件查询
        List listMain = queryMain(params);
        //普通查询结束
        if(null==taskTypeId || taskTypeId.equals("1") || taskTypeId.equals("2") || taskTypeId.startsWith("3") || taskTypeId.startsWith("5") ) {
            return listMain;
        }

        if(listMain==null){
            listMain= new ArrayList<>();
        }

        //以下处理特巡的附加条件 ，这些条件没有显示在界面上
        List<Map> listAdition; //附加查询结果
        //特殊巡检的 缺陷跟踪巡检
        // 告警条件 和主条件 并列 关系 .
        // 设置一个告警级别，筛选出大于等于这个级别的（全站范围），合并普通条件结果
        if(taskTypeId.equals("42") ) {
            TaskCondition taskCondition = taskService.selectTaskCondition("42", 1, 6, siteId);
            if (taskCondition == null) {
                return null;
            }
            Map newParams = new HashMap();
            newParams.put("alarmLevel", Integer.valueOf(taskCondition.getSelectedValue()));
            newParams.put("siteId", siteId);
            listAdition = ptzsetService.queryPtzList(newParams);
        } else if (taskTypeId.equals("43") || taskTypeId.equals("44") || taskTypeId.equals("45") || taskTypeId.equals("46") ) {
            TaskCondition taskCondition = taskService.selectTaskCondition(taskTypeId, 1, 6, siteId);
            if (taskCondition == null) {
                return null;
            }
            Map newParams = new HashMap();
            newParams.put("extId", taskCondition.getSelectedValue());
            listAdition = ptzsetService.queryPtzList(newParams);
        }else{//特殊条件
            listAdition = ptzsetService.querySpecialTaskPoint(taskTypeId,siteId);
        }
        //合并listAll 和 listAdition
        ListUtil.addListItem(listMain, listAdition);
        return listMain;*/
        return null;
    }

    private  List<Map>  queryMain(Map params){
        //以下处理编辑页面上方查询条件传来的选择项目  按任务类型查
        String taskTypeId=(String)params.get("taskTypeId");
        if(params.get("areaList")!=null ) {
            List list = (List) params.get("areaList");
            if (list.size() == 0) {
                //areas 全无，则不查询选中项 .area是必要必要条件
                return new ArrayList<>();
            }
        }
        if(params.get("areaList")==null){
            return new ArrayList<>();
        }

        if(params.get("devTypeList")!=null ){
            //大设备类型 是必要必要条件
            List list=(List)params.get("devTypeList");
            if(list.size()==0){
                return new ArrayList<>();
            }
        }
        if(params.get("devTypeList")==null){
            return new ArrayList<>();
        }

        //都按传入的枚举查询。必要条件
        if(params.get("opsTypeList")!=null ){
            //识别类型也是必要条件
            List list=(List)params.get("opsTypeList");
            if(list.size()==0){
                return new ArrayList<>();
            }
        }
        if(params.get("opsTypeList")==null){
            return new ArrayList<>();
        }

        //处理表计类型
        if(params.get("opsTypeList")!=null ){
            List opsList=(List)params.get("opsTypeList");
            if(opsList.indexOf("1")>=0){
                //表计类型 只有 识别类型为1时才有意义
                boolean selectAll=(boolean)params.get("meterTypeAll");
                if(selectAll) {
                    params.remove("meterTypeList");
                }else{
                    if(params.get("meterTypeList")!=null ){
                        List list=(List)params.get("meterTypeList");
                        if(list.size()==0){
                            list.add("0");//让查不到,配合sql语句
                        }
                    }else{
                        List<String> list=new ArrayList();
                        list.add("0");//让查不到,配合sql语句
                        params.put("meterTypeList",list);
                    }
                }
            }else{//如果没有选中识别类型的表计，则meterTypeList无效
                params.remove("meterTypeList");
            }
        }

        //处理外观类型
        if(params.get("devTypeList")!=null ){
            List devTypeList=(List)params.get("devTypeList");
            if(devTypeList.indexOf("FZSB")>=0){
                if(params.get("outLookTypeAll")!=null){
                    boolean  selectAll=(boolean)params.get("outLookTypeAll");
                    if(selectAll) {
                        params.remove("outLookTypeList");
                    }else{
                        if(params.get("outLookTypeList")!=null ){
                            List list=(List)params.get("outLookTypeList");
                            if(list.size()==0){
                                list.add("0");//让查不到,配合sql语句
                            }
                        }else{
                            List<String> list=new ArrayList();
                            list.add("0");//让查不到,配合sql语句
                            params.put("outLookTypeList",list);
                        }
                    }
                }
            }else{//如果没有选中识别类型的表计，则meterTypeList无效
                params.remove("outLookTypeList");
            }
        }

        //全选,为了提高查询效率
        boolean selectAll=(boolean)params.get("areaAll");
        if(selectAll ) {
            params.remove("areaList");
        }
        //全选,为了提高查询效率
        selectAll=(boolean)params.get("devTypeAll");
        if(selectAll) {
            params.remove("devTypeList");
        }
        //全选,为了提高查询效率
        selectAll=(boolean)params.get("opsTypeAll");
        if(selectAll) {
            params.remove("opsTypeList");
        }

        //只有site条件和taskTypeId，筛选条件一个都没有，则返回空
        if(params.size()==2){
            return new ArrayList<>();
        }
        List<Map> list=ptzsetService.queryPtzList(params);
        if(list==null ||list.size()==0){
            return new ArrayList<>();
        }
        return list;

    }


    /**
     *  完全树，或者名称筛选
      * @param params
     * @param request
     * @return
     */
    @ApiOperation(value = "PTZSET树",notes = " 完整的或者部分 params:点位名称" )
    @ResponseBody
    @PostMapping("/getPtzTree")
    public CommonTree<Node> getPtzTree(@RequestBody Map params,HttpServletRequest request){
        String siteId=getRequestSiteId(request);
        params.put("siteId",siteId);
        Object searchTextObj=params.get("searchText");
        if(searchTextObj!=null){
            String searchText=(String)searchTextObj;
            if(searchText.equals("")){
                params.remove("searchText");
            }
        }
        List<Map> list=ptzsetService.getPtzListForTree(params);
        if(list==null ||list.size()==0){
            return null;
        }

        Site site=getRequestSite(request);

        Node root = new Node();
        root.setId(site.getUid());
        root.setParentId("");
        root.setName(site.getName());
        root.setAlarmLevel(new Integer(0));
        CommonTree<Node> treeData= new CommonTree<Node>(root);


        for(int i=0;i<list.size();i++) {
            Map item=list.get(i);
            CommonTree<Node>  area=treeData.getChild((String)item.get("areaId"));
            if(area==null) {
                Node areaNode = new Node();
                areaNode.setId((String)item.get("areaId"));
                areaNode.setParentId(root.getId());
                areaNode.setName((String)item.get("areaName"));
                area = new CommonTree<Node>(areaNode);
                treeData.addTreeNode(area);
            }
            CommonTree<Node>  bay=area.getChild((String)item.get("bayId"));
            if(bay==null) {
                Node bayNode = new Node();
                bayNode.setId((String)item.get("bayId"));
                bayNode.setParentId(area.getId());
                bayNode.setName((String)item.get("bayName"));
                bay = new CommonTree<Node>(bayNode);
                area.addChild(bay);
            }
            CommonTree<Node>  bigDevType=bay.getChild((String)item.get("bigDevTypeId"));
            if(bigDevType==null) {
                Node bigDevNode = new Node();
                bigDevNode.setId((String)item.get("bigDevTypeId"));
                bigDevNode.setParentId(bay.getId());
                bigDevNode.setName((String)item.get("bigDevTypeName"));
                bigDevType = new CommonTree<Node>(bigDevNode);
                bay.addChild(bigDevType);
            }


            CommonTree<Node>  bigDev=bigDevType.getChild((String)item.get("bigDevId"));
            if(bigDev==null) {
                Node dev = new Node();
                dev.setId((String)item.get("bigDevId"));
                dev.setParentId(bigDevType.getId());
                dev.setName((String)item.get("bigDevName"));
                bigDev = new CommonTree<Node>(dev);
                bigDevType.addChild(bigDev);
            }

            //ptz list
            Node ptz = new Node();
            ptz.setId((String)item.get("uid"));
            ptz.setParentId(bigDev.getId());
            ptz.setName((String)item.get("description"));
            ptz.setData(item);//最后一层填入所有数据
            ptz.setNodeType("ptz");
            ptz.setAlarmLevel(((Integer)item.get("alarmLevel")).intValue());
            CommonTree<Node> ptzTree = new CommonTree<Node>(ptz);
            ptzTree.addValue(ptz.getId());
            bigDev.addChild(ptzTree);

            //以下处理上层的警告，以下层节点的最高告警级别为上层的告警级别
            int alarmLevel=((Integer)item.get("alarmLevel")).intValue();
            int oldLevel=bigDev.getData().getAlarmLevel();
            if(alarmLevel>oldLevel){
                bigDev.getData().setAlarmLevel(alarmLevel);
            }

            oldLevel=bigDevType.getData().getAlarmLevel();
            if(alarmLevel>oldLevel){
                bigDevType.getData().setAlarmLevel(alarmLevel);
            }

            oldLevel=bay.getData().getAlarmLevel();
            if(alarmLevel>oldLevel){
                bay.getData().setAlarmLevel(alarmLevel);
            }

            oldLevel=area.getData().getAlarmLevel();
            if(alarmLevel>oldLevel){
                area.getData().setAlarmLevel(alarmLevel);
            }

            oldLevel=treeData.getData().getAlarmLevel();
            if(alarmLevel>oldLevel){
                treeData.getData().setAlarmLevel(alarmLevel);
            }
            //为了前端显示增加
            bigDev.addValue(ptz.getId());
            bigDevType.addValue(ptz.getId());
            bay.addValue(ptz.getId());
            area.addValue(ptz.getId());
            treeData.addValue(ptz.getId());
        }

        return treeData;
    }



    /**
     *  完全树，或者名称筛选
     * @param params
     * @param request
     * @return
     */
    @ApiOperation(value = "PTZSET树",notes = " 完整的或者部分 params:点位名称" )
    @ResponseBody
    @PostMapping("/getPtzTree_bak")
    public Tree<Node> getPtzTree_bak(@RequestBody Map params,HttpServletRequest request){
        String siteId=getRequestSiteId(request);
        params.put("siteId",siteId);
        List<Map> list=ptzsetService.getPtzListForTree(params);
        if(list==null ||list.size()==0){
            return null;
        }
        Site site=getRequestSite(request);

        Tree<Node> root = new Tree<Node>();
        root.setId(site.getUid());
        root.setNodeType("1");
        root.setParentId("");
        root.setText(site.getName());
        root.setAttribute("alarmLevel",new Integer(0));

        for(int i=0;i<list.size();i++) {
            Map item=list.get(i);
            Tree<Node> area=root.getChild((String)item.get("areaId"));
            if(area==null) {
                Tree<Node> tree = new Tree<Node>();
                tree.setId((String)item.get("areaId"));
                tree.setParentId(root.getId());
                tree.setText((String)item.get("areaName"));
                tree.setAttribute("alarmLevel",new Integer(0));
                root.addChild(tree);
                area=tree;
            }

            Tree<Node> bigDevType=area.getChild((String)item.get("bigDevTypeId"));
            if(bigDevType==null) {
                Tree<Node> tree = new Tree<Node>();
                tree.setId((String)item.get("bigDevTypeId"));
                tree.setParentId(area.getId());
                tree.setText((String)item.get("bigDevTypeName"));
                tree.setAttribute("alarmLevel",new Integer(0));
                area.addChild(tree);
                bigDevType=tree;
            }

            Tree<Node> bigDev=bigDevType.getChild((String)item.get("bigDevId"));
            if(bigDev==null) {
                Tree<Node> tree = new Tree<Node>();
                tree.setId((String)item.get("bigDevId"));
                tree.setParentId(bigDevType.getId());
                tree.setText((String)item.get("bigDevName"));
                tree.setAttribute("alarmLevel",new Integer(0));
                bigDevType.addChild(tree);
                bigDev=tree;
            }

            //ptz list
            Tree<Node> tree = new Tree<Node>();
            tree.setId((String)item.get("uid"));

            tree.setParentId(bigDev.getId());
            tree.setText((String)item.get("regzSpotName"));
            tree.setAttributes(item);//把所有字段都传入
            tree.addValue(tree.getId());
            bigDev.addChild(tree);

            //以下处理上层的警告，以下层节点的最高告警级别为上层的告警级别
            int alarmLevel=((Integer)item.get("alarmLevel")).intValue();
            int oldLevel=((Integer)bigDev.getAttribute("alarmLevel")).intValue();
            if(alarmLevel>oldLevel){
                bigDev.setAttribute("alarmLevel",alarmLevel);
            }

            oldLevel=((Integer)bigDevType.getAttribute("alarmLevel")).intValue();
            if(alarmLevel>oldLevel){
                bigDevType.setAttribute("alarmLevel",alarmLevel);
            }

            oldLevel=((Integer)area.getAttribute("alarmLevel")).intValue();
            if(alarmLevel>oldLevel){
                area.setAttribute("alarmLevel",alarmLevel);
            }
            oldLevel=((Integer)root.getAttribute("alarmLevel")).intValue();
            if(alarmLevel>oldLevel){
                root.setAttribute("alarmLevel",alarmLevel);
            }

            bigDev.addValue(tree.getId());
            bigDevType.addValue(tree.getId());
            area.addValue(tree.getId());
            root.addValue(tree.getId());
        }

        return root;
    }




    /**
     * 巡检点管理页面查询多个巡检点详细信息 ArrayList  ids
     * @param params
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("/getPtzListByIds")
    public PageInfo  getPtzListByIds(@RequestBody Map params,HttpServletRequest request) {
        String siteId=getRequestSiteId(request);
        int pageNum = (Integer) params.get("pageNum");
        int pageSize = (Integer) params.get("pageSize");
        ArrayList<String> ids = (ArrayList<String>)params.get("ids");
        if(ids==null ){
            return null;
        }
        int queryStatus=Integer.parseInt(params.get("status").toString());
        PageHelper.startPage(pageNum,pageSize);
        List<Map> list=ptzSetMapper.selectPtzSetByIds(ids,queryStatus);
        PageInfo pageInfo=new PageInfo(list);
        if(list!=null){
            for(Map map:list){
                Dev dev = DataCache.findDev(siteId,(String)map.get("dev_id"));
                map.put("devName",dev.getName());
                DevType devType=DataCache.findDevType(dev.getDevTypeId());
                map.put("devTypeId",devType.getUid());
                map.put("devTypeName",devType.getName());
                Dev bay = DataCache.findDev(siteId,dev.getParentId());
                map.put("bayName",bay.getName());
                Dev area = DataCache.findDev(siteId,bay.getParentId());
                map.put("areaName",area.getName());

                map.put("devPath",area.getName()+"->"+bay.getName()+"->"+dev.getName());

                RegzSpot spot=DataCache.findRegzSpot((String)map.get("regz_spot_id"));
                Integer opsType=spot.getOpsType();
                map.put("opsTypeName", OPS_TYPE.fromInt(opsType.intValue()).toStrValue());
                map.put("regzSpotName",spot.getSpotName());
                if(opsType==1){
                    Integer meterType=spot.getMeterType();
                    map.put("meterTypeName", METER_TYPE.fromInt(meterType.intValue()).toStrValue());
                }

                Integer saveType = spot.getSaveType();
                String saveTypeName=SAVE_TYPE.fromInt(saveType).toStrValue();
                map.put("saveTypeName", saveTypeName);
                Integer heatType= spot.getHeatType();
                String heatTypeName="";
                if(heatType!=null){
                    heatTypeName = HEAT_TYPE.fromInt(heatType).toStrValue();
                }
                map.put("heatTypeName",heatTypeName);

                RegzObject regzObject=DataCache.findRegzObject((String)map.get("regz_object_id"));
                map.put("regzObjectName",regzObject.getName());
                RegzObjectType regzObjectType=DataCache.findRegzObjectType((String)map.get("regz_object_type"));
                map.put("regzObjectTypeName",regzObjectType.getName());

                Integer status=(Integer)map.get("status");
                if(status==0){
                    map.put("statusText", "禁用");
                }else{
                    map.put("statusText", "启用");
                }
                Integer setted=(Integer)map.get("setted");
                if(setted==0){
                    map.put("settedText", "未设置");
                }else{
                    map.put("settedText", "已设置");
                }

            }
        }
        return  pageInfo;
    }

    /**
     * 批量设置巡检点 状态 启用和禁用
     * ids：点位list
     * status：0,1
     */
    @ResponseBody
    @PostMapping(value = "/setPtzStatus")
    public Result setPtzStatus(@RequestBody Map params, HttpServletRequest request) {
        Site site=getRequestSite(request);
        ArrayList<String> ids = (ArrayList<String> )params.get("ids");
        int status =(int )params.get("status");
        int ret;
            ret = ptzsetService.setPtzSetStatus(ids,status);
        if(ret<0){
            return ResultUtil.failed("保存失败");
        }else{
            DataCache.reload();
            return ResultUtil.build(0,"保存成功",null);
        }
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
    private Site getRequestSite(HttpServletRequest request){
        String token=request.getHeader(Constans.TOKEN);
        Site site = (Site) SessionManager.getSessionEntity(token,Constans.SESSION_SITE);
        if(site==null){
            return null;
        }
        return site;
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


    /**
     * 根据鼠标点击位置调整云台角度
     * @param params
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/adjustTerrace")
    public Result adjustTerrace(@RequestBody Map params, HttpServletRequest request) {
        String robotIp=getRobotIp(request);
        if(robotIp==null){
            return ResultUtil.failed("没有选择机器人");
        }
        AdjustTerraceEvent adjustTerraceEvent=new AdjustTerraceEvent(robotIp);
        double diffX=Double.parseDouble(params.get("diffX").toString());
        double diffY=Double.parseDouble(params.get("diffY").toString());
        adjustTerraceEvent.setDiffX((int)Math.round(diffX));
        adjustTerraceEvent.setDiffY((int)Math.round(diffY));
        EventQueue.addTask(adjustTerraceEvent);
        return ResultUtil.success();
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



class Node {

    private String id;
    private String parentId;
    private String name;
    private String nodeType;
    private int alarmLevel=0;
    private Object data;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public int getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(int alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Node() {
        super();
    }

    @Override
    public String toString() {
        return "Node{" +
                "id='" + id + '\'' +
                ", parentId='" + parentId + '\'' +
                ", name='" + name + '\'' +
                ", nodeType='" + nodeType + '\'' +
                ", alarmLevel=" + alarmLevel +
                ", data=" + data +
                '}';
    }
}

package com.wootion.service.impl;


import com.wootion.Debug;
import com.wootion.agvrobot.utils.FileUtil;
import com.wootion.alg.Point;
import com.wootion.commons.Result;
import com.wootion.dao.IDao;
import com.wootion.dao.ITaskDao;
import com.wootion.mapper.*;
import com.wootion.model.*;
import com.wootion.protocols.robot.GeneralPublish;
import com.wootion.protocols.robot.msg.MsgNames;
import com.wootion.robot.MemRobot;
import com.wootion.robot.MemUtil;
import com.wootion.service.IMapService;
import com.wootion.utiles.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("mapService ")
public class MapService implements IMapService {
    private static final Logger logger = LoggerFactory.getLogger(MapService.class);

    @Autowired
    private MaintainAreaMapper maintainAreaMapper;
    @Autowired
    private RunMarkMapper runMarkMapper;
    @Autowired
    RunLineMapper runLineMapper;
    @Autowired
    private PtzSetMapper ptzSetMapper;
    @Autowired
    private DevMapper devMapper;
    @Autowired
    private ChargeRoomMapper chargeRoomMapper;

    @Override
    public List<RunMark> getRunMarkListBySiteId(String siteId) {
        List<RunMark> runMarkList = runMarkMapper.getRunMarkBySiteId(siteId);
        for (RunMark runMark : runMarkList) {
            List<PtzSet> ptzSets = DataCache.findPtzSetByRunMark(siteId, runMark.getUid());
            RunMark validRunMark = DataCache.getFiltedRunMark(siteId, runMark.getUid());
            runMark.setPtzSetList(ptzSets);
            runMark.setExistRoute(validRunMark!=null);
        }
        return runMarkList;
    }

  @Override
    public boolean addMapRunLine(ArrayList<String> runMarkIdList, String siteId) {
        if (runMarkIdList.size()<2) {
            return false;
        }
        // 获取所有点
        ArrayList<RunMark> runMarkList=new ArrayList<>();
        int [] index = new int[runMarkIdList.size()];
        for (int i=0; i<runMarkIdList.size(); i++) {
            runMarkList.add(DataCache.findRunMark(siteId,runMarkIdList.get(i)));
            index[i] = 0; // 初始为未使用
        }
        // 取到距离最大的两个点作为端点, 从任意一点开始，找最邻近的点画线
        int[] endPoint = findTwoEndPointPos(runMarkList);
        int p1 = endPoint[0];
        while (p1 != -1) {
            index[p1] = 1;  // 标记为已使用
            int p2 = findNearbyPointPos(index, runMarkList, p1);
            if (p2 != -1) {
                addMapRunLine(runMarkList.get(p1).getUid(), runMarkList.get(p2).getUid(), siteId);
            } else {
                break; // 结束循环
            }
            p1 = p2;
        }
        return true;
    }

    @Override
    public boolean addMapRunLine(String mark_id1, String mark_id2, String siteId) {
        RunLine runLine = DataCache.findFiltedRunLine(siteId, mark_id1, mark_id2);
        if (runLine!=null) {
            return true; // 存在则直接返回成功
        }
        Integer maxLineID = runLineMapper.maxRunLineID(siteId);
        runLine= new RunLine();
        runLine.setSiteId(siteId);
        runLine.setLineName(maxLineID+1+"号线");
        runLine.setLineId(maxLineID+1);
        runLine.setStatus(1);
        runLine.setMarkId1(mark_id1);
        runLine.setMarkId2(mark_id2);
        runLine.setMaxVel(0.500f);
        int ret =runLineMapper.insert(runLine);
        if (ret == 1) {
            DataCache.reload();
            return true;
        }
        return false;
    }

    @Override
    public int deleteMapRunLine(List<Map> lineList, String siteId) {
        int result =0;
        for( int i = 0 ; i < lineList.size() ; i++) {
            if (deleteMapRunLine((String) lineList.get(i).get("uid1"), (String)lineList.get(i).get("uid2"), siteId)) {
                result +=1;
            }
        }
        return result;
    }

    @Override
    public boolean deleteMapRunLine(String markId1, String markId2, String siteId) {
        int r = runLineMapper.deleteByTwoMark(markId1,markId2);
        if (r > 0) {
            DataCache.reload();
            return true;
        }else
            return false;

    }

    @Override
    public int batchAppRunMark(String mark_id1, String mark_id2, int n, int ptzType, String siteId) {
          RunMark runMark1 = DataCache.findRunMark(siteId, mark_id1);
          RunMark runMark2 = DataCache.findRunMark(siteId, mark_id2);
          if (runMark1==null || runMark2==null) {
              return 0;
          }
        double x1=runMark1.getLon();
        double y1=runMark1.getLat();
        double x2=runMark2.getLon();
        double y2=runMark2.getLat();
        String name = runMark1.getMarkName()+"-"+runMark2.getMarkName();
        double dx = (x2-x1)/(n+1);
        double dy = (y2-y1)/(n+1);
        int count = 0;
        for (int i=0; i<n; i++) {
            double x = x1+ dx*(i+1);
            double y = y1+ dy*(i+1);
            Result ret = addRunMark(siteId,x,y,name+"-"+(i+1),ptzType,0,0);
            if (ret.getCode()==0) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int deleteMapRunMark(List<String> markIdList) {
        int result =0;
        for( int i = 0 ; i < markIdList.size() ; i++) {
            String markId = markIdList.get(i);
            List<PtzSet> ptzSetList = ptzSetMapper.findPtzSetListByMarkId( markId);
            if(ptzSetList != null){
                boolean flag=true;
                for (PtzSet ptzSet:ptzSetList) {
                    if (ptzSet.getPtzType()==4){
                        //有监测点不允许删除run_mark
                        flag=false;
                        break;
                    }
                }
                if(flag){
                    runLineMapper.deleteByMark(markId);
                    //删除ptz (地标点，原点，充点电 都允许删除)
                    ptzSetMapper.deleteByMarkId(markId);
                    runMarkMapper.delete(markId);
                    result +=1;
                }
            }
        }
//        if (result>0){
//            return true;
//        }else
        return result;
    }
/*

    @Override
    public boolean updateDevParams(String devId, String params, String siteId) {
        int r = devMapper.updateDevParams(devId,params,siteId);
        if (r > 0) {
            DataCache.reload();
            return true;
        }else
            return false;

    }
*/

    @Override
    @Transactional
    public int addMaintainArea(MaintainArea maintainArea,String robotId){
        int r=maintainAreaMapper.insert(maintainArea);
        if(r!=1){
            return -1;
        }
        MemRobot  memRobot=MemUtil.queryRobot(robotId);
        String path =FileUtil.getUserHome()+"preset"+File.separator;
        String filename="virtual_obstacle_dynamic.txt";
        FileUtil fileUtil=new FileUtil();
        String UID =maintainArea.getUid();
        String[] temp1=maintainArea.getPoint1().split(",");
        double[] tempP1 = {Double.parseDouble(temp1[0]), Double.parseDouble(temp1[1])};
        String[] temp2=maintainArea.getPoint2().split(",");
        double[] tempP2 = {Double.parseDouble(temp2[0]), Double.parseDouble(temp2[1])};
        String[] temp3=maintainArea.getPoint3().split(",");
        double[] tempP3 = {Double.parseDouble(temp3[0]), Double.parseDouble(temp3[1])};
        String[] temp4=maintainArea.getPoint4().split(",");
        double[] tempP4 = {Double.parseDouble(temp4[0]), Double.parseDouble(temp4[1])};
        String[] temp5=maintainArea.getPoint5().split(",");
        double[] tempP5 = {Double.parseDouble(temp5[0]), Double.parseDouble(temp5[1])};
        String str=String.format("%s%s(%.4f%s%.4f%s(%.4f%s%.4f%s(%.4f%s%.4f%s(%.4f%s%.4f%s(%.4f%s%.4f%s",UID,":",tempP1[0],",",tempP1[1],")",tempP2[0],",",tempP2[1],")",tempP3[0],",",tempP3[1],")",tempP4[0],",",tempP4[1],")",tempP5[0],",",tempP5[1],")");
        try{
            fileUtil.addLine(path+filename,str);
            uploadVirturalFileDynamic(robotId,"virtual_obstacle_dynamic.txt");
            //不管成功失败
            sendReloadCommandToNav(robotId);
            DataCache.reload();
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
    }


    private boolean uploadVirturalFileDynamic(String robotId,String fileName){
        Robot robot= DataCache.getRobot(robotId);
        if(robot==null){
            return false;
        }
        boolean ret;
        if(Debug.haveRobotFtp==0){
            return false;
        }
        SFtpUtil sFtpUtil=new SFtpUtil();
        try{
            //ros:ros@10.204.157.230
            String ftpUrl=DataCache.findRobotParam(robotId,"ftpUrl");
            if(ftpUrl==null){
                ftpUrl="ros:ros@"+robot.getRobotIp();
            }
            String host=ftpUrl.substring(ftpUrl.indexOf("@")+1);
            String userName=ftpUrl.substring(0,ftpUrl.indexOf(":"));
            String password=ftpUrl.substring(ftpUrl.indexOf(":")+1,ftpUrl.indexOf("@"));
            int connected=sFtpUtil.connect(host,userName,password,22);
            if(connected!=1){
                return false;
            }
            ret=sFtpUtil.uploadFile("/opt/wootion_robot/share/customized_layer/data/","virtual_obstacle_dynamic.txt",
                    "/home/ros/preset/","virtual_obstacle_dynamic.txt");
            if(ret){
                return true;
            }else{
                return false;
            }
        }finally {
            sFtpUtil.disConnect();
        }
    }

    @Override
    @Transactional
    public int addChargeRoom(ChargeRoom chargeRoom){
        int r=chargeRoomMapper.insert(chargeRoom);
        if(r!=1){
            return -1;
        }
       return  r;
    }

    @Override
    public List<Map> getMaintainOrobstacle(String siteId) {
        return  maintainAreaMapper.getMaintainOrobstacleList(siteId);
        //return (List<Map> )baseDao.selectList("MaintainArea.getMaintainOrobstacleList",null);
    }

    @Override
    public List<Map> getChargeRoom(String siteId) {
        return  chargeRoomMapper.getChargeRoom(siteId);
        //return (List<Map> )baseDao.selectList("MaintainArea.getMaintainOrobstacleList",null);
    }

    /**
     * 发送消息给导航，提示导航模块加载 动态虚拟障碍文件
     * @param robotId
     * @return
     */
    public Result sendReloadCommandToNav(String robotId){
        MemRobot memRobot= MemUtil.queryRobotById(robotId);
        if (memRobot == null) {
            logger.info("sendReloadCommandToNav failed, memRobot is null");
            return ResultUtil.build(-2,"sendReloadCommandToNav failed, memRobot is null",null);
        }
        String cmd="reload_dynamic";
        String param=null;
        Result result= GeneralPublish.publishTopic(memRobot.getCh(), MsgNames.node_move_base,MsgNames.topic_virtual_obstacle_command,cmd,param,20);
        return result;
    }

    @Override
    @Transactional
    public int deleteMaintainOrobstacle(String uid,String robotId) {
        MaintainArea maintainArea=maintainAreaMapper.select(uid);
        Integer r = maintainAreaMapper.delete(uid);
        if(r ==0){
            //todo wait 这个需要clearDevParams
            //Integer result = devMapper.clearDevParams(uid,null);
            Integer result=0;
            return result;
        }
        String path =FileUtil.getUserHome()+"preset"+File.separator;
        String filename="virtual_obstacle_dynamic.txt";
        FileUtil fileUtil=new FileUtil();
        String UID =maintainArea.getUid();
        String[] temp1=maintainArea.getPoint1().split(",");
        double[] tempP1 = {Double.parseDouble(temp1[0]), Double.parseDouble(temp1[1])};
        String[] temp2=maintainArea.getPoint2().split(",");
        double[] tempP2 = {Double.parseDouble(temp2[0]), Double.parseDouble(temp2[1])};
        String[] temp3=maintainArea.getPoint3().split(",");
        double[] tempP3 = {Double.parseDouble(temp3[0]), Double.parseDouble(temp3[1])};
        String[] temp4=maintainArea.getPoint4().split(",");
        double[] tempP4 = {Double.parseDouble(temp4[0]), Double.parseDouble(temp4[1])};
        String[] temp5=maintainArea.getPoint5().split(",");
        double[] tempP5 = {Double.parseDouble(temp5[0]), Double.parseDouble(temp5[1])};
        String str=String.format("%s%s(%.4f%s%.4f%s(%.4f%s%.4f%s(%.4f%s%.4f%s(%.4f%s%.4f%s(%.4f%s%.4f%s",UID,":",tempP1[0],",",tempP1[1],")",tempP2[0],",",tempP2[1],")",tempP3[0],",",tempP3[1],")",tempP4[0],",",tempP4[1],")",tempP5[0],",",tempP5[1],")");
        try{
            fileUtil.deleteLine(path+filename,str);
            uploadVirturalFileDynamic(robotId,"virtual_obstacle_dynamic.txt");
            //不管成功失败
            sendReloadCommandToNav(robotId);
            return r;
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            fileUtil.addLine(path+filename,str);
            return -2;
        }
    }

    @Override
    public boolean checkMaintainName(String name){
        List<MaintainArea> maintainAreas = maintainAreaMapper.getByName(name);
        if(maintainAreas.size()==0){
            return true;
        }else
            return false;
    }

    @Override
    public boolean checkChargeRoomName(String siteId){
        List<ChargeRoom> chargeRoom = chargeRoomMapper.findListBySiteId(siteId);
        if(chargeRoom.size()==0){
            return true;
        }else
            return false;
    }

    public void alignRunMarks( String siteId,ArrayList<String> runMarkIdList, int type){
        List<RunMark> runMarkList=new ArrayList<>();
        for (String id:runMarkIdList) {
            runMarkList.add(DataCache.findRunMark(siteId,id));
        }

        //find farest two point
        RunMark[] endPoint=findTwoEndPoint(runMarkList);
        Point p1= new Point();
        p1.x=endPoint[0].getLon();
        p1.y=endPoint[0].getLat();
        Point p2= new Point();
        p2.x=endPoint[1].getLon();
        p2.y=endPoint[1].getLat();

        //align the other middle point one by one
        for (RunMark runMark:runMarkList){
            if (type==1) {
                if(!runMark.getUid().equals(endPoint[0].getUid())
                        && !runMark.getUid().equals(endPoint[1].getUid())) {
                    //foot always at middle .
                    Point p3 = new Point();
                    p3.x = runMark.getLon();
                    p3.y = runMark.getLat();
                    Point foot = LineUtil.getFoot(p1, p2, p3);
                    System.out.println(foot);
                    runMark.setLon(foot.x);
                    runMark.setLat(foot.y);
                    runMarkMapper.update(runMark);
                }
            } else if (type==2 || type==3) {
                if(!runMark.getUid().equals(endPoint[0].getUid())) {
                    if (type==2) {
                        runMark.setLat(endPoint[0].getLat());
                    } else {
                        runMark.setLon(endPoint[0].getLon());
                    }
                    runMarkMapper.update(runMark);
                }
            }
        }
    }

    /**
     * fint nearby point pos
     */
    private int findNearbyPointPos(int [] index, List<RunMark>list, int start){
        RunMark p = list.get(start);
        int pos = -1;
        double minDis=Double.MAX_VALUE;
        for (int i=0;i<list.size();i++){
            if (index[i]!=1) {
                double newDis=distance(p.getLon(),p.getLat(),list.get(i).getLon(),list.get(i).getLat());
                if(newDis<minDis){
                    minDis=newDis;
                    pos = i;
                }
            }
        }
        return pos;
    }

    /**
     * fint two end point pos
     */
    private int[] findTwoEndPointPos( List<RunMark> list){
        int [] points= new int[2];
        points[0] = -1;
        points[1] = -1;
        double maxDis=0;
        for (int i=0;i<list.size();i++){
            RunMark p1=list.get(i);
            for(int j=i+1;j<list.size();j++){
                RunMark p2=list.get(j);
                double newDis=distance(p1.getLon(),p1.getLat(),p2.getLon(),p2.getLat());
                if(newDis>maxDis){
                    maxDis=newDis;
                    points[0]=i;
                    points[1]=j;
                }
            }
        }
        return points;
    }


    /**
     * fint two end point
     */
    private RunMark[] findTwoEndPoint( List<RunMark> list){
        int pos1=0;
        int pos2=0;
        double maxDistance=0;
        RunMark runMark1=null;
        RunMark runMark2;
        RunMark[] points=new RunMark[2];

        for (int i=0;i<list.size();i++){
            runMark1=list.get(i);
            for(int j=i+1;j<list.size();j++){
                runMark2=list.get(j);
                double distance=distance(runMark1.getLon(),runMark1.getLat(),runMark2.getLon(),runMark2.getLat());
                if(distance>maxDistance){
                    maxDistance=distance;
                    points[0]=runMark1;
                    points[1]=runMark2;
                }
            }
        }
        return points;
    }

    public  double distance(double x1,double y1,double x2,double y2){
        double d=(x1-x2)*(x1-x2)+(y1-y2)*(y1-y2);
        d=Math.sqrt(d);
        return d;
    }

    @Transactional
    @Override
    public Result addRunMark(String siteId,double x,double y,String name,int ptzType,float robotAngle,int moveStyle){
        RunMark sameRunMark = RunMarkUtil.findTooCloseRunMark(x,y,siteId);
        if(sameRunMark!=null){
            return ResultUtil.build(-1,"添加失败，已经存在一个很近的地标点。",null);
        }
        //检查名称重复
        List<RunMark> runMarkList = DataCache.getRunMarkList(siteId);
        if(runMarkList==null){
            return null;
        }
        for (RunMark exitRunMark : runMarkList) {
            if(exitRunMark.getMarkName().equals(name)){
                return ResultUtil.build(-2,"添加失败，已经存在这个名称的地标点，请修改名称。",null);
            }
        }

        RunMark runMark=new RunMark();
        runMark.setLon(x);
        runMark.setLat(y);
        runMark.setSiteId(siteId);
        runMark.setStatus(0);
        runMark.setMarkName(name);
        runMark.setMoveStyle(moveStyle);
        int ret= runMarkMapper.insert(runMark);
        if(ret!=1){
            return ResultUtil.build(-1,"增加地标点失败，写入数据表失败。",null);
        }
        PtzSet ptzSet = new PtzSet();
        ptzSet.setSiteId(siteId);
        ptzSet.setPtzType(ptzType);
        ptzSet.setMarkId(runMark.getUid());
        ptzSet.setRobotAngle(robotAngle);//一般地标点不用设置角度
        ptzSet.setMarkId(runMark.getUid());
        ptzSet.setDescription(name);
        ptzSet.setStatus(1);
        ptzSet.setSetted(1);
        ret=ptzSetMapper.insertForMark(ptzSet);
        if(ret==1){
            return ResultUtil.build(0,"添加地标点成功。",null);
        } else {
            return ResultUtil.build(-1,"增加地标点失败，写入数据表失败。",null);
        }
    }

    @Override
    @Transactional
    public int deleteChargeRoom(String uid) {
        Integer r = chargeRoomMapper.delete(uid);
        if (r == 0) {
            return -3;
        }
        return r;
    }
}

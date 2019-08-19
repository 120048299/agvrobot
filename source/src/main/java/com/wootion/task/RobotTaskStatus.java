package com.wootion.task;

import com.github.pagehelper.PageInfo;
import com.wootion.agvrobot.utils.UUIDUtil;
import com.wootion.model.*;
import com.wootion.task.map2.Coordinate;
import com.wootion.utiles.DataCache;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 机器人上任务的状态
 */
@Data
public class RobotTaskStatus {
    private static final Logger logger = LoggerFactory.getLogger(RobotTaskStatus.class);

    private Job job;
    private Task task;
    private PtzSet ptzSet;
    private int total=0;
    private int finished=0;
    private int alarmCount=0;
    private int estimatedTime=-1;

    private List<String> pathMarks;
    private int  finishedPathIndex=-1;
    ArrayList<double[]> mapPoints = new ArrayList<>();
    ArrayList<double[]> finishedPoints = new ArrayList<>();
    List<ArrayList<double[]>> hisPathPoints = new ArrayList<>();//历史分段路径

    private PageInfo<Map> taskLogList;

    public void setTaskLogList(PageInfo<Map> list){
        if(job==null || task==null){
            return;
        }
        this.taskLogList=list;
        this.finished=list.getList().size();
        //估计剩余时间
        if(finished==0){
            return;
        }
        if(total<3){
            return;
        }
        long totalCostTime=0;
        int realCount=0;
        for (Map map:list.getList()){
            Integer level=(Integer) map.get("alarm_level");
            if(level!=null && level>0){
                alarmCount++;
            }
            Date beginTime=(Date)map.get("begin_time");
            Date finishTime=(Date)map.get("finish_time");
            if(finishTime==null || beginTime==null){
                continue;
            }
            long costedTime=(finishTime.getTime()-beginTime.getTime())/1000;
            totalCostTime = totalCostTime+ costedTime;
            realCount++;
        }

        double timePerPtz=1.0*totalCostTime/realCount;
        int waitToDoCount=total-finished;
        estimatedTime=(int) (timePerPtz*waitToDoCount);//s
    }

    public void setPath (List<Map> allPathSection){
        if(job==null){
            return ;
        }
        if(allPathSection==null){
            return;
        }
        Site site=DataCache.getSite(job.getSiteId());
        if(site==null){
            logger.error("site is null job.getSiteId()="+job.getSiteId());
        }
        logger.info("setHisPath begin");
        if(allPathSection!=null && allPathSection.size()>0){
            for(int i=0;i<allPathSection.size();i++){
                Map sectionPath =allPathSection.get(i);
                int index = (Integer) sectionPath.get("index");
                List<String> markList = (List)sectionPath.get("markList");
                if(markList==null) {
                    break;
                }
                if(i==allPathSection.size()-1){
                    //当前路径规划的和走过的
                    this.finishedPathIndex=index;
                    this.pathMarks=markList;
                    mapPoints = new ArrayList<>();
                    finishedPoints = new ArrayList<>();
                    for( int j=0;j<markList.size();j++) {
                        String markId=markList.get(j);
                        RunMark runMark= DataCache.findRunMark(job.getSiteId(),markId);
                        if(runMark==null){
                            logger.error("runMark is null markId="+markId);
                        }
                        double[] pt = Coordinate.nav2Web(runMark.getLon(), runMark.getLat(),site.getScale());
                        mapPoints.add(pt);
                        if(i<=finishedPathIndex){
                            finishedPoints.add(pt);
                        }
                    }
                }else{
                    //历史走过的路径
                    ArrayList<double[]> points = new ArrayList<>();
                    for( int j=0;j<=index;j++) {
                        String markId=markList.get(j);
                        RunMark runMark= DataCache.findRunMark(job.getSiteId(),markId);
                        if(runMark==null){
                            logger.error("runMark is null markId="+markId);
                        }
                        double[] pt = Coordinate.nav2Web(runMark.getLon(), runMark.getLat(),site.getScale());
                        points.add(pt);
                    }
                    hisPathPoints.add(points);
                }
            }
        }
        logger.info("setHisPath end hisPathPoints size="+hisPathPoints.size());
    }

}

package com.wootion.task.exec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 内存中的当前任务：
 */
public class MemTask {
    private static final Logger logger = LoggerFactory.getLogger(MemTask.class);
/*

    private Job job;  //特殊的任务没有plan，只有一个exec：一键返航，充电返航
    private List<TaskExec> taskExecList ;
    //private List<List<MarkList>>  pathList;//全局路径
    //private List<List<MarkList>>  finishedPathList= new ArrayList<>();//已经执行完成，走过的全局路径

    private List<TaskExec> finishedTaskExecList = new ArrayList<>();//当前执行完成的就移动到此列表

    public List<TaskExec> getPausedTaskExecList() {
        return pausedTaskExecList;
    }

  */
/*  public List<List<MarkList>> getPauseleftpath() {
        return pauseleftpath;
    }*//*


    //added by btrmg for task pause 2018.12.21
    private List<TaskExec> pausedTaskExecList = new ArrayList<>();//暂停时还未执行的taskexec，当暂停恢复是，继续执行
    //private List<List<MarkList>>  pauseleftpath = new ArrayList<>(); //暂停还未巡检完的路径，暂停恢复后保持原路径
    //added end

    private int total=0; //整个plan的exec总数
    private int currentAlarmCount = 0;      //本次执行过程中的告警数量
    private int currentCount=0;             //本次待执行的exec总数，不变
    private int currentFinishedCount=0;     //本次 完成数量

    private int finishedCount=0;            //本次执行之前已经完成的任务数量，不变
    private int alarmCount=0;

    private TaskExec currentTaskExec ;

    private java.util.Date taskStartTime = null;
    private java.util.Date lastFinishedTime = null;

    private boolean paused=false;
    private TaskRunningStatus taskRunningStatus =null;//供监控页面查看任务状态。任务结束之后，保留最后的状态;新任务来时初始化

    public synchronized TaskExec getCurrentTaskExec() {
        return currentTaskExec;
    }

    public int getAlarmCount() {
        return alarmCount;
    }

    public void setAlarmCount(int alarmCount) {
        this.alarmCount = alarmCount;
    }

    public void plusAlarmCount() {
        this.alarmCount++;
    }

    public MemTask(){

    }




    public synchronized Job getJob() {
        return job;
    }

    */
/*public synchronized List<TaskExec> getTaskExecList() {
        return taskExecList;
    }*//*



    */
/**
     * 暂停的情况：未执行的exec清除了。 返回之前的状态
     * 任务运行结束后
     * @return
     *//*

    public TaskRunningStatus getTaskRunningSatus () {
        if(job ==null) {
            return null;
        }
        if(taskRunningStatus==null){
            taskRunningStatus=new TaskRunningStatus();
        }

        */
/*if(paused){
            return taskRunningStatus;
        }
        *//*

        taskRunningStatus.setPlanName(job.getName());
        taskRunningStatus.setPlanId(job.getUid());
        if(currentTaskExec!=null){
            String name = currentTaskExec.getPtzSet().getDescription();
            */
/*if(currentTaskExec.getPtzSet().getPtzNum()==0){
                name += ":" + currentTaskExec.getRegzSpot().getSpotName();
            }*//*

            taskRunningStatus.setCurrentPoint(name);
        }else{
            taskRunningStatus.setCurrentPoint(null);
        }
        //总进度
        taskRunningStatus.setTotal(total);
        taskRunningStatus.setFinished(currentFinishedCount+finishedCount);
        int progress=(int) (taskRunningStatus.getFinished()*100/taskRunningStatus.getTotal());
        taskRunningStatus.setRateOfProgress(progress);
        taskRunningStatus.setWarn(alarmCount);

        //预估所需时间 依据当前执行完成的点数和时间来计算
        if(currentFinishedCount>0){
            long costedTime=(lastFinishedTime.getTime()-taskStartTime.getTime())/1000;
            double timePerTask=1.0*costedTime/currentFinishedCount;
            //本次待执行总数-本次完成数
            int waitToDoCount=currentCount-currentFinishedCount;
            int estimatedTime=(int) (timePerTask*waitToDoCount/60);
            if(estimatedTime<=0){
                estimatedTime=1;
            }
            taskRunningStatus.setEstimatedTime(estimatedTime);
        }else {
            taskRunningStatus.setEstimatedTime(-1);
        }
        return taskRunningStatus;
    }
*/
}

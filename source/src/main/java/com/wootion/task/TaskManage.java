package com.wootion.task;

import com.wootion.dao.IAlarmDao;
import com.wootion.dao.ITaskDao;
import com.wootion.mapper.SystemLogMapper;
import com.wootion.protocols.robot.RosBridgeClient;
import com.wootion.protocols.robot.RosCommandCaller;
import com.wootion.protocols.robot.RosCommandCallerHolder;
import com.wootion.protocols.robot.msg.*;
import com.wootion.robot.MemRobot;
import com.wootion.robot.MemUtil;
import com.wootion.service.ITaskService;
import com.wootion.task.event.*;
import com.wootion.taskmanager.TaskControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskManage extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(TaskManage.class);


    @Autowired()
    private ITaskService taskService;

    @Autowired
    private SystemLogMapper systemLogMapper;

    //private PathPlanner pathPlanner;
    RemoteController remoteController;
    private boolean running = true;

    public void close() {
        running = false;
    }

    @Override
    public void run() {
        logger.info("TaskManage started!");
        RosBridgeClient client = null;
        //pathPlanner = new PathPlanner(taskDao);
        remoteController=new RemoteController();
        while (running) {
            try {
                Object evt = EventQueue.takeTask();
                /**
                 *  初始化机器人链路
                 */
                if (evt instanceof StartRosBridgeEvent) {
                    logger.info("-------------start ros bridge------------");
                    StartRosBridgeEvent startRosBridgeEvent = (StartRosBridgeEvent) evt;
                    final String url = startRosBridgeEvent.getMemRobot().getRobotRosUrl();
                    client = new RosBridgeClient(url);
                    try {
                        client.open();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        logger.warn("failed open in StartRosBridge, {}", e.getMessage());
                        MemUtil.setRobotCh(startRosBridgeEvent.getMemRobot(),null);
                        continue;
                    }
                }

                /**
                 * ====================================================================
                 */
                if (evt instanceof SubscribeRobotEvent) {
                    MemUtil.subscribeRobot((SubscribeRobotEvent) evt);
                    continue;
                }
                if (evt instanceof AdvertiseRobotEvent) {
                    MemUtil.advertiseRobot((AdvertiseRobotEvent) evt);
                    continue;
                }


                //机器人移动命令响应（主要为移动）
                if (evt instanceof UpdateRobotEvent) {
                    updateRobotStepNew((UpdateRobotEvent) evt);
                    continue;
                }

                if (evt instanceof DeleteMemRobot) { // DeleteMemRobot
                    logger.info("DeleteMemRobot event");
                    DeleteMemRobot deleteMemRobot = (DeleteMemRobot) evt;
                    MemUtil.deleteMemRobotByIp(deleteMemRobot.getRobotIp());
                    continue;
                }

                if (evt instanceof AddPtzSetOverEvent) {
                    this.handleAddPtzSetAck((AddPtzSetOverEvent) evt);
                }

                if (evt instanceof StopTaskEvent) {
                    stopTask((StopTaskEvent) evt);
                    continue;
                }

                if (evt instanceof StartTaskEvent) {
                    startTask((StartTaskEvent) evt);
                    continue;
                }

                if (evt instanceof ChargeEvent) {
                    startCharge((ChargeEvent) evt);
                    continue;
                }


//                if (evt instanceof AlarmCalcFinishedEvent) {
//                    alarmCalcFinished((AlarmCalcFinishedEvent) evt);
//                    continue;
//                }

            } catch (Exception e) {
                logger.warn("Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }
        logger.info("TaskManage stopped!");
        try {
            client.close();
        } catch (Exception e) {
            logger.warn("close rosBridgeClient failed" + e.getMessage());
        }
    }

    private void startCharge(ChargeEvent evt) {
        logger.info("ChargeEvent  ip=" + evt.getRobotIp());
        String robotIp = evt.getRobotIp();
        MemRobot memRobot = MemUtil.queryRobot(robotIp);
        try{
            if(memRobot.getChargeFlowStatus()==1){
                logger.debug("忽略多的消息 ");
                return ;
            }
            TaskControl.goBack(memRobot);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
        }
    }



    /**
     * todo wait
     * //手工暂停任务 来自日历页面
     * 停止执行中的任务
     * @param evt
     */
    private void stopTask(StopTaskEvent evt) {
        Runnable r = ()-> {
            logger.info("stopTask " + evt);
            String taskPlanId = evt.getTaskPlanId();
            String robotIp = evt.getRobotIp();
            if (taskPlanId == null && robotIp == null) {
                return;
            }
            /*ConcurrentHashMap<String, TaskRunner> taskRunnerMap = MemUtil.getTaskRunnerMap();
            if (taskPlanId != null) {
                Job taskPlan = taskDao.getJob(evt.getTaskPlanId());
                //任务不是待运行和运行中，不必暂停
                if (!(taskPlan.getStatus() == 0 || taskPlan.getStatus() == 1)) {
                    return;
                }
                TaskRunner taskRunner1 = null;
                for (TaskRunner taskRunner : taskRunnerMap.values()) {
                    if (taskRunner.getMemTask() != null) {
                        if (taskRunner.getMemTask().getJob().getUid().equals(taskPlan.getUid())) {
                            taskRunner1 = taskRunner;
                            taskRunner1.setStop();//实际为停止
                            break;
                        }
                    }
                }
                if (taskRunner1 != null) {
                    try {
                        while (taskRunner1.isAlive()) {
                            Thread.sleep(1000);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    taskPlan.setStatus(5);
                    taskDao.update(taskPlan);
                    //更新任务所有未完成的task_exec为暂停
                    taskDao.pauseTaskExec(taskPlan.getUid(), 5);
                }
            }
            if (robotIp != null) {
                for (TaskRunner taskRunner : taskRunnerMap.values()) {
                    if (taskRunner.getMemRobot() != null) {
                        if (taskRunner.getMemRobot().getRobotIp().equals(robotIp)) {
                            taskRunner.setStop();//实际为停止
                        }
                    }
                }
            }*/
        };
        new Thread(r).start();
    }


    /**
     * web发起立即启动任务，先暂停其他任务
     * 立即启动,任务优先级最高( 在此之前已经在业务侧调整了优先级，evt的planId已经无意义)。
     * 在taskdbmanage同步发起的sql，按priority先排序，计划时间在后
     * 另外：如果真的要停止任务，则从任务管理界面进行暂停。则此任务不会再次启动。除非取消暂停。
     * @param evt
     */
    private void startTask(StartTaskEvent evt) {
        logger.debug("startTask "+evt);
        //added by btrmg for robot is pause 2018.12.29
        //todo wait
       /* ConcurrentHashMap<String,TaskRunner>  taskRunnerMap= MemUtil.getTaskRunnerMap();
        for(TaskRunner taskRunner: taskRunnerMap.values()) {
            if(taskRunner.getMemRobot()!=null){
                if(taskRunner.getMemRobot().getRobotIp().equals(evt.getRobotIp())) {
                    if(taskRunner.getMemRobot().getTaskStatus() == 2)
                    {
                        logger.debug("the robot is pause ,can't start the task");
                        return;
                    }
                }
            }
        }*/

        //added end
        stopRobotTask(evt.getRobotIp());
        //EventQueue.addTask(new SyncTaskEvent(evt.getRobotId()));
        SyncTaskEvent syncTaskEvent= new SyncTaskEvent(evt.getTaskPlanId());
        syncTaskEvent.setRobotId(evt.getRobotId());
        EventQueue.addTask(syncTaskEvent);
    }

    /**
     *      停止机器人当前任务  内部调用
     * @param robotIp
     * @return
     */
    //todo wait
    private int stopRobotTask(String robotIp) {
       /* Runnable r = ()-> {
            logger.debug("stopRobotTask "+robotIp);
            ConcurrentHashMap<String,TaskRunner>  taskRunnerMap= MemUtil.getTaskRunnerMap();
            TaskRunner taskRunner1=null;
            for(TaskRunner taskRunner: taskRunnerMap.values()) {
                if(taskRunner.getMemRobot().getRobotIp().equals(robotIp))
                taskRunner1=taskRunner;
            }
            if(taskRunner1!=null){
                taskRunner1.setStop();//实际为停止进行中的任务，对plan为暂停，总体暂停，空闲时还会运行
                try{
                    while (taskRunner1.isAlive()){
                        Thread.sleep(1000);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        new Thread(r).start();*/
        return 0;
    }

    /**
     * RobotCommandAck:已经不是返回状态，是响应.由于相关接口多，暂时沿用名字RobotStatus
     * 当接收到RobotStatus消息后，对当前任务的步骤进行处理
     * 例如 发出move，接到响应消息 机器人开始move，机器人到位后又接到机器人到达 消息
     * @param updateRobot
     */
    private void updateRobotStepNew(UpdateRobotEvent updateRobot) {
        RobotCommandAck robotCommandAck = updateRobot.getRobotCommandAck();
        logger.debug(String.format("updateRobotTask msg = %s", robotCommandAck.toString()));
        // 如果对应seqId会话不存在，直接丢弃消息
        int seqId = robotCommandAck.getTrans_id();
        Publish<?> op = EventQueue.getMsgSession(seqId);
        if (op == null) {
            logger.debug("updateRobot: msgSession is expired " + seqId);
            return;
        }
        // 如果对应操作机器人不存在，丢弃消息
        MemRobot memRobot = MemUtil.queryRobot(robotCommandAck.getRobot_ip());
        if (memRobot == null) {
            logger.warn("updateRobot: memRobot is null " +robotCommandAck.getTrans_id());
            EventQueue.popMsgSession(seqId);
            return;
        }

        //int ack=robotCommandAck.getAck();
        //updateRobotTask(memRobot, seqId, op.opId(), ack);
        RosCommandCaller rosCommandCaller = RosCommandCallerHolder.getInstance().getStep(robotCommandAck.getTrans_id());
        if(rosCommandCaller ==null){
            logger.warn("rosCommandCaller not exist"+robotCommandAck.getTrans_id());
        }else{
            rosCommandCaller.setResultMsg(robotCommandAck);
        }

/*
        if (op.opStatus() != OP_STATUS.OP_CREATED) {
            // cmd 消息不删除
            op.opStatus(OP_STATUS.OP_RESPONDED);
        } else {
            EventQueue.popMsgSession(seqId);
        }
*/

    }



    /**
     * 即接口 preset_scale_ack
     * @param evt
     */
    private void handleAddPtzSetAck(AddPtzSetOverEvent evt) {
        PresetScaleAckMsg ackMsg=(PresetScaleAckMsg)evt.getMsg();
        // 如果对应seqId会话不存在，直接丢弃消息
        int seqId = ackMsg.getTrans_id();
        Publish<?> op = EventQueue.getMsgSession(seqId);
        if (op == null) {
            logger.debug("handleAddPtzSetAck msgSession is expired " + seqId);
            return;
        }
        // 如果对应操作机器人不存在，丢弃消息
        MemRobot memRobot = MemUtil.queryRobot(ackMsg.getRobot_ip());
        if (memRobot == null) {
            logger.warn("handleAddPtzSetAck memRobot is null " );
            EventQueue.popMsgSession(seqId);
            return;
        }
        PresetScaleCommandMsg cmdMsg=(PresetScaleCommandMsg) op.getMsg();
        RosCommandCaller rosCommandCaller = RosCommandCallerHolder.getInstance().getStep(cmdMsg.getTrans_id());
        if(rosCommandCaller ==null){
            logger.warn("handleAddPtzSetAck not exist");
            EventQueue.popMsgSession(seqId);
        }else{
            rosCommandCaller.setResultMsg(ackMsg);
        }
    }



//    private void alarmCalcFinished(AlarmCalcFinishedEvent evt){
//        ConcurrentHashMap<String,TaskRunner>  taskRunnerMap= MemUtil.getTaskRunnerMap();
//        for(TaskRunner taskRunner: taskRunnerMap.values()) {
//            if(taskRunner.getMemTask()!=null){
//                if(taskRunner.getMemTask().getJob().getUid().equals(evt.getTaskPlanId())) {
//                    taskRunner.setWaitingCalc(false);
//                }
//            }
//        }
//    }
}

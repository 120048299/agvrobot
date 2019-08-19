package com.wootion.robot;

import com.wootion.dao.ITaskDao;
import com.wootion.mapper.RobotMapper;
import com.wootion.mapper.SiteMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;


/**
 * 对机器人内存数据的维护, 检查机器人通信超时,数据同步等功能
 */
@Component
public class MemRobotMonitor extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(MemRobotMonitor.class.getName());
    private TaskExecutor taskExecutor;


    @Autowired
    private RobotMapper robotMapper;

    @Autowired
    private SiteMapper siteMapper;

    public void run() {
        //启动时
        logger.info("MemRobotMonitor start.");
        MemUtil.syncMemRobot(robotMapper, siteMapper);
        while(true) {
            try {
                MemUtil.checkRobotStatus();

                // EventQueue.addStep(new SyncTaskEvent()); //order 3
                //EventQueue.addTask(new TimerSendMsgEvent());
                MemUtil.writeAllCmd();//发心跳消息给ros
                MemUtil.pushRobotStatus();//发状态给网页
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Check Timeout exception:" + e.getMessage());
                break;
            } catch (Exception e) {
                System.out.println("get Exception" + e.getMessage());
                e.printStackTrace();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                    break;
                }
            }
        }
    }

}



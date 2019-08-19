package com.wootion.task;

import com.wootion.dao.ITaskDao;
import com.wootion.task.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*
    慢的数据库操作，单独分离出来
 */
@Component
public class TaskRemoteManage extends  Thread{
    private static final Logger logger = LoggerFactory.getLogger(TaskRemoteManage.class);


    private  boolean  running = true;
    private RemoteController  remoteController;
    private TerraceController terraceController;
    private CameraController  cameraController;
    private PresetControl presetControl;

    public void close() {
        running = false;
    }



    @Override
    public void run() {
        remoteController  = new RemoteController();
        terraceController = new TerraceController();
        cameraController  = new CameraController();

        while (running) {
            try {
                Thread.sleep(100);
                Object evt = EventQueue.takeRemoteControlTask();

                //远程遥控命令
                if (evt instanceof RobotControlEvent) {
                    logger.info("robot control event!");
                    remoteController.robotControlTask(evt);
                    continue;
                }

                if (evt instanceof RemoteControlTimeOutEvent) {
                    logger.info("robot control time out event!");
                    remoteController.remoteControlTimeOut(evt);
                    continue;
                }

                //云台控制
                if (evt instanceof TerraceControlEvent) {
                    logger.info("robot control event!");
                    terraceController.sendTerraceCmd(evt);
                    continue;
                }
                //摄像机控制
                if (evt instanceof CameraControlEvent) {
                    logger.info("robot control event!");
                    cameraController.doControlTask(evt);
                    continue;
                }

                //巡检点调整云台
                if (evt instanceof AdjustTerraceEvent) {
                    logger.info("robot control event!");
                    presetControl.adjustTerrace((AdjustTerraceEvent)evt);
                    continue;
                }


            } catch (Exception e) {
                logger.warn("Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }
        logger.info("TaskRemoteManage stopped!");

    }



}

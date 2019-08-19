package com.wootion;

import com.wootion.agvrobot.websocket.NettyServer;
import com.wootion.robot.DataSynThread;
import com.wootion.robot.HeartBeat;
import com.wootion.robot.MemRobotMonitor;
import com.wootion.taskmanager.ResultSynThread;
import com.wootion.task.*;

import com.wootion.taskmanager.TaskEventThread;
import com.wootion.utiles.DataCache;
import com.wootion.utiles.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@DependsOn("applicationContextProvider")
public class TaskStarter {

    final static Logger logger = LoggerFactory.getLogger(TaskStarter.class);

    @PostConstruct
    public void init() {

        SessionManager sessionManager = ApplicationContextProvider.getBean("sessionManager",SessionManager.class);
        Thread thread = new Thread(sessionManager,"sessionManager");
        thread.start();

        com.wootion.config.SysParam sysParam =  ApplicationContextProvider.getBean("sysParam", com.wootion.config.SysParam.class);
        int dataCacheTime= sysParam.dataCacheTime;
        DataCache dataCache = ApplicationContextProvider.getBean("dataCache", DataCache.class);
        dataCache.setDataCacheTime(dataCacheTime);
        dataCache.start();

        // 等待第1次数据缓存结束
        while (!dataCache.isFinished()) {
            try {
                Thread.sleep(1000);
                System.out.println("waiting dataCache start end");
            } catch (Exception e) {
                logger.warn("Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }

        int isMainServer = sysParam.isMainServer;
        NettyServer nettyServer = ApplicationContextProvider.getBean("nettyServer",NettyServer.class);
        nettyServer.initialiseNettyServer();

        if(isMainServer==1){
            MemRobotMonitor memRobotMonitor=  ApplicationContextProvider.getBean("memRobotMonitor", MemRobotMonitor.class);
            memRobotMonitor.start();

          /*  if(hasWeatherStation==1){
                WeatherStationMonitor weatherStationMonitor = ApplicationContextProvider.getBean("weatherStationMonitor", WeatherStationMonitor.class);
                weatherStationMonitor.start();
            }
            if(hasChargeRoom==1 && isWuhan==0) {
                ChargeRoomMonitor chargeRoomMonitor = ApplicationContextProvider.getBean("chargeRoomMonitor", ChargeRoomMonitor.class);
                chargeRoomMonitor.start();
            }*/

            TaskManage taskManage =  ApplicationContextProvider.getBean("taskManage", TaskManage.class);
            taskManage.start();

            TaskRemoteManage taskRemoteManage=  ApplicationContextProvider.getBean("taskRemoteManage", TaskRemoteManage.class);
            taskRemoteManage.start();

            HeartBeat heartBeat =  ApplicationContextProvider.getBean("heartBeat", HeartBeat.class);
            heartBeat.start();

            TimeoutTaskManage timeoutTaskManage = ApplicationContextProvider.getBean("timeoutTaskManage",TimeoutTaskManage.class);
            timeoutTaskManage.start();

            ReadScaleQueue readScaleQueue =  ApplicationContextProvider.getBean("readScaleQueue",ReadScaleQueue.class);
            readScaleQueue.start();

            DataSynThread dataSynThread = ApplicationContextProvider.getBean("dataSynThread",DataSynThread.class);
            dataSynThread.start();

            ResultSynThread resultSynThread= ApplicationContextProvider.getBean("resultSynThread",ResultSynThread.class);
            resultSynThread.start();

             TaskEventThread taskEventThread = ApplicationContextProvider.getBean("taskEventThread",TaskEventThread.class);
            taskEventThread.start();
        }

    }

}

package com.wootion.task;

import com.wootion.dao.ITaskDao;
import com.wootion.mapper.CleanExpiredTaskMapper;
import com.wootion.model.CleanExpiredTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component ("timeoutTaskManage")
public class TimeoutTaskManage extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(TimeoutTaskManage.class);

    @Autowired
    CleanExpiredTaskMapper cleanExpiredTaskMapper;
    private boolean running = true;

    public void close() {
        running = false;
    }

    @Override
    public void run() {
        logger.info("TimeoutTaskManage started!");

        while (running) {
            try {
                Date today = new Date();
                SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat f = new SimpleDateFormat("HH");
                String day = d.format(today);
                String hour = f.format(today);
                if(Integer.parseInt(hour) >= 3 && Integer.parseInt(hour)<=5){
                    DealExpiredTaskInDb(day);
                    Thread.sleep(1000*60*5);
                }
           }  catch (Exception e) {
                logger.warn("Exception: " + e.getMessage());
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000*60*20);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
                break;
            }
        }
        logger.info("TimeoutTaskManage stopped!");
    }
    public void DealExpiredTaskInDb(String date){
        cleanExpiredTaskMapper.cleanExpiredTask();
    }

}

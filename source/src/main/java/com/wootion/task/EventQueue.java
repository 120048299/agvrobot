package com.wootion.task;


import com.wootion.model.RunMark;
import com.wootion.protocols.robot.msg.Publish;
import com.wootion.task.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class EventQueue {

    public static final int TIMEOUT = 10;
    private static final Logger logger = LoggerFactory.getLogger(EventQueue.class);

    private static LinkedBlockingQueue<Object> taskEvent = new LinkedBlockingQueue<>();

    private static LinkedBlockingQueue<Object> remoteControlEvent = new LinkedBlockingQueue<>();

    private static LinkedBlockingQueue<Object>  dataSynThread = new LinkedBlockingQueue<>();

    //分离数据库，慢操作
    private static LinkedBlockingQueue<Object> taskDbEvent = new LinkedBlockingQueue<>();

    // msg send to robot records
    // when recv status with same seqId, it will be removed from here
    // when msg send to robot, it will be saved here 
    private static ConcurrentHashMap<Integer, Publish> msgSession = new ConcurrentHashMap<>();
    private static final AtomicInteger seqId = new AtomicInteger();

    private EventQueue() {}

    public static Integer getNextSeqId() {
        return seqId.incrementAndGet();
    }

    public static void addTask(Object evt) {
        if (evt instanceof SyncTaskEvent) {
            taskDbEvent.add(evt);
        }else if (evt instanceof RobotControlEvent
                || evt instanceof TerraceControlEvent
                || evt instanceof CameraControlEvent
                || evt instanceof RemoteControlTimeOutEvent
                || evt instanceof AddPtzSetEvent
                || evt instanceof AdjustTerraceEvent
                ){
            remoteControlEvent.add(evt);
        }else{
            //消息类
            taskEvent.add(evt);
        }
    }

    public static Object takeTask() {
        try {
            return taskEvent.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object takeRemoteControlTask() {
        try {
            return remoteControlEvent.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object takeDbTask() {
        try {
            return taskDbEvent.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

//    public static Object beginDataSyn() {
//        try {
//            return dataSynThread.take();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
	public static Integer addMsgSession(Publish cmd) {
        if (cmd == null) {
            logger.info("addMsgSession, cmd is null!");
            return null;
        }
        int seqId = EventQueue.getNextSeqId();
        msgSession.put(seqId, cmd);
		return seqId;
	}

	public static Publish getMsgSession(Integer seqId) {
        if (seqId == null) {
            logger.info("getMsgSession: seqId is null!");
            return null;
        }

        return msgSession.get(seqId);
	}

    public static Publish popMsgSession(Integer seqId) {
        if (seqId == null) {
            logger.info("getMsgSession: seqId is null!");
            return null;
        }

        Publish op = msgSession.get(seqId);
        msgSession.remove(seqId);
        return op;
    }

    public static double calcAngle(RunMark from, RunMark to) {
        double ret = Math.atan2((to.getLat()-from.getLat()), (to.getLon()-from.getLon()));
        return ret;
    }

    /**
     *
     * @param date 字符串日期
     * @param format 日期格式
     * @return 指定日期月份的第一天和最后一天日期
     */
    public static List<Date> getFirstAndLastDdate(String date, String format){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        List<Date> dateList = new ArrayList<>();
        try {
            Date currentDate = sdf.parse(date);
            calendar.setTime(currentDate);
            calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMinimum(Calendar.DATE));
            Date firstDate = calendar.getTime();
            dateList.add(firstDate);
            calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DATE));
            Date lastDate = calendar.getTime();
            dateList.add(lastDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateList;
    }



}

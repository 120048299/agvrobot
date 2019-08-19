package com.wootion.protocols.robot;

import com.alibaba.fastjson.JSONObject;
import com.wootion.protocols.robot.msg.*;
import com.wootion.protocols.robot.operation.OP_STATUS;
import com.wootion.task.EventQueue;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public class RosCommandCaller implements Callable<Object> {
    private static final Logger logger = LoggerFactory.getLogger(RosCommandCaller.class);
    private String taskId;
    private Integer transId;
    private int status=0; //0 初始 -1 错误结束  1 正常结束  -7取消不再做  -3取消，需要再做

    Channel ch;
    private Publish<?> cmdOp;

    private Object resultMsg;
    private Object lock=new Object();

    public Object getLock() {
        return lock;
    }
    public RosCommandCaller(Channel ch){
        this.ch = ch;

    }

    public Publish<?> getCmdOp() {
        return cmdOp;
    }

    public RosCommandCaller(Channel ch, Publish<?> cmdOp) {
        this.ch = ch;
        this.cmdOp=cmdOp;
        this.transId=cmdOp.getMsg().getTrans_id();
    }

    public void releaseLock(){
        synchronized (lock){
            logger.debug("task=" + taskId +" transId="+transId+ "releaseLock .notify lock="+lock.toString());
            status=-2;
            lock.notify();
        }
    }

    public void cancel(int style){
        synchronized (lock){
            logger.debug("task=" + taskId +" transId="+transId+ "cancel style="+style+" .notify lock="+lock.toString());
            status=style;
            lock.notify();
        }
    }

    public synchronized void sendCmdOp( ){
        if(ch==null){
            return;
        }
        logger.debug("task "+taskId+" start :send msg."+this.cmdOp);
        ChannelFuture future;

        future = ch.writeAndFlush(this.cmdOp);
        future.addListener( (f) -> {
            if (!f.isSuccess()) {
                logger.warn("writeCmd failed: " + this.cmdOp + " - " + JSONObject.toJSON(this.cmdOp).toString());
                logger.warn("writeCmd: write failed! try again later " + f.cause());
                logger.warn("write failed.  cause="+f.cause()+" "+ JSONObject.toJSON(this.cmdOp).toString());
            } else {
                logger.debug("write  succ sendTimes="+this.cmdOp.sendTime()+JSONObject.toJSON(this.cmdOp).toString());
            }
        });
     }


    @Override
    public Object call() throws Exception {
        String threadName=Thread.currentThread().getName();
        //System.out.println(System.currentTimeMillis()+ "  "+threadName+" step:");
        sendCmdOp();
        synchronized (lock){
            lock.wait();
        }
        return getResultMsg();
    }

    public void setResultMsg(Object jobj){
        logger.debug("task "+taskId+"   set result msg!");
        this.resultMsg=jobj;
        synchronized (lock) {
            status=1;
            lock.notify();
        }
    }
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }


    public Object getResultMsg() {
        //System.out.println("getResultMsg");
        return resultMsg;
    }

    public Integer getTransId() {
        return transId;
    }

    public void setTransId(Integer transId) {
        this.transId = transId;
    }

    public int getStatus() {
        return status;
    }


}

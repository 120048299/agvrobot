package com.wootion.protocols.robot;

import com.alibaba.fastjson.JSONObject;
import com.wootion.protocols.robot.msg.*;
import com.wootion.protocols.robot.operation.OP_STATUS;
import com.wootion.task.EventQueue;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public class RosActionCaller implements Callable<Object> {
    private static final Logger logger = LoggerFactory.getLogger(RosActionCaller.class);
    private String taskId;
    private Integer transId;
    private int status=0;

    Channel ch;
    private ActionPublish  cmdOp;

    private Object resultMsg;
    private Object lock=new Object();

    public Object getLock() {
        return lock;
    }
    public RosActionCaller(Channel ch){
        this.ch = ch;

    }


    public RosActionCaller(Channel ch, ActionPublish cmdOp) {
        this.ch = ch;
        this.cmdOp=cmdOp;
        this.transId=cmdOp.getMsg().getGoal().getTrans_id();
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

    /**
     * 这个地方目的是等待命令执行结果上来，触发caller返回。
     * @param resultMsg
     */
    public void handleResult(ActionResponseMsg resultMsg){
        if(resultMsg==null){
            logger.debug(" handleResult  resultMsg is null.");
            return ;
        }
       /* if(resultMsg.getType()==0){//feedback
            RobotCommandAck cmdAck=(RobotCommandAck) resultMsg;//普通指令反馈结果
            switch (cmdAck.getAck()) {
                case 100:
                    //响应
                    logger.debug("task=" + taskId +" transId="+cmdAck.getTrans_id()+ " do step 100.");
                    logger.debug("task=" + taskId +" transId="+cmdAck.getTrans_id()+ " need more result msg.");
                    break;
                case 300:
                case 150:
                    //error
                    logger.debug("task=" + taskId +" transId="+cmdAck.getTrans_id()+ " do step 300 or 150  error. notify.");
                    EventQueue.popMsgSession(((RobotCommandAck) resultMsg).getTrans_id());
                    synchronized (lock){
                        logger.debug("task=" + taskId +" transId="+cmdAck.getTrans_id()+ "do step 300 or 150  .notify lock="+lock.toString());
                        status=-1;
                        lock.notify();
                    }
                    return ;
                case 200:
                    //succ
                    logger.debug("task=" + taskId +" transId="+cmdAck.getTrans_id()+ "do step 200 .******************");
                    EventQueue.popMsgSession(((RobotCommandAck) resultMsg).getTrans_id());
                    synchronized (lock){
                        logger.debug("task=" + taskId +" transId="+cmdAck.getTrans_id()+ "do step 200 .notify lock="+lock.toString());
                        status=1;
                        lock.notify();
                    }
                    return ;
                default:
                    ;
            }
        }*/
        synchronized (lock) {
            status=1;
            lock.notify();
        }
    }

    public synchronized void sendCmdOp( ){
        if(ch==null){
            return;
        }
        logger.debug("task "+taskId+" start :send msg."+this.cmdOp);
        ChannelFuture future;
        TextWebSocketFrame text=new TextWebSocketFrame(JSONObject.toJSON(this.cmdOp).toString() );
        future = ch.writeAndFlush(text);
        System.out.println(JSONObject.toJSON(cmdOp).toString());
        future.addListener( (f) -> {
            if (!f.isSuccess()) {
                logger.warn("write failed.  cause="+f.cause()+" "+ JSONObject.toJSON(this.cmdOp).toString());
            } else {
                logger.debug("write  succ "+ JSONObject.toJSON(this.cmdOp).toString());
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

    public void setResultMsg(Object resultMsg){
          logger.debug("task "+taskId+"   set result msg!");

          this.resultMsg=resultMsg;
          //this.handleResult(resultMsg);
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

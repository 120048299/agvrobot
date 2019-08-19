package com.wootion.protocols.robot;


import com.alibaba.fastjson.JSONObject;
import com.wootion.commons.Result;
import com.wootion.protocols.robot.msg.Publish;
import com.wootion.robot.MemRobot;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * 2018.11.12
 * 实现 单独执行一个命令并返回结果
 */
public class RosCommandExcecutor {

    private static final Logger logger = LoggerFactory.getLogger(RosCommandExcecutor.class);

    protected Channel ch;
    Future<Object> future;//用于等待结果
    private RosCommandCaller rosCommandCaller; //当前步骤
    public RosCommandExcecutor() {}

    public RosCommandExcecutor(Channel ch) {
        this.ch = ch;
    }

    public void setCh(Channel ch) {
        this.ch = ch;
    }

    public void cancel(int style){
        if(rosCommandCaller!=null){
            logger.debug("to 取消任务"+style);
            rosCommandCaller.cancel(style);
        }else{
            logger.debug("to 取消任务:"+style +",当前无步骤");
        }
    }

    /**
     * 发布topic消息，等待返回消息。返回消息对同一个transId可以多个，直到最后结束。
     *  等待相同的transId返回，如果有transId冲突，不同模块发来的？
     * @param
     * @param topicOp
     * @param timeOut
     * @return  msg object
     */
    public Result publish(Publish<?> topicOp, int timeOut){
        int ret=-1;
        rosCommandCaller =new RosCommandCaller(ch,topicOp);
        int transId= rosCommandCaller.getTransId();
        try {
            future= RosCommandCallerHolder.getInstance().addStep(transId, rosCommandCaller);
            Object result=future.get(timeOut,TimeUnit.SECONDS);
            //等待结果
            if(rosCommandCaller.getStatus()==-3 ){
                RosCommandCallerHolder.getInstance().removeStep(transId);
                return new Result(-3,"timeOut",null);
            }else if(rosCommandCaller.getStatus()==1 ){
                RosCommandCallerHolder.getInstance().removeStep(transId);
                return new Result(1,"success",result);
            }
        }catch (TimeoutException e) {
            e.printStackTrace();
            rosCommandCaller.releaseLock();
            ret =-2;
        }catch (CancellationException e) {
            e.printStackTrace();
            ret=-1;
        }catch (Exception e) {
            e.printStackTrace();
            rosCommandCaller.releaseLock();
            ret =-1;
        }
        RosCommandCallerHolder.getInstance().removeStep(transId);
        return new Result(ret,"timeOut",null);
    }


    /**
     * 发布topic 不等待结果
     * @param
     * @param topicOp
     * @return  msg object
     */
    public Result publish(Publish<?> topicOp){
        if(ch==null){
            return new Result(-1,"ch is null",null);
        }
        rosCommandCaller =new RosCommandCaller(ch,topicOp);
        rosCommandCaller.sendCmdOp();
        return new Result(1,"success",null);
    }



    /**
     * callService
     * @param
     * @param
     * @param timeOut
     * @return  msg object
     */
    public Result callService(Publish<?> requestOp, int timeOut){
        int ret=-1;
        //requestOp.setTopic();
        requestOp.setId(String.valueOf(requestOp.getMsg().getTrans_id()));
        rosCommandCaller =new RosCommandCaller(ch,requestOp);
        int transId= rosCommandCaller.getTransId();
        try {
            future= RosCommandCallerHolder.getInstance().addStep(transId, rosCommandCaller);
            Object result=future.get(timeOut,TimeUnit.SECONDS);
            //等待结果
            if(rosCommandCaller.getStatus()==-3 ){
                ret = rosCommandCaller.getStatus();
                RosCommandCallerHolder.getInstance().removeStep(transId);
                return new Result(-3,"timeOut",null);
            }else if(rosCommandCaller.getStatus()==1 ){
                RosCommandCallerHolder.getInstance().removeStep(transId);
                boolean flag=((JSONObject)result).getBoolean("result");
                if(!flag){
                    String errorMsg=((JSONObject)result).getString("values");
                    return new Result(-1,errorMsg,result);
                }
                return new Result(1,"success",result);
            }
        }catch (TimeoutException e) {
            e.printStackTrace();
            rosCommandCaller.releaseLock();
            ret =-2;
        }catch (CancellationException e) {
            e.printStackTrace();
            ret=-1;
        }catch (Exception e) {
            e.printStackTrace();
            rosCommandCaller.releaseLock();
            ret =-1;
        }
        RosCommandCallerHolder.getInstance().removeStep(transId);
        return new Result(ret,"timeOut",null);
    }

}

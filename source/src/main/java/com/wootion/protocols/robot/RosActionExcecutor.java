package com.wootion.protocols.robot;


import com.wootion.commons.Result;
import com.wootion.protocols.robot.msg.ActionPublish;
import com.wootion.protocols.robot.msg.Publish;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * 2019.6.11
 * 实现 action
 */
public class RosActionExcecutor {

    private static final Logger logger = LoggerFactory.getLogger(RosActionExcecutor.class);

    protected Channel ch;
    Future<Object> future;//用于等待结果
    private RosActionCaller rosActionCaller; //当前步骤
    public RosActionExcecutor() {}

    public RosActionExcecutor(Channel ch) {
        this.ch = ch;
    }

    public void setCh(Channel ch) {
        this.ch = ch;
    }

    public void cancel(int style){
        if(rosActionCaller!=null){
            logger.debug("to 取消任务"+style);
            rosActionCaller.cancel(style);
        }else{
            logger.debug("to 取消任务:"+style +",当前无步骤");
        }
    }

    /**
     * 发布topic
     * @param
     * @param topicOp
     * @return  msg object
     */
    public Result publishTopic(ActionPublish topicOp){
        if(ch==null){
            return new Result(-1,"ch is null",null);
        }
        rosActionCaller =new RosActionCaller(ch,topicOp);
        rosActionCaller.sendCmdOp();
        return new Result(1,"success",null);
    }

    /**
     * 发布action goal，等待feedback,直到result
     * @param
     * @param topicOp
     * @param timeOut
     * @return  msg object
     */
    public Result publishTopic(ActionPublish topicOp, int timeOut){
        int ret=-1;
        rosActionCaller =new RosActionCaller(ch,topicOp);
        int transId= rosActionCaller.getTransId();
        try {
            future= RosActionCallerHolder.getInstance().addStep(transId, rosActionCaller);
            Object result=future.get(timeOut,TimeUnit.SECONDS);
            //等待结果
            if(rosActionCaller.getStatus()==-3 ){
                RosCommandCallerHolder.getInstance().removeStep(transId);
                return new Result(-3,"timeOut",null);
            }else if(rosActionCaller.getStatus()==1 ){
                RosCommandCallerHolder.getInstance().removeStep(transId);
                return new Result(1,"success",result);
            }
        }catch (TimeoutException e) {
            e.printStackTrace();
            rosActionCaller.releaseLock();
            ret =-2;
        }catch (CancellationException e) {
            e.printStackTrace();
            ret=-1;
        }catch (Exception e) {
            e.printStackTrace();
            rosActionCaller.releaseLock();
            ret =-1;
        }
        RosCommandCallerHolder.getInstance().removeStep(transId);
        return new Result(ret,"timeOut",null);
    }

}

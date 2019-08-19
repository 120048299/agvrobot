package com.wootion.task;

import com.wootion.Debug;
import com.wootion.commons.Result;
import com.wootion.protocols.robot.GeneralPublish;
import com.wootion.protocols.robot.RosCommandExcecutor;
import com.wootion.protocols.robot.msg.*;
import com.wootion.protocols.robot.operation.CommonOp;
import com.wootion.robot.MemRobot;
import com.wootion.robot.MemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 改变模式 和 直接控制机器人移动命令
 */
public class ModeAndMoveControl {
    private static final Logger logger = LoggerFactory.getLogger(ModeAndMoveControl.class);

    /**
     * 发送远程控制命令 移动和转向
     * @param memRobot
     * @param speed
     * @param yaw
     * @return ret 1 成功  -1 失败
     */
    public static Result sendMoveCmd(MemRobot memRobot,float speed,float yaw){
        logger.info("sendMoveCmd speed="+speed+" yaw="+yaw );
        if(Debug.sendMoveCmd==0){
            return null;
        }
        MoveCommandMsg cmdMsg=new MoveCommandMsg();
        int opId=MemUtil.newOpId();
        cmdMsg.setTrans_id(opId);
        cmdMsg.setVelocity_x(speed);
        cmdMsg.setVelocity_yaw(yaw);
        CommonOp op=new CommonOp(opId,cmdMsg,MsgNames.topic_move_command);
        RosCommandExcecutor rosCommandExcecutor =new RosCommandExcecutor();
        rosCommandExcecutor.setCh(memRobot.getCh());
        return rosCommandExcecutor.publish(op);
    }

    /**
     * 获得后台遥控模式
     * @param memRobot
     * @return  1 成功
     */
    public static int getRemoteControl(MemRobot memRobot){
        Result result=GeneralPublish.publish(memRobot.getCh(),MsgNames.node_robot_control,MsgNames.topic_mode_command,"acquire","server");
        if(result.getCode()!=1){
            logger.info("acquire remote mode failed");
            return -1;
        }
        int i=0;
        while (i<5){//等5秒
            if(memRobot.getRobotMode()==1){
                logger.info("acquire remote mode ok");
                return 1;
            }
            try{
                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }
            i++;
        }
        logger.info("acquire remote mode failed");
        return -1;
    }

    /**
     * 释放后台遥控模式
     * @param memRobot
     * @return  1 成功
     */
    public static int releaseControl(MemRobot memRobot){
        Result result=GeneralPublish.publish(memRobot.getCh(),MsgNames.node_robot_control,MsgNames.topic_mode_command,"release","--");
        if(result.getCode()!=1){
            logger.info("release remote mode failed");
            return -1;
        }
        int i=0;
        while (i<5){//等5秒
            if(memRobot.getRobotMode()!=1){
                logger.info("release remote mode ok");
                return 1;
            }
            try{
                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }
            i++;
        }
        logger.info("release remote mode failed");
        return -1;
    }

}

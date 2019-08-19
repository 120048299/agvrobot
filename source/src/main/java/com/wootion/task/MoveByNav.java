package com.wootion.task;

import com.alibaba.fastjson.JSON;
import com.wootion.commons.Result;
import com.wootion.protocols.robot.RosCommandExcecutor;
import com.wootion.protocols.robot.msg.CommonMsg;
import com.wootion.protocols.robot.msg.MsgNames;
import com.wootion.protocols.robot.operation.CommonOp;
import com.wootion.robot.MemRobot;
import com.wootion.robot.MemUtil;
import com.wootion.utiles.DataCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MoveByNav {
    private static final Logger logger = LoggerFactory.getLogger(MoveByNav.class);
    /**
     *  地图编辑时移动机器人到的某个位置,并且指定到达后的机器人朝向 . 前进或者后退
     *  向cmd-interface发送指令，机器人移动,无需等待结果
     *
     */
    public static  int directMove(String robotId,double x,double y,double xFrom,double yFrom){
        MemRobot memRobot=MemUtil.queryRobotById(robotId);
        if(memRobot==null){
            return -2;
        }
        if(!memRobot.canDoService()){
            return -3;
        }
        float robotSpeed= DataCache.getRobotParamFloat(robotId,"speed_x");
        Float[] position=new Float[2];
        position[0]=(float)x;
        position[1]=(float)y;
        float orientation=(float)Math.atan2((y-yFrom), (x-xFrom));
        float robotOrientation = memRobot.getRobotInfo().getOrientation();
        logger.debug("前进方向："+orientation+" , 机器人方向："+robotOrientation);
        float diff=robotOrientation-orientation;
        int with_direction=1;
        int precision=1;
        int move_style=0;
        float velocity_x=robotSpeed;
        float velocity_yaw=0.5f;

        if(Math.PI*3/4<Math.abs(diff) && Math.abs(diff)<Math.PI*5/4) {
            //倒退
            move_style=1;
        }
        Result result=MoveByNav.sendMoveCmd(memRobot,x,y,orientation,with_direction,precision,move_style,velocity_x,velocity_yaw);
        if(result.getCode()<0){
            return -1;
        }else{
            return 0;
        }
    }



    public static int directRotate(String robotId,double angle){
        MemRobot memRobot=MemUtil.queryRobotById(robotId);
        if(memRobot==null){
            return -2;
        }
        if(!memRobot.canDoService()){
            return -3;
        }
        Float[] position=memRobot.getRobotInfo().getPosition();
        float robotOrientation = memRobot.getRobotInfo().getOrientation();
        double newOrientation=(robotOrientation+Math.PI*angle/180.0);
        if(newOrientation>Math.PI){
            newOrientation=newOrientation-2*Math.PI;
        } else if(newOrientation<-Math.PI){
            newOrientation=2*Math.PI+newOrientation;
        }
        logger.debug("机器人方向："+robotOrientation+" new 机器人方向："+newOrientation);
        float robotSpeed= DataCache.getRobotParamFloat(robotId,"moveSpeed");
        int with_direction=0;
        int precision=0;
        int move_style=0;
        float velocity_x=robotSpeed;
        float velocity_yaw=1;
        Result result=MoveByNav.sendMoveCmd(memRobot,position[0],position[1],(float)newOrientation,with_direction,precision,move_style,velocity_x,velocity_yaw);
        if(result.getCode()<0){
            return -1;
        }else{
            return 0;
        }
    }

    private static Result sendMoveCmd(MemRobot memRobot,double x,double y,float orientation,int with_direction,int precision,int move_style,
                                      float velocity_x,float velocity_yaw){
        memRobot.setLastRemoteControlTime( (new java.util.Date()).getTime());
        Map goal_pose=new HashMap<>();
        goal_pose.put("x",x);
        goal_pose.put("y",y);
        goal_pose.put("yaw",orientation);

        Map data=new HashMap<>();
        data.put("with_direction",with_direction);
        data.put("precision",precision);
        data.put("move_style",move_style);
        data.put("velocity_x",velocity_x);
        data.put("velocity_yaw",velocity_yaw);
        data.put("goal_pose",goal_pose);

        Map params=new HashMap<>();

        params.put("action_name","move_action");
        params.put("type","move");
        params.put("data",data);

        CommonMsg commonMsg= new CommonMsg();
        commonMsg.setSender(MsgNames.node_server);
        commonMsg.setReceiver(MsgNames.node_cmd_interface);
        commonMsg.setData(JSON.toJSONString(params));
        logger.debug("JSON.toJSONString(params)");
        int opId=MemUtil.newOpId();
        commonMsg.setTrans_id(opId);
        CommonOp op= new CommonOp(opId,commonMsg, MsgNames.topic_cmd_action_goal);
        RosCommandExcecutor rosCommandExcecutor =new RosCommandExcecutor();
        rosCommandExcecutor.setCh(memRobot.getCh());
        Result result= rosCommandExcecutor.publish(op);
        return result;
    }
}

package com.wootion.task;
import com.wootion.commons.MSG_TYPE;

import com.wootion.task.event.RobotControlEvent;
import com.wootion.robot.MemRobot;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 机器人权限控制
 * 张继强 2017.12.12
 */
public class RobotAuthManage {
    private MemRobot memRobot;

    private Logger logger = LoggerFactory.getLogger(RobotAuthManage.class);
    public RobotAuthManage(MemRobot memRobot) {
        this.memRobot = memRobot;
    }

    /**
     * 授予控制权限，如果成功则返回true，否则返回false
     * @param msg_type
     * @return
     */

    public boolean authControlAuthorization(MSG_TYPE msg_type){
        /*if (memRobot.authAble(msg_type)){
            Map<Channel, User> channelUserInfoMap = MemUtil.getChannelUserInfoMap();
            Set<Channel> channelSet = channelUserInfoMap.keySet();
            for (Channel channel : channelSet){
                if (channelUserInfoMap.get(channel).equals(memRobot.getUserController())){
                    memRobot.setController(channel);
                    if (msg_type == MSG_TYPE.MT_GET_CONTROL){
                        memRobot.setRobotStatus(ROBOT_STATUS.WEB_HANDLE, (oldstate) -> {
                            if (oldstate == ROBOT_STATUS.IDLE || oldstate == ROBOT_STATUS.TASKING||oldstate == ROBOT_STATUS.URGENT_TASKING) {
                                return true;
                            }
                            return false;
                        });
                    }
                    return true;
                }
            }
        }*/

        return false;
    }


    /**
     * 判断是否有控制权限
     * @param robotControlEvent
     * @return
     */
    public boolean isAuthorizedControlAuth(RobotControlEvent robotControlEvent){
        Channel eventChannel = robotControlEvent.getChannel();
        if (memRobot.getController() == eventChannel){
            return true;
        }
        return false;
    }

    /**
     * 判断是否已经监听改机器人
     * @param robotControlEvent
     * @return
     */
    public boolean isAuthorizedMointorAuth(RobotControlEvent robotControlEvent){
        Channel eventChannel = robotControlEvent.getChannel();
        return memRobot.isMonitorby(eventChannel);
    }

    /**
     * 判断机器人能否被赋予控制权限
     * @param msg_type
     * @return
     */
    public boolean authAble(MSG_TYPE msg_type){
        //机器人状态为充电、掉线、手柄控制、故障 则不能赋予控制权
       /* if (memRobot.getRobotStatus() == ROBOT_STATUS.CHARGING
                || memRobot.getRobotStatus() == ROBOT_STATUS.OFFLINE
                || memRobot.getRobotStatus() == ROBOT_STATUS.HANDLE
                || memRobot.getRobotStatus() == ROBOT_STATUS.WARNING){
            return false;
        }
        //机器人状态为在执行自动巡检任务或空闲状态，则可以赋予控制以及
        if (memRobot.getRobotStatus() == ROBOT_STATUS.TASKING || memRobot.getRobotStatus() == ROBOT_STATUS.IDLE){
            //赋予手柄控制权限
            if (msg_type == MSG_TYPE.MT_GET_CONTROL){
                return true;

            } else
                return false;
        }*/

        return false;
    }
}

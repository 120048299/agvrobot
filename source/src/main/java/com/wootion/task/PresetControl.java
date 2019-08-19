package com.wootion.task;

import com.wootion.commons.Result;
import com.wootion.mapper.PtzSetMapper;
import com.wootion.mapper.RunMarkMapper;
import com.wootion.model.RunMark;
import com.wootion.protocols.robot.RosCommandExcecutor;
import com.wootion.protocols.robot.msg.MsgNames;
import com.wootion.protocols.robot.msg.PresetRotateCommandMsg;
import com.wootion.protocols.robot.msg.PresetScaleCommandMsg;
import com.wootion.protocols.robot.msg.RobotInfo;
import com.wootion.protocols.robot.operation.AddPtzSetOp;
import com.wootion.protocols.robot.operation.PresetRotateOp;
import com.wootion.robot.MemRobot;
import com.wootion.robot.MemUtil;
import com.wootion.task.event.AdjustTerraceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

//import com.wootion.utiles.FtpUtil;

/**
 * 添加预置位接口。发出消息后同步等待返回。
 * （以前要通过memRobot变量中转，现在改为同步线程等待，预置位接口不再返回preset_id，且避免重复点击错误）
 */
@Component
public class PresetControl {
    private static final Logger logger = LoggerFactory.getLogger(PresetControl.class);

    @Autowired
    RunMarkMapper runMarkMapper;
    @Autowired
    PtzSetMapper ptzSetMapper;


    RosCommandExcecutor rosCommandExcecutor =new RosCommandExcecutor();

    /**
     * 录制预置位时鼠标点击视频中的位置，调整旋转云台水平和俯仰，使点击的目标移到屏幕中央
     */
    protected void adjustTerrace(AdjustTerraceEvent adjustTerraceEvent) {
        MemRobot memRobot = MemUtil.queryRobot(adjustTerraceEvent.getRobotIp());
        if(memRobot==null){
            return;
        }
        int opId = MemUtil.newOpId();
        PresetRotateCommandMsg commandMsg = new PresetRotateCommandMsg(opId, adjustTerraceEvent.getDiffX(),adjustTerraceEvent.getDiffY(),1920,1080);
        PresetRotateOp op = new PresetRotateOp(opId, commandMsg);
        RosCommandExcecutor rosCommandExcecutor =new RosCommandExcecutor(memRobot.getCh());
        rosCommandExcecutor.publish(op);
    }

    /**
     * 添加预置位指令
     */
    protected Result doAddPtzSetCmd(String ptzSetId,String type,short infrared,MemRobot memRobot ) {
        int opId = MemUtil.newOpId();
        PresetScaleCommandMsg commandMsg = new PresetScaleCommandMsg(opId, ptzSetId,type,infrared);
        AddPtzSetOp op = new AddPtzSetOp(opId, commandMsg, MsgNames.topic_addptzset_command);
        rosCommandExcecutor.setCh(memRobot.getCh());
        Result result=rosCommandExcecutor.publish(op,30);
        return result;
    }



    /**
     *
     * @param memRobot
     * @param runMarkName
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public RunMark addRunMark(MemRobot memRobot, String runMarkName) {
        RunMark runMark = new RunMark();
        RobotInfo robotInfo = memRobot.getRobotInfo();
        runMark.setLat(Double.valueOf(robotInfo.getPosition()[1]));
        runMark.setLon(Double.valueOf(robotInfo.getPosition()[0]));
        runMark.setMarkName(runMarkName);
        runMark.setSiteId(memRobot.getSiteId());
        runMark.setStatus(0);
        runMark.setMoveStyle(0);
        runMarkMapper.insert(runMark);
        return runMark;
    }



}

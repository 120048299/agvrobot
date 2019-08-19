package com.wootion.task;

import com.wootion.commons.Result;
import com.wootion.protocols.robot.RosCommandExcecutor;
import com.wootion.protocols.robot.msg.ReadScaleAckMsg;
import com.wootion.protocols.robot.msg.ReadScaleCommandMsg;
import com.wootion.protocols.robot.operation.ReadScaleOp;
import com.wootion.robot.MemRobot;
import com.wootion.robot.MemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 异物识别
 */
@Component
public class ForeignReadScale {
    private static final Logger logger = LoggerFactory.getLogger(ForeignReadScale.class);

    /**
     * 异物测试：preset专门建立一个巡检点，专用于异物识别能力测试
     * 数据库没有这个ptzset等
     * @param fileName
     * @return
     */
    public static Map detectForeign(String fileName, String processPath, String robotIp){
        //构造读表消息
        MemRobot memRobot=MemUtil.queryRobot(robotIp);
        int opId = MemUtil.newOpId();
        ReadScaleCommandMsg commandMsg = new ReadScaleCommandMsg(opId, processPath);
        commandMsg.setForeign_detect(1);
        String fileNames[]=new String[1];
        fileNames[0]=fileName;
        commandMsg.setForeign_filename(fileNames);
        ReadScaleOp readScaleOp = new ReadScaleOp(opId, commandMsg);
        RosCommandExcecutor rosCommandExcecutor =new RosCommandExcecutor(memRobot.getCh());
        Result result=rosCommandExcecutor.publish(readScaleOp,120);
        if(result.getCode()==1){
            ReadScaleAckMsg ackMsg = (ReadScaleAckMsg)result.getData();
            String ackResult=ackMsg.getForeign_result();
            if (ackResult == null || ackResult.equals("failed")) {
                logger.debug("异物检测失败 ");
            } else {
                String picPath[] = null;
                double foreignScale[] = null;
                foreignScale = ackMsg.getForeign_scale();
                picPath = ackMsg.getForeign_picture_path();
                if (foreignScale.length<1 || picPath.length<1) {
                    logger.debug("异物检测结果异常 ");
                } else {
                    logger.info("异物检测成功!");
                    Map<String, Object> resultObj = new HashMap();
                    resultObj.put("hasForeign",Double.valueOf(foreignScale[0]).intValue());
                    String foreignPic =(picPath[0]).substring((picPath[0]).indexOf("picture"));
                    resultObj.put("foreignPic",foreignPic);
                    return resultObj;
                }
            }
        }
       return null;
    }


}

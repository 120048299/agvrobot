package com.wootion.taskmanager;

import com.alibaba.fastjson.JSONObject;
import com.wootion.commons.Result;
import com.wootion.protocols.robot.GeneralService;
import com.wootion.protocols.robot.msg.GeneralServiceAckMsg;
import com.wootion.protocols.robot.msg.MsgNames;
import com.wootion.robot.MemRobot;

import java.util.HashMap;
import java.util.Map;

public class TaskControl {

    public static Result sendTaskManagerCommand(MemRobot memRobot, String type, String data) {
        Result result = GeneralService.call(memRobot.getCh(), MsgNames.node_task_manage, MsgNames.service_task_control, type, data, 5);
        if (result.getCode() != 1) {
            return new Result(-1, "发送任务控制命令失败: " + result.getMsg(), null);
        }
        GeneralServiceAckMsg ackMsg = (GeneralServiceAckMsg) result.getData();
        if (!ackMsg.getRet_code().equals("true")) {
            return new Result(-1, "发送任务控制命令失败: " + ackMsg.getRet_msg(), null);
        }
        return new Result(0, "", null);
    }

    /**
     *  启动任务
     * @param memRobot
     * @param taskId
     * @return
     */
    public static  Result startTask(MemRobot memRobot,String taskId){
        String type="start_task";
        Map req=new HashMap<String,String>();
        req.put("id",taskId);
        String data= JSONObject.toJSONString(req);
        return sendTaskManagerCommand(memRobot,type,data);
    }

    /**
     *  激活任务
     * @param memRobot
     * @param taskId
     * @return
     */
    public static  Result activateTask(MemRobot memRobot,String taskId){
        String type="activate_task";
        Map req=new HashMap<String,String>();
        req.put("id",taskId);
        String data= JSONObject.toJSONString(req);
        return sendTaskManagerCommand(memRobot,type,data);
    }

    /**
     *  去激活任务
     * @param memRobot
     * @param taskId
     * @return
     */
    public static  Result deactivateTask(MemRobot memRobot,String taskId){
        String type="deactivate_task";
        Map req=new HashMap<String,String>();
        req.put("id",taskId);
        String data= JSONObject.toJSONString(req);
        return sendTaskManagerCommand(memRobot,type,data);
    }

    /**
     *  去激活任务
     * @param memRobot
     * @param taskId
     * @return
     */
    public static  Result deleteTask(MemRobot memRobot,String taskId){
        String type="delete_task";
        Map req=new HashMap<String,String>();
        req.put("id",taskId);
        String data= JSONObject.toJSONString(req);
        return sendTaskManagerCommand(memRobot,type,data);
    }



    /**
     *  暂停job
     * @param memRobot
     * @param suspendStyle 暂停方式，0-手动暂停 1-原地暂停
     * @return
     */
    public static  Result suspendJob(MemRobot memRobot,int suspendStyle){
        String type="suspend_job";
        Map req=new HashMap<String,String>();
        req.put("style",suspendStyle);
        String data= JSONObject.toJSONString(req);
        return sendTaskManagerCommand(memRobot,type,data);
    }
    /**
     *  恢复运行job
     * @param memRobot
     * @param jobId
     * @param style
     * @return
     */
    public static  Result resumeJob(MemRobot memRobot,String jobId,int style){
        String type="resume_job";
        Map req=new HashMap<String,String>();
        req.put("id",jobId);
        req.put("style",style);
        String data= JSONObject.toJSONString(req);
        return sendTaskManagerCommand(memRobot,type,data);
    }

    /**
     *  取消job
     *  job_id为作业id，可选，原地终止时，不传job_id,终止当前执行的作业
     cancel_style为取消方式，0-手动终止 1-原地终止
     * @param memRobot
     * @param jobId
     * @return
     */
    public static  Result cancelJob(MemRobot memRobot,String jobId,int cancelStyle){
        String type="cancel_job";
        Map req=new HashMap<String,String>();
        req.put("id",jobId);
        req.put("style",cancelStyle);
        String data= JSONObject.toJSONString(req);
        return sendTaskManagerCommand(memRobot,type,data);
    }

    /**
     * 结束等待
     * @param memRobot
     * @return
     */
    public static  Result endWait(MemRobot memRobot){
        String type="end_wait";
        Map req=new HashMap<String,String>();
        String data= JSONObject.toJSONString(req);
        return sendTaskManagerCommand(memRobot,type,data);
    }

    /**
     * 返航
     * @param memRobot
     * @return
     */
    public static  Result goBack(MemRobot memRobot){
        String type="go_back";
        Map req=new HashMap<String,String>();
        String data= JSONObject.toJSONString(req);
        return sendTaskManagerCommand(memRobot,type,data);
    }

    /**
     * 结束充电
     * @param memRobot
     * @return
     */
    public static  Result stopCharge(MemRobot memRobot){
        String type="stop_charge";
        Map req=new HashMap<String,String>();
        String data= JSONObject.toJSONString(req);
        return sendTaskManagerCommand(memRobot,type,data);
    }


    /**
     * 远程急停
     * @param memRobot
     * @return
     */
    public static  Result remoteStop(MemRobot memRobot){
        String type="remoteStop";
        Map req=new HashMap<String,String>();
        String data= JSONObject.toJSONString(req);
        return sendTaskManagerCommand(memRobot,type,data);
    }

    /**
     *  设置task_manager的模式
     * @param memRobot
     * @param mode  1紧急和0普通
     * @return
     */
    public static  Result setTaskMode(MemRobot memRobot,int mode){
        String type="set_task_mode";
        Map req=new HashMap<String,String>();
        req.put("mode",mode);
        String data= JSONObject.toJSONString(req);
        return sendTaskManagerCommand(memRobot,type,data);
    }
}

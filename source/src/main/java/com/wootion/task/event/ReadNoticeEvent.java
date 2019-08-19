package com.wootion.task.event;

import com.wootion.robot.MemRobot;
import com.wootion.task.exec.MemTask;
import lombok.Data;

import java.util.Arrays;

/**
 * 读表通知
 */
@Data
public class ReadNoticeEvent {
    //private MemRobot memRobot;
    private String ptzSetId;
    private String taskLogId;
    private String fileName;
    private String infraredFileName;
    private Integer[]     roi_vertex;//预选框顶点
    private Integer[]     roi_vertex_thermal;//预选框顶点

    private String[] foreignFileNames;
    private int foreign_detect;

}

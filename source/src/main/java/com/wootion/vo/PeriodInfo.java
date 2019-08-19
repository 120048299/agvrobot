package com.wootion.vo;

import com.wootion.agvrobot.utils.DateUtil;
import com.wootion.model.TaskPeriod;

import java.time.Period;
import java.util.List;

public class PeriodInfo {
    private String uid;
    private String taskId;
    //style_param   	      varchar(350)  comment 'style为 隔天，保存间隔天数数字0,1,2...,0即每天;
    // 每周 保存选择的周几：1;2;3;4;5;6;7 ;每月保存选择的天，01;02;35;31,指定日期时：yyyy.mm.dd;yyyy.mm.dd;多个日期',
    //within_aday_time     varchar(120)  comment '一天内的计划开始和结束时间,可以多个时段, 时:分-时:分;时:分-时:分;时:分-时:分;',

    private String dateList[];  //定期执行的日期
    private String timeList[];  //日内时段
    private int style;   //0隔几天, 1每周, 2 每月 3 指定日期

    private String startDate;
    private String endDate;
    private String perDay;
    private String perWeek[];
    private String perMonth[];

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String[] getDateList() {
        return dateList;
    }

    public void setDateList(String[] dateList) {
        this.dateList = dateList;
    }

    public String[] getTimeList() {
        return timeList;
    }

    public void setTimeList(String[] timeList) {
        this.timeList = timeList;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getPerDay() {
        return perDay;
    }

    public void setPerDay(String perDay) {
        this.perDay = perDay;
    }

    public String[] getPerWeek() {
        return perWeek;
    }

    public void setPerWeek(String[] perWeek) {
        this.perWeek = perWeek;
    }

    public String[] getPerMonth() {
        return perMonth;
    }

    public void setPerMonth(String[] perMonth) {
        this.perMonth = perMonth;
    }

    public PeriodInfo(){

    }

    public static PeriodInfo toPeriodInfo(TaskPeriod taskPeriod){
        PeriodInfo info=new PeriodInfo();
        info.setUid(taskPeriod.getUid());
        info.setTaskId(taskPeriod.getTaskId());
        info.setStyle(taskPeriod.getStyle());
        if(info.getStyle()!=3){
            info.setStartDate(DateUtil.dateToString(taskPeriod.getStartDate(),"yyyy-MM-dd"));
            info.setEndDate(DateUtil.dateToString(taskPeriod.getEndDate(),"yyyy-MM-dd"));
        }
        if(taskPeriod.getWithinAdayTime()!=null && !"".equals(taskPeriod.getWithinAdayTime() ) ){
            info.setTimeList(taskPeriod.getWithinAdayTime().split(";"));
        }

        String styleParam=taskPeriod.getStyleParam();
        if(info.style==0){
            info.setPerDay(styleParam);
        }else if(info.style==1){
            info.setPerWeek(styleParam.split(";"));
        }else if(info.style==2){
            info.setPerMonth(styleParam.split(";"));
        }else {
            info.setDateList(styleParam.split(";"));
        }
        return info;
    }

    public static TaskPeriod toPeriod(PeriodInfo info){
        TaskPeriod period=new TaskPeriod();
        period.setUid(info.getUid());
        period.setTaskId(info.getTaskId());
        int style=info.getStyle();
        period.setStyle(style);

        if(style!=3){
            period.setStartDate(DateUtil.stringToDate(info.getStartDate(),"yyyy-MM-dd"));
            period.setEndDate(DateUtil.stringToDate(info.getEndDate(),"yyyy-MM-dd"));
        }

        String str="";
        for (String timeStr : info.timeList){
            str += timeStr+";" ;
        }
        period.setWithinAdayTime(str);

        if(style==0){
            period.setStyleParam(info.getPerDay());
        }else if(style==1){
            str="";
            for (String item: info.getPerWeek()){
                str += item+";" ;
            }
            period.setStyleParam(str);
        }else if(style==2){
            str="";
            for (String item: info.getPerMonth()){
                str += item+";" ;
            }
            period.setStyleParam(str);
        }else {
            str="";
            for (String item: info.getDateList()){
                str += item+";" ;
            }
            period.setStyleParam(str);
        }
        return period;
    }
}

package com.wootion.agvrobot.utils;

import org.slf4j.LoggerFactory;

import java.util.Date;

public class MsgTraceUtil {
    private static final org.slf4j.Logger msgTrace = LoggerFactory.getLogger(MsgTraceUtil.class);



    public static void writeMsg(String text){
        msgTrace.info(text);
    }

    public static void writeMsg(String ip,String fromMarkId,String toMarkId,String text){
        writeMsg(ip,null,null,fromMarkId,toMarkId,text);
    }

    public static void writeMsg(String ip,String planId,String taskExecId,String fromMarkId,String toMarkId,String text){

        Date date=new Date();
        StringBuffer buf=new StringBuffer();
        buf.append(date.toString());
        if(ip!=null){
            buf.append(" ip=");
            buf.append(ip);
        }
        if(planId!=null) {
            buf.append(" planId=");
            buf.append(planId);
        }
        if(taskExecId!=null) {
            buf.append(" taskExecId=");
            buf.append(taskExecId);
        }
        if(fromMarkId!=null) {
            buf.append(" fromMarkId=");
            buf.append(fromMarkId);
        }
        if(toMarkId!=null) {
            buf.append(" toMarkId=");
            buf.append(toMarkId);
        }

        buf.append(" ");
        buf.append(text);
        buf.append("\n");
        msgTrace.info(buf.toString());
    }



    public static  void  main (String agrs[]){

    }
}


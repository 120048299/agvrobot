package com.wootion.agvrobot.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static Date stringToDate(String str) {
        return stringToDate(str, "yyyy-MM-dd HH:mm:ss");
    }

    public static Date parseDate(String str) {
        if(str==null || "".equals(str)){
            return null;
        }
        try {
            Date date = null;
            String format="yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            date = formatter.parse(str);
            //System.out.println(date);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Date date = null;
            String format="yyyy-MM-dd";
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            date = formatter.parse(str);
            //System.out.println(date);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date stringToDate(String str, String format) {
        if(str==null || format==null){
            return null;
        }
        if("".equals(str) || "".equals(format)){
            return null;
        }
        try {
            Date date = null;
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            date = formatter.parse(str);
            //System.out.println(date);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // "yyyy-MM-dd HH:mm:ss"
    public static String dateToString(Date date, String format) {
        if(date==null || format==null){
            return null;
        }
        if( "".equals(format)){
            return null;
        }
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            String time = formatter.format(date);
            return time;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static java.sql.Time strToSqlTime(String strTime,String fomat) {
        String str = strTime;
        SimpleDateFormat format = new SimpleDateFormat(fomat);
        Date d = null;
        try {
            d = format.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        java.sql.Time time = new java.sql.Time(d.getTime());
        return time;
    }

    /**
     *
     * @param date
     * @param unit 1 天 2 小时 3 分钟 4 秒
     * @param n
     * @return
     */
    public static  java.util.Date add(java.util.Date date,int unit,int n){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        if(unit==1){
            c.add(Calendar.DAY_OF_MONTH, n);// +1天
        }else if(unit==2){
            c.add(Calendar.HOUR, n);
        }else if(unit==3){
            c.add(Calendar.MINUTE, n);
        } else if(unit==4){
            c.add(Calendar.SECOND, n);
        }

        return  c.getTime();
    }

    public static int getWeekDay(java.util.Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);//星期日=1
        if (weekDay == 1) {
            weekDay = 7;
        } else {
            weekDay--;
        }
        return weekDay;
    }

    public static int getMonthDay(java.util.Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return  calendar.get(Calendar.DAY_OF_MONTH);//星期日=1
    }


    public static String timeSpan(Date date1,Date date2){
        int timeSpan = (int)(date2.getTime()-date1.getTime())/1000;
        int hour = timeSpan / 3600;
        int minute = (timeSpan % 3600 )/60;
        return String.format("%d小时%d分钟",hour,minute);
    }

    public static void main(String args[]) throws ParseException {
        Date date= DateUtil.stringToDate("2018-10-19","yyyy-MM-dd");
        //System.out.println(date);
    }
}
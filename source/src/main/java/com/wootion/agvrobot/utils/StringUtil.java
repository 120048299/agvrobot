package com.wootion.agvrobot.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    /**
     * 判断字符串是否是数字.
     *
     * @param str the str
     * @return true, if is numeric
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static boolean isNull(String str){
        if(str==null){
            return true;
        }
        if("".equals(str)){
            return true;
        }
        return false;
    }


    /**
     * 去掉字符转末尾的8位日期数字 abc20180829  ==>> abc
     * @param str
     * @return
     */
    public static String trimSuffixDate(String str){
        String temp;
        String numbers = str.replaceAll(".*[^\\d](?=(\\d+))","");
        if(numbers.length()<8) {
            //名称后的数字不是日期
            return str;
        }
        numbers=numbers.substring(numbers.length()-8);
        if(isValidDate(numbers)){
            String newName=str.substring(0,str.length()-8);
            return newName;
        }else{
            //虽然是数字，但是不是日期格式
            return str;
        }
    }

    /**
     * 判断字符串是否以8位日期数字结尾  abc20180829
     * @param str
     * @return
     */
    public static boolean isEndWithDate(String str){
        String temp;
        String numbers = str.replaceAll(".*[^\\d](?=(\\d+))","");
        if(numbers.length()<8) {
            //名称后的数字不是日期
            return false;
        }
        numbers=numbers.substring(numbers.length()-8);
        if(isValidDate(numbers)){
            return true;
        }else{
            //虽然是数字，但是不是日期格式
            return false;
        }
    }

    /**
     * 判断字符串是否日期
     * @param str
     * @return
     */
    public static boolean isValidDate(String str) {
        boolean convertSuccess=true;
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            // e.printStackTrace();
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess=false;
        }
        return convertSuccess;
    }

    public static void  main(String args[]){

    }


}

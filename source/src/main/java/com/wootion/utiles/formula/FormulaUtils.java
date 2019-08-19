package com.wootion.utiles.formula;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

/**
 * @项目名称: sunson_pams
 * @类名称: FormulaUtils
 * @类描述: 非原创（慎用）
 * @创建人: 唐泽齐
 * @创建时间: 2017年12月15日 上午9:47:23
 * @修改人: 唐泽齐
 * @修改时间: 2017年12月15日 上午9:47:23
 * @修改备注:
 * @version: 1.0
 */
public class FormulaUtils {


    /**
     * 验证表达式是否合法 ，不报错即合法
     * @param exp
     * @return
     */
    public static int validate(String exp){
        try{
            ExpressionParser parser=new ExpressionParser(exp);
            parser.readNode();
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }


    /**
     * 替换变量之后验证表达式合法性。
     * 如：validate("({result} >= 10) ","result","1")
     * @param exp
     * @param paramName
     * @param paramValue
     * @return
     */
    public static int validate(String exp,String paramName,String paramValue) {
        if(exp==null || "".equals(exp) ) {
            return 0;
        }
        String exp2=exp.replace("{"+paramName+"}",paramValue);
        int ret=validate(exp2);
        if(ret==1){
            return validateByEval(exp2);
        }
        return ret;
    }
    //todo 验证表达式必须时逻辑值


    /**
         * 用计算来验证,不出异常则对
         * @param exp
         * @return
         */
    public static  int validateByEval(String exp){
        try{
            ExpressionEvaluator evaluator = new ExpressionEvaluator();
            evaluator.eval(exp);
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 用计算结果是否为Boolean来验证,表达式最后结果是否为逻辑值.
     * 告警设置时调用验证
     * @param exp
     * @return -2不合法, -1 不是逻辑值 ,0 表达式为空 , 1 是逻辑值
     */
    public static  int isBooleanExpByEval(String exp,String paramName,String paramValue){
        try{
            if(exp==null || "".equals(exp) ) {
                return 0;
            }
            ExpressionEvaluator evaluator = new ExpressionEvaluator();
            String exp2=exp.replace(paramName,paramValue);
            Object obj=evaluator.eval(exp2);
            if(obj instanceof Boolean){
                return 1;
            }else{
                return -1;
            }
        }catch (Exception e){
            e.printStackTrace();
            return -2;
        }
    }

    /**
     * 用计算结果是否为Boolean来验证,表达式最后结果是否为逻辑值.
     * 告警设置时调用验证
     * @param exp
     * @return -2不合法, -1 不是逻辑值 ,0 表达式为空 , 1 是逻辑值
     */
    public static  int isBooleanExpByEval(String exp,String paramName[],String paramValue[]){
        try{
            if(exp==null || "".equals(exp) ) {
                return 0;
            }
            ExpressionEvaluator evaluator = new ExpressionEvaluator();
            String exp2=exp;
            for (int i=0;i<paramName.length;i++){
                exp2=exp2.replace(paramName[i],String.valueOf(paramValue[i]));
            }
            //String exp2=exp.replace("{"+paramName+"}",paramValue);
            Object obj=evaluator.eval(exp2);
            if(obj instanceof Boolean){
                return 1;
            }else{
                return -1;
            }
        }catch (Exception e){
            e.printStackTrace();
            return -2;
        }
    }
    /**
     * 计算结果 可能返回true false 或者数值
     * @param exp 一个参数的情况
     * @return
     */
    public static Object eval(String exp,String paramName,String paramValue){
        try{
            ExpressionEvaluator evaluator = new ExpressionEvaluator();
            String exp2=exp.replace(paramName,paramValue);
            Object obj=evaluator.eval(exp2);
            return obj;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 计算结果 可能返回true false 或者数值
     * @param exp 一个参数的情况
     * @return
     */
    public static Object eval(String exp,String paramName[],double paramValue[]){
        if(exp==null || paramName==null || paramValue==null){
            return null;
        }
        if(paramName.length!=paramValue.length){
            return null;
        }
        try{
            ExpressionEvaluator evaluator = new ExpressionEvaluator();
            String exp2=exp;
            for (int i=0;i<paramName.length;i++){
                //exp2=exp2.replace("{"+paramName[i]+"}",String.valueOf(paramValue[i]));
                exp2=exp2.replace(paramName[i],String.valueOf(paramValue[i]));
            }
            Object obj=evaluator.eval(exp2);
            System.out.println(obj.toString());
            return obj;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static boolean evalBool(String exp,String paramName[],double paramValue[]) {
        Object obj=eval(exp,paramName,paramValue);
        if(obj instanceof Boolean){
            return ((Boolean) obj).booleanValue();
        }else if(obj instanceof  Number){
            if(((Number) obj).intValue()==1){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }

    public static boolean evalBool(String exp,String paramName,Double paramValue) {
        Object obj=eval(exp,paramName,String.valueOf(paramValue));
        if(obj instanceof Boolean){
            return ((Boolean) obj).booleanValue();
        }else if(obj instanceof  Number){
            if(((Number) obj).intValue()==1){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }
        /**
         * 测试
         *
         * @方法名:main
         * @参数 @param args
         * @返回类型 void
         */
    public static void main(String[] args) {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();


/*
        String s1 = "1+2+3+4";
        System.out.println(evaluator.eval(s1));

        String s2 = "(20 - 6*5) < 3.12";
        System.out.println(evaluator.eval(s2));
*/
        //
        String exp = "!*(1==1 &&(  2==2 && 3!=1) )";
        int ret3=FormulaUtils.isBooleanExpByEval(exp,"result","10");
        System.out.println("合法性："+ret3);
        Object ret4=FormulaUtils.eval(exp,"result","0");
        System.out.println("结果"+ret4);
/*

        String s4 = "\"hello\" == \"hello\" && 3 != 4";
        System.out.println(evaluator.eval(s4));

        String s5 = "\"helloworld\" @ \"hello\" &&  \"helloworld\" !@ \"word\" ";
        System.out.println(evaluator.eval(s5));
*/

    }

}
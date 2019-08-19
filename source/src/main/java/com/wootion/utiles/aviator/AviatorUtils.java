package com.wootion.utiles.aviator;

import com.alibaba.fastjson.JSONObject;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.wootion.commons.Result;

import java.util.*;

/**
 * @Author: majunhui
 * @Date: 2019/1/25 0025
 * @Version 1.0
 */
public class AviatorUtils {
    public static final String CURR ="curr"; //当前值
    public static final String LAST ="last"; //上次
    public static final String OTHERPHASE ="otherPhase"; //其他相值
    public static final String ENVTEMP ="envTemp"; //环境温度

    private static final Map<String, Map<String, String>> OpMap = new HashMap<>();
    static {
        // 算术运算符
        OpMap.put("mathOp.plus", new HashMap(){{
            put("name", "+");
            put("value", "+");
        }});
        OpMap.put("mathOp.subtract", new HashMap(){{
            put("name", "-");
            put("value", "-");
        }});
        OpMap.put("mathOp.multiply", new HashMap(){{
            put("name", "×");
            put("value", "*");
        }});
        OpMap.put("mathOp.divide", new HashMap(){{
            put("name", "÷");
            put("value", "/");
        }});

        // 关系运算符
        OpMap.put("relationOp.greaterThan", new HashMap(){{
            put("name", ">");
            put("value", ">");
        }});
        OpMap.put("relationOp.lessThan", new HashMap(){{
            put("name", "<");
            put("value", "<");
        }});
        OpMap.put("relationOp.greaterEqualsThan", new HashMap(){{
            put("name", ">=");
            put("value", ">=");
        }});
        OpMap.put("relationOp.lessEqualsThan", new HashMap(){{
            put("name", "<=");
            put("value", "<=");
        }});
        OpMap.put("relationOp.equals", new HashMap(){{
            put("name", "==");
            put("value", "==");
        }});
        OpMap.put("relationOp.notEquals", new HashMap(){{
            put("name", "!=");
            put("value", "!=");
        }});

        // 逻辑运算符
        OpMap.put("logicOp.and", new HashMap(){{
            put("name", " 且 ");
            put("value", " && ");
        }});
        OpMap.put("logicOp.or", new HashMap(){{
            put("name", " 或 ");
            put("value", " || ");
        }});
        OpMap.put("logicOp.not", new HashMap(){{
            put("name", " 非");
            put("value", " !");
        }});

        // 其他运算符
        OpMap.put("otherOp.leftParentheses", new HashMap(){{
            put("name", "(");
            put("value", "(");
        }});
        OpMap.put("otherOp.rightParentheses", new HashMap(){{
            put("name", ")");
            put("value", ")");
        }});

        /*
        函数
        abs(x) {"type":"function","id":"abs","name":"绝对值"}
        (x>=minValue && x<=maxValue) {"type":"function","id":"between","name":"在区间","minValue":"1","maxValue":"5"}
        (x<minValue || x>maxValue) {"type":"function","id":"notBetween","name":"不在区间","minValue":"1","maxValue":"5"}
        元素
        fixValue {"type":"element","id":"fixValue","name":"固定值",value:"15"}
        变量
        ptzSetValue {
            "type":"variable","id":"ptzSetValue","name":"检测点检测值",
            "itemId":"10001","itemName":"温度值",
            "valueType":"currentValue","valueName":"当前值"
        }
        */
    }

    public static ParseExpResult parseExpList(ArrayList<JSONObject> expList) throws Exception {
        if (expList == null || expList.size()==0) {
            return null;
        }
        String expStr1 = "";
        String expStr2 = "";
        ArrayList<String> currItems = new ArrayList<>();
        ArrayList<String> lastItems = new ArrayList<>();
        ArrayList<String> phaseItems = new ArrayList<>();
        String envTemp = null;
        int size = expList.size();
        JSONObject lastObj = null;
        Stack<Integer> stackOperator1 = new Stack<>();
        int lastLen1 = 0;
        Stack<Integer> stackOperator2 = new Stack<>();
        int lastLen2 = 0;
        for( int i = 0 ; i < size ; i++) {
            JSONObject obj = expList.get(i);
            String type = obj.getString("type");
            String id = obj.getString("id");
            String str1 = null;
            String str2 = null;
            switch (type) {
                case "mathOp":
                case "relationOp":
                case "logicOp":
                    str1 = OpMap.get(type+"."+id).get("value");
                    str2 = OpMap.get(type+"."+id).get("name");
                    break;
                case "otherOp": {
                    str1 = OpMap.get(type+"."+id).get("value");
                    str2 = OpMap.get(type+"."+id).get("name");
                    switch (id) {
                        case "leftParentheses":
                            stackOperator1.push(expStr1.length());
                            stackOperator2.push(expStr2.length());
                            break;
                        case "rightParentheses": {
                            boolean nextIsFunction = false;
                            if (i + 1 < size) {
                                JSONObject nextObj = expList.get(i + 1);
                                if (nextObj != null) {
                                    String nextType = nextObj.getString("type");
                                    if (nextType != null && nextType.equals("function")) {
                                        nextIsFunction = true;
                                    }
                                }
                            }
                            if (!nextIsFunction) {
                                stackOperator1.pop();
                                stackOperator2.pop();
                            }
                            break;
                        }
                    }
                    break;
                }
                case "function": {
                    String lastType = lastObj.getString("type");
                    String lastId = lastObj.getString("id");
                    int k1 = -1;
                    int k2 = -1;
                    if (lastType != null && (lastType.equals("variable") || lastType.equals("function"))) {
                        k1 = expStr1.length() - lastLen1;
                        k2 = expStr2.length() - lastLen2;
                    } else if (lastType != null && lastType.equals("otherOp") &&
                            lastId != null && lastId.equals("rightParentheses")) {
                        k1 = stackOperator1.pop();
                        k2 = stackOperator2.pop();
                    }
                    if (k1 == -1 || k2 == -1) {
                        break;
                    }
                    String strK1 = expStr1.substring(k1);
                    String strK2 = expStr2.substring(k2);
                    switch (id) {
                        case "abs":
                            str1 = "math.abs(" + strK1 + ")";
                            str2 = strK2 + "绝对值";
                            break;
                        case "between": {
                            String minValue = obj.getString("minValue");
                            String maxValue = obj.getString("maxValue");
                            if (minValue != null && maxValue != null) {
                                str1 = "(" + strK1 + ">=" + minValue + " && " + strK1 + "<=" + maxValue + ")";
                                str2 = strK2 + "在[" + minValue + "," + maxValue + "]区间";
                            }
                            break;
                        }
                        case "notBetween": {
                            String minValue = obj.getString("minValue");
                            String maxValue = obj.getString("maxValue");
                            if (minValue != null && maxValue != null) {
                                str1 = "(" + strK1 + "<" + minValue + " || " + strK1 + ">" + maxValue + ")";
                                str2 = strK2 + "不在[" + minValue + "," + maxValue + "]区间";
                            }
                            break;
                        }
                    }
                    if (str1 != null) {
                        expStr1 = expStr1.substring(0, k1);
                    }
                    if (str2 != null) {
                        expStr2 = expStr2.substring(0, k2);
                    }
                    break;
                }
                case "element": {
                    switch (id) {
                        case "fixValue": {
                            String lastType = lastObj.getString("type");
                            String lastId = lastObj.getString("id");
                            // 不能连续出现固定值
                            if (type.equals(lastType) && id.equals(lastId)) {
                                break;
                            }
                            String value = obj.getString("value");
                            if (value != null) {
                                str1 = str2 = value;
                            }
                            break;
                        }
                    }
                    break;
                }
                case "variable": {
                    switch (id) {
                        case "ptzSetValue": {
                            String itemId = obj.getString("itemId");
                            String itemName = obj.getString("itemName");
                            String valueType = obj.getString("valueType");
                            String valueName = obj.getString("valueName");
                            if (itemId != null && valueType != null) {
                                String currValue = CURR + itemId ;
                                String lastValue = LAST + itemId;
                                String phaseValue = OTHERPHASE + itemId;
                                switch (valueType) {
                                    case "currentValue":
                                        // 当前值
                                        currItems.add(itemId);
                                        str1 = currValue;
                                        break;
                                    case "preValue":
                                        // 初始值
                                        lastItems.add(itemId);
                                        str1 = lastValue;
                                        break;
                                    case "changeValue":
                                        // (当前值-上次值)
                                        currItems.add(itemId);
                                        lastItems.add(itemId);
                                        str1 = "("+currValue+"-"+lastValue+")";
                                        break;
                                    case "changeRate":
                                        // (当前值-上次值)/上次值
                                        currItems.add(itemId);
                                        lastItems.add(itemId);
                                        str1 = "("+currValue+"-"+lastValue+")/"+lastValue;
                                        break;
                                    case "tempRiseValue":  //温升
                                        // (当前值-环境温度)
                                        currItems.add(itemId);
                                        envTemp = ENVTEMP;
                                        str1 = "("+currValue+"-"+ENVTEMP+")";
                                        break;
                                    case "relativeTempDiff": //相对温差
                                        // (当前值-其他相值)/(当前值-环境温度)
                                        currItems.add(itemId);
                                        phaseItems.add(itemId);
                                        envTemp = ENVTEMP;
                                        str1 = "("+currValue+"-"+phaseValue+")/("+currValue+"-"+ENVTEMP+")";
                                        break;
                                }
                                str2 = itemName+valueName;
                            }
                            break;
                        }
                    }
                    break;
                }
            }
            if (str1 == null) {
                throw new Exception("表达式错误，" + obj);
            }
            lastObj = obj;
            lastLen1 = str1.length();
            lastLen2 = str2.length();
            expStr1 += str1;
            expStr2 += str2;
        }
        return new ParseExpResult(expStr1, expStr2, currItems, lastItems, phaseItems, envTemp);
    }

    /**
     * 判断计算结果是否为Boolean来验证是否为逻辑值表达式
     * 表达式列表
     * @param expList
     * @return -2 表达式不合法, -1 非逻辑表达式 , 0 表达式为空 , 1 表达式合法
     */
    public static Result isBooleanExp(ArrayList<JSONObject> expList){
        try {
            ParseExpResult parseExpResult = AviatorUtils.parseExpList(expList);
            if (parseExpResult==null) {
                return new Result(0, "表达式为空");
            }
            Expression exp = AviatorEvaluator.compile(parseExpResult.getExpStr1(), true);
            Map<String, Object> env = new HashMap<>();
            for (String itemId: parseExpResult.getCurrItems()){
                env.put(CURR+itemId, 20);
            }
            for(String itemId: parseExpResult.getLastItems()){
                env.put(LAST+itemId, 10);
            }
            if (parseExpResult.getEnvTemp()!=null) {
                env.put(ENVTEMP, 10);
            }
            for(String itemId: parseExpResult.getPhaseItems()){
                env.put(OTHERPHASE+itemId, 20);
            }
            Object obj = exp.execute(env);
            if (obj instanceof Boolean){
                return new Result(1, "表达式合法", parseExpResult.getExpStr2());
            } else {
                return new Result(-1, "非逻辑表达式");
            }
        } catch (Exception e){
            e.printStackTrace();
            return new Result(-2, "表达式不合法");
        }
    }

    public static boolean execBooleanExp(Expression exp, Map<String, Object> env) throws Exception {
        if (exp==null) {
            return false;
        }
        try {
            Object obj = exp.execute(env);
            if (obj instanceof Boolean){
                return ((Boolean) obj).booleanValue();
            } else {
                return false;
            }
        } catch (Exception e){
            throw e;
        }
    }

    public static void main(String[] args) {
        ArrayList<JSONObject> expList = new ArrayList<>(Arrays.asList(
            JSONObject.parseObject("{\"type\":\"variable\",\"id\":\"ptzSetValue\",\"name\":\"检测点\",\"itemId\":\"10000\",\"itemName\":\"电压\",\"valueType\":\"currentValue\",\"valueName\":\"当前值\"}"),
            JSONObject.parseObject("{\"type\":\"mathOp\",\"id\":\"multiply\",\"name\":\"乘\"}"),
            JSONObject.parseObject("{\"type\":\"element\",\"id\":\"fixValue\",\"name\":\"固定值\",value:\"2\"}"),
            JSONObject.parseObject("{\"type\":\"relationOp\",\"id\":\"greaterEqualsThan\",\"name\":\"大于等于\"}"),
            JSONObject.parseObject("{\"type\":\"element\",\"id\":\"fixValue\",\"name\":\"固定值\",value:\"36\"}"),
            JSONObject.parseObject("{\"type\":\"logicOp\",\"id\":\"and\",\"name\":\"并且\"}"),
            JSONObject.parseObject("{\"type\":\"otherOp\",\"id\":\"leftParentheses\",\"name\":\"左括号\"}"),
            JSONObject.parseObject("{\"type\":\"variable\",\"id\":\"ptzSetValue\",\"name\":\"检测点\",\"itemId\":\"10001\",\"itemName\":\"温度A\",\"valueType\":\"currentValue\",\"valueName\":\"当前值\"}"),
            JSONObject.parseObject("{\"type\":\"mathOp\",\"id\":\"subtract\",\"name\":\"减\"}"),
            JSONObject.parseObject("{\"type\":\"variable\",\"id\":\"ptzSetValue\",\"name\":\"检测点\",\"itemId\":\"10002\",\"itemName\":\"温度B\",\"valueType\":\"currentValue\",\"valueName\":\"当前值\"}"),
            JSONObject.parseObject("{\"type\":\"otherOp\",\"id\":\"rightParentheses\",\"name\":\"右括号\"}"),
            JSONObject.parseObject("{\"type\":\"function\",\"id\":\"abs\",\"name\":\"绝对值\"}"),
            JSONObject.parseObject("{\"type\":\"function\",\"id\":\"between\",\"name\":\"在区间\",\"minValue\":\"1\",\"maxValue\":\"5\"}")
        ));
        System.out.println(expList);

        // 验证表达式是否合法
        Result result = AviatorUtils.isBooleanExp(expList);
        System.out.println(result);

        // 计算表达式
        try {
            ParseExpResult parseExpResult = AviatorUtils.parseExpList(expList);
            if (parseExpResult==null) {
                return;
            }
            Expression exp = AviatorEvaluator.compile(parseExpResult.getExpStr1(), true);
            Map<String, Object> env = new HashMap<>();
            for (String itemId: parseExpResult.getCurrItems()){
                env.put(CURR+itemId, 2);
            }
            for(String itemId: parseExpResult.getLastItems()){
                env.put(LAST+itemId, 1);
            }
            if (parseExpResult.getEnvTemp()!=null) {
                env.put(ENVTEMP, 1);
            }
            for(String itemId: parseExpResult.getPhaseItems()){
                env.put(OTHERPHASE+itemId, 2);
            }
            Object obj =  AviatorUtils.execBooleanExp(exp, env);
            System.out.println(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
package com.wootion.utiles.aviator;

import java.util.ArrayList;
import java.util.Map;

/**
 * @Author: majunhui
 * @Date: 2019/1/25 0025
 * @Version 1.0
 */

// 解析表达式结果
public class ParseExpResult {
    // 表达式字符串(用于计算)
    private String expStr1;
    // 表达式字符串(用于展示)
    private String expStr2;
    // 当前值
    private ArrayList<String> currItems;
    // 上次值
    private ArrayList<String> lastItems;
    // 其它相位值
    private ArrayList<String> phaseItems;
    // 环境温度
    private String envTemp;

    public ParseExpResult(String expStr1, String expStr2, ArrayList<String> currItems, ArrayList<String> lastItems, ArrayList<String> phaseItems, String envTemp) {
        this.expStr1 = expStr1;
        this.expStr2 = expStr2;
        this.currItems = currItems;
        this.lastItems = lastItems;
        this.phaseItems = phaseItems;
        this.envTemp = envTemp;
    }

    public String getExpStr1() {
        return expStr1;
    }

    public String getExpStr2() {
        return expStr2;
    }

    public ArrayList<String> getCurrItems() {
        return currItems;
    }

    public ArrayList<String> getLastItems() {
        return lastItems;
    }

    public ArrayList<String> getPhaseItems() {
        return phaseItems;
    }

    public String getEnvTemp() {
        return envTemp;
    }
}

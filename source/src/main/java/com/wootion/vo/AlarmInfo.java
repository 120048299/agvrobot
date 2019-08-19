package com.wootion.vo;

import com.alibaba.fastjson.JSONObject;
import com.wootion.model.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AlarmInfo {
    private String alarmCodeName;//点位告警名称
    private boolean defaultTemplateFlag;//是否设置为默认告警条件
    private String siteId;
    private ArrayList<String> ptzSetIds;
    private String ptzSetId;
    private PtzAlarmConfig ptzAlarmConfig;
    private AlarmCode alarmCode;

    private ArrayList<JSONObject> editExpItemList;
    private ArrayList<JSONObject> earlyExpItemList;
    private  ArrayList<JSONObject> commonlyExpItemList;
    private  ArrayList<JSONObject> majorExpItemList;
    private  ArrayList<JSONObject> dangerExpItemList;

    private RegzObject regzObject;
    private List<RegzObjectField> fields;
    private Integer editLevel;
    private String editExp;

    private Integer editMode;
    private String selectedTemplateId;
    private Integer saveTemplateType;

    private AlarmExpression earlyExp;
    private AlarmExpression commonlyExp;
    private AlarmExpression majorExp;
    private AlarmExpression dangerExp;
}

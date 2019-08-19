package com.wootion.service;

import com.github.pagehelper.PageInfo;
import com.wootion.commons.Result;
import com.wootion.model.AlarmCode;
import com.wootion.model.AlarmLog;
import com.wootion.model.AlarmSub;
import com.wootion.vo.AlarmInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface AlarmService {



    AlarmInfo getAlarmInfo(String ptzSetId);
    int saveAlarmInfo(AlarmInfo alarmInfo);
    int saveAlarmInfos(AlarmInfo alarmInfo);
    int deleteAlarmInfo(String ptzSetId);

    int addAlarmSub(List<AlarmSub> list);
    int deleteAlarmSub(List<String> list);
    int modifyAlarmSub(AlarmSub alarmSub);

    int saveAsTemplate(AlarmInfo info,String siteId);
    List<AlarmCode> getAlarmTemplates(String siteId,String ptzSetId);
    AlarmInfo getTemplateInfo(String uid);
    int updateTemplate(AlarmInfo info,String siteId);
    Result applyTemplateToPtzSets(String templateId, List<String> ptzSetIds);
    int deleteTemplate(String uid);

}

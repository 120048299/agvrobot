package com.wootion.task.alarm;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.wootion.model.SysAlarmConfig;
import com.wootion.model.SysAlarmLog;
import com.wootion.utiles.DataCache;
import com.wootion.utiles.aviator.AviatorUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: majunhui
 * @Date: 2019/2/28 0028
 * @Version 1.0
 */
public class SysAlarmHelper {

    /**
     * 根据告警码和值判断是否有新告警或者告警已恢复
     * @param alarmCode
     * @param value
     * @return
     */
    public static SysAlarmLog checkSysAlarm(String siteId, String sourceId, String alarmCode, Object value, String desc) {
        if (siteId==null || sourceId==null || alarmCode==null || value==null) {
            return null;
        }
        boolean alarm = false;
        SysAlarmConfig config = DataCache.findSysAlarmConfig(alarmCode);
        if (config==null) {
            return null;
        }
        String expStr = config.getAlarmExp();
        // 没有表达式, 则检测告警位的值是否为1
        if (expStr==null || expStr.length()==0) {
            alarm = ((Integer)value==1);
        } else {
            Expression exp = AviatorEvaluator.compile(expStr, true);
            Map<String, Object> env = new HashMap<>();
            env.put("X", value);
            try {
                alarm = AviatorUtils.execBooleanExp(exp, env);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        String key = getKey(siteId, config.getSourceType(), sourceId, alarmCode);
        SysAlarmLog sysAlarmLog = DataCache.findFromSysAlarmLogMap(key);
        if (alarm) {
            // 新告警
            if (sysAlarmLog==null) {
                sysAlarmLog = new SysAlarmLog();
                sysAlarmLog.setAlarmCode(alarmCode);
                sysAlarmLog.setAlarmType(config.getAlarmType());
                sysAlarmLog.setAlarmLevel(config.getAlarmLevel());
                sysAlarmLog.setSourceType(config.getSourceType());
                sysAlarmLog.setAlarmExp(config.getAlarmExp());
                sysAlarmLog.setSourceId(sourceId);
                sysAlarmLog.setAlarmTime(new Date());
                sysAlarmLog.setStatus(0);
                String allDesc = (desc==null)?"":desc;
                if (expStr!=null) {
                    allDesc += "当前值:"+ value +", 告警条件:" + expStr;
                }
                sysAlarmLog.setDescription(allDesc);
                sysAlarmLog.setSiteId(siteId);
                return sysAlarmLog;
            }
        } else {
            // 告警恢复
            if (sysAlarmLog!=null) {
                sysAlarmLog.setRemoveTime(new Date());
                sysAlarmLog.setStatus(1);
                return sysAlarmLog;
            }
        }
        return null;
    }

    public static boolean dealSysAlarm(String siteId, String sourceId, String alarmCode, Object value, String desc) {
        SysAlarmLog sysAlarmLog = checkSysAlarm(siteId, sourceId, alarmCode, value, desc);
        if (sysAlarmLog!=null) {
            if (sysAlarmLog.getStatus()==0) {
                DataCache.addToSysAlarmLogMap(sysAlarmLog);
            } else {
                DataCache.removeFromSysAlarmLogMap(sysAlarmLog);
            }
            return true;
        }
        return false;
    }

    public static String getKey(String siteId, Integer sourceType, String sourceId, String alarmCode) {
        return siteId+ "-" + sourceType +  "-" + sourceId + "-" + alarmCode;
    }

    public static String getKey(SysAlarmLog log) {
        return getKey(log.getSiteId(), log.getSourceType(), log.getSourceId(), log.getAlarmCode());
    }

}

package com.wootion.mapper;

import com.wootion.model.SysAlarmLog;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: majunhui
 * @Date: 2018/12/20
 * @Version 1.0
 */

@Component
@Mapper
public interface SysAlarmLogMapper {
//    @Results(id = "SysAlarmLogMap", value = {
//            @Result(property = "alarmCode", column = "alarm_code"),
//            @Result(property = "sourceId", column = "source_id"),
//            @Result(property = "alarmTime", column = "alarm_time"),
//            @Result(property = "removeTime", column = "remove_time"),
//            @Result(property = "sourceType", column = "source_type"),
//            @Result(property = "alarmType", column = "alarm_type"),
//            @Result(property = "alarmLevel", column = "alarm_level"),
//            @Result(property = "alarmExp", column = "alarm_exp"),
//            @Result(property = "siteId", column = "site_id")
//    })
    @Select("SELECT log.*, cfg.source_type, cfg.alarm_type, cfg.alarm_level, cfg.alarm_exp " +
            "FROM sys_alarm_log log, sys_alarm_config cfg " +
            "WHERE log.alarm_code = cfg.alarm_code")
    List<SysAlarmLog> findAll();

//    @ResultMap("SysAlarmLogMap")
    @SelectProvider(type = SysAlarmLogProvider.class, method = "findListWhere")
    List<SysAlarmLog> findListWhere(@Param("siteId") String siteId,
                                    @Param("status") Integer status);

    @SelectKey(statement="SELECT replace(uuid(), '-', '')", keyProperty="uid", before=true, resultType = String.class)
    @Options(keyProperty = "uid", useGeneratedKeys = true)
    @Insert("INSERT INTO sys_alarm_log(uid, alarm_code, source_id, alarm_time, status, description, site_id)" +
            "VALUES(#{uid}, #{alarmCode}, #{sourceId}, #{alarmTime}, #{status}, #{description}, #{siteId})")
    int insert(SysAlarmLog sysAlarmLog);

    @Update("UPDATE sys_alarm_log SET status=#{status}, remove_time=#{removeTime} WHERE uid=#{uid}")
    int update(SysAlarmLog sysAlarmLog);

    // Provider
    class SysAlarmLogProvider {
        public String findListWhere(@Param("siteId") String siteId,
                                    @Param("status") Integer status) {
            String sql = new SQL(){{
                SELECT("log.*");
                SELECT("cfg.source_type");
                SELECT("cfg.alarm_type");
                SELECT("cfg.alarm_level");
                SELECT("cfg.alarm_exp");
                FROM("sys_alarm_log log");
                FROM("sys_alarm_config cfg");
                WHERE("log.alarm_code = cfg.alarm_code");
                if (siteId!=null) {
                    WHERE("log.site_id = #{siteId}");
                }
                if (status!=null) {
                    WHERE("log.status = #{status}");
                }
                ORDER_BY("log.alarm_time DESC");
            }}.toString();
            return sql;
        }
    }
}



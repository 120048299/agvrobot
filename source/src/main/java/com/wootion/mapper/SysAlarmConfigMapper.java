package com.wootion.mapper;

import com.wootion.model.SysAlarmConfig;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: majunhui
 * @Date: 2018/12/20
 * @Version 1.0
 */

@Component
@Mapper
public interface SysAlarmConfigMapper {

    @Select("SELECT * FROM sys_alarm_config")
    List<SysAlarmConfig> findAll();

    @Update("UPDATE sys_alarm_config SET alarm_exp=#{alarmExp} WHERE alarm_code=#{alarmCode}")
    int updateAlarmExp(SysAlarmConfig sysAlarmConfig);
}



package com.wootion.mapper;

import com.wootion.model.SystemLog;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;


@Component
@Mapper
public interface SystemLogMapper {

    @SelectKey(statement="SELECT replace(uuid(), '-', '')", keyProperty="uid", before=true, resultType = String.class)
    @Options(keyProperty = "uid", useGeneratedKeys = true)
    @Insert("INSERT INTO system_log( uid, site_id, robot_id, event, result, log_time,desc,memo)" +
            "VALUES(#{uid}, #{siteId}, #{robotId},#{event},#{result},#{log_time},#{desc}, #{memo} ")
    int insert(SystemLog robotEventLog);



    @Select("select * from system_log where uid=#{uid}")
    SystemLog select(String uid);



}
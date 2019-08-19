package com.wootion.mapper;

import com.wootion.model.RobotParam;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Author: btrmg
 * @Date: 2018/12/20
 * @Version 1.0
 */

@Component
@Mapper
public interface RobotParamMapper {
    @Select("select * from robot_param where robot_id=#{robotId}" )
    List<RobotParam> getRobotParam(String robotId);

    @Update("UPDATE robot_param t1 SET t1.value=#{value} WHERE t1.robot_id=#{robotId} and t1.key=#{key}")
    int updateRobotParam(@Param("robotId")String uid,@Param("key") String key,@Param("value") String value);

    @Select("select count(1) from task_log " )
    int getTotalInspectiDevs( );

    @Select("select count(1) from alarm_log " )
    int getTotalDefects( );

    @Select("select sum(TIMEDIFF(real_end_time,real_start_time)) from task_plan")
    int getTotalRunTime( );

    @Select("select param.key, param.name,param.value,param.desc from robot_param param where robot_id=#{robotId}" )
    List<Map> sysRobotParam(String robotId);

}



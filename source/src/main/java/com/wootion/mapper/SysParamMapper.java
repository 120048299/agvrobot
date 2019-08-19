package com.wootion.mapper;

import com.wootion.model.SysParam;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Author: majunhui
 * @Date: 2018/12/20
 * @Version 1.0
 */

@Component
@Mapper
public interface SysParamMapper {
    @Select("SELECT * FROM sys_param ORDER BY uid")
    List<SysParam> findAll();

    @Select("SELECT * FROM sys_param WHERE uid=#{uid}")
    SysParam findByUid(@Param("uid") String uid);

    @Update("UPDATE sys_param SET value=#{value} WHERE uid=#{uid}")
    int update(SysParam sysParam);

    @Update("UPDATE sys_param SET value=#{value} WHERE sys_param.key=#{key}")
    int updateByKey(@Param("key") String key, @Param("value") String value);

    @Update("UPDATE sys_param SET value=#{value} WHERE uid=#{uid}")
    int updateByUid(@Param("uid") String uid, @Param("value") String value);

    @Select("select param.key, param.name,param.value,param.desc  from sys_param param where param.key like 'robot%' or param.key like 'task%' or param.key like 'chargeRoom%' ")
    List<Map> sysDataSyn();

}



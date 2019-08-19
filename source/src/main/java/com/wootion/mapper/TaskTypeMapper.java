package com.wootion.mapper;

import com.wootion.model.Task;
import com.wootion.model.TaskType;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@Mapper
public interface TaskTypeMapper {

    @Select("select * from task_type where uid=#{uid}")
    TaskType select(@Param("uid")String uid);

    @Select("select * from task_type")
    List<TaskType> selectAll();



}
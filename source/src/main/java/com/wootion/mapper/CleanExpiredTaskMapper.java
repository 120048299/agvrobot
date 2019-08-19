package com.wootion.mapper;

import com.wootion.model.Robot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface CleanExpiredTaskMapper {

    @Update(" call deal_expired_task")
    int cleanExpiredTask();
}



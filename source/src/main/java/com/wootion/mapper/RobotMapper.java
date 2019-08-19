package com.wootion.mapper;

import com.wootion.model.Robot;
import com.wootion.model.Site;
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
public interface RobotMapper {
    @Select("SELECT * FROM robot")
    List<Robot> findAll();

    @Select("SELECT * FROM robot WHERE uid=#{uid} ")
    Robot select(@Param("uid") String uid);

    @Select("SELECT * FROM robot WHERE site_id=#{siteId} and status=1")
    List<Robot> findListBySiteId(@Param("siteId") String siteId);

    @Update("UPDATE robot SET site_id=#{siteId} WHERE uid=#{uid}")
    int updateRobotSite(Robot robot);

    @Update("UPDATE robot SET status=#{status} WHERE uid=#{uid}")
    int updateRobotStatus(Robot robot);
}



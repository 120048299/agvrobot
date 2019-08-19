package com.wootion.mapper;

import com.wootion.model.WeatherStation;
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
public interface WeatherStationMapper {

    @Select("SELECT * FROM weather_station")
    List<WeatherStation> findAll();

    @Select("SELECT * FROM weather_station WHERE site_id=#{siteId}")
    List<WeatherStation> findListBySiteId(@Param("siteId") String siteId);
}



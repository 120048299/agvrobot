package com.wootion.mapper;

import com.wootion.model.MaintainArea;
import com.wootion.model.Site;
import com.wootion.model.SystemLog;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Mapper
public interface MaintainAreaMapper {

    @SelectKey(statement=" select concat('area',getTableId('maintain_area'))", keyProperty="uid", before=true, resultType = String.class)
    @Options(keyProperty = "uid", useGeneratedKeys = true)
    @Insert("INSERT INTO maintain_area ( uid,name,modify_time,point1,point2,point3,point4,point5,maintain_type,memo,site_id)" +
            "VALUES(#{uid}, #{name}, #{modifyTime},#{point1},#{point2},#{point3},#{point4}, #{point5},#{maintainType},#{memo}, #{siteId}) ")
    int insert(MaintainArea maintainArea);

    @Delete("delete from maintain_area where uid=#{uid}")
    int delete(String uid);


    @Select("select * from maintain_area where uid=#{uid}")
    MaintainArea select(String uid);

    @Select("select * from maintain_area where site_id=#{site_id}")
    List<Map> getMaintainOrobstacleList(String siteId);

    @Select("select * from maintain_area where uid=#{name}")
    List<MaintainArea> getByName(String name);

    @Select("SELECT * FROM maintain_area where site_id=#{site_id}")
    List<MaintainArea> getAll(String siteId);




}



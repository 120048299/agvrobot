package com.wootion.mapper;

import com.wootion.model.ChargeRoom;
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
public interface ChargeRoomMapper {
    @Select("SELECT * FROM charge_room")
    List<ChargeRoom> findAll();

    @Select("SELECT * FROM charge_room WHERE site_id=#{siteId}")
    List<ChargeRoom> findListBySiteId(@Param("siteId") String siteId);

    @Select("SELECT * FROM charge_room WHERE site_id=#{siteId}")
    List<Map> getChargeRoom(@Param("siteId") String siteId);


    @SelectKey(statement=" select concat('room',getTableId('charge_room'))", keyProperty="uid", before=true, resultType = String.class)
    @Options(keyProperty = "uid", useGeneratedKeys = true)
    @Insert("INSERT INTO charge_room ( uid,code,name,status,addr,site_id,description,corners)" +
            "VALUES(#{uid}, #{code}, #{name},#{status},#{addr},#{siteId},#{description}, #{corners}) ")
    int insert(ChargeRoom chargeRoom);


    @Delete("delete from charge_room where uid=#{uid}")
    int delete(String uid);
 }


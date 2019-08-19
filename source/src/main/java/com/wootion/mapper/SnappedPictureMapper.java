package com.wootion.mapper;

import com.wootion.model.SnappedPicture;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @Author: majunhui
 * @Date: 2018/12/20
 * @Version 1.0
 */

@Component
@Mapper
public interface SnappedPictureMapper {
    @SelectKey(statement="SELECT replace(uuid(), '-', '')", keyProperty="uid", before=true, resultType = String.class)
    @Options(keyProperty = "uid", useGeneratedKeys = true)
    @Insert("INSERT INTO snapped_picture(uid, file_name, memo, is_infra, create_time,site_id)" +
            "VALUES(#{uid}, #{fileName}, #{memo}, #{isInfra}, #{createTime},#{siteId})")
    int insert(SnappedPicture snappedPicture);

    @Delete("delete from snapped_picture where uid=#{uid}")
    int delete(String uid);

    @Select("SELECT * FROM snapped_picture")
    List<SnappedPicture> findAll();

    @Select("SELECT * FROM snapped_picture WHERE site_id=#{siteId} and create_time >=#{fromTime} and create_time <=#{toTime} order by create_time desc")
    List<SnappedPicture> findList(@Param("siteId") String siteId, @Param("fromTime") String fromTime,
                                  @Param("toTime") String toTime);
}



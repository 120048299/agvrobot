package com.wootion.mapper;

import com.wootion.model.Resource;
import com.wootion.model.Role;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface ResourceMapper {

    @Select("select * from resource where uid=#{uid}")
    Resource select(@Param("uid") String uid);

    @Select("select * from resource res where res.uid in (select resource_id from role_resource t2 where t2.role_id= #{roleId} )")
    List<Resource> selectResourceByRoleId(@Param("roleId") String roleId);

    @Select("select * from resource  ")
    List<Resource> selectAll();
}



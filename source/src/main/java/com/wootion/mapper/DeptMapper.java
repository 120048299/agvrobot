package com.wootion.mapper;

import com.wootion.model.Dept;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: btrmg
 * @Date: 2018/12/20
 * @Version 1.0
 */

@Component
@Mapper
public interface DeptMapper {
    @Select("select * from dept where uid !='00000001' order by level asc")
    List<Dept> findAll();

    @Select("select * from dept where uid ='00000001'")
    Dept findCompany( );

    @SelectKey(statement="SELECT replace(uuid(), '-', '')", keyProperty="uid", before=true, resultType = String.class)
    @Options(keyProperty = "uid", useGeneratedKeys = true)
    @Insert("INSERT INTO dept(uid, name, code, parent_id, level)" +
            "VALUES(#{uid}, #{name}, #{code}, #{parentId}, #{level})")
    int insert(Dept dept);

    @Update("UPDATE dept SET name=#{name},code= #{code} WHERE uid=#{uid}")
    int update(Dept dept);

    @Delete("delete from dept where uid=#{uid}")
    int delete(String uid);

    @Select("select * from dept where parent_id =#{uid}")
    List<Dept> getSubDept(String uid);



}



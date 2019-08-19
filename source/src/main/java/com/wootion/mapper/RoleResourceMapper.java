package com.wootion.mapper;

import com.wootion.model.Role;
import com.wootion.model.RoleResource;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface RoleResourceMapper {
    @SelectKey(statement="SELECT replace(uuid(), '-', '')", keyProperty="uid", before=true, resultType = String.class)
    @Options(keyProperty = "uid", useGeneratedKeys = true)
    @InsertProvider(type = RoleResourceSqlProvider.class, method = "insert")
    int insert(RoleResource record);

    @UpdateProvider(type = RoleResourceSqlProvider.class, method = "update")
    int update(RoleResource record);

    @Delete("delete from role_resource where uid=#{uid}")
    int delete(@Param("uid") String uid);

    @Select("select * from role_resource where uid=#{uid}")
    RoleResource select(@Param("uid") String uid);

    @Select("select * from role_resource  ")
    List<RoleResource> selectAll();

    @Delete("DELETE FROM role_resource WHERE role_id = #{roleId}")
    int deleteByRoleId(@Param("roleId") String roleId);

    class RoleResourceSqlProvider {
        public String insert(RoleResource record) {
            return new SQL() {
                {
                    INSERT_INTO("role_resource");
                    if (record.getUid()!= null) {
                        VALUES("uid", "#{uid}");
                    }
                    if (record.getRoleId()!= null) {
                        VALUES("role_id", "#{roleId}");
                    }
                    if (record.getResourceId()!= null) {
                        VALUES("resource_id", "#{resourceId}");
                    }
                }
            }.toString();
        }

        public String update(RoleResource record) {
            return new SQL() {
                {
                    UPDATE("role_resource");
                    if (record.getUid()!= null) {
                        SET("uid = #{uid}");
                    }
                    if (record.getRoleId()!= null) {
                        SET("role_id = #{roleId}");
                    }
                    if (record.getResourceId()!= null) {
                        SET("resource_id = #{resourceId}");
                    }
                    WHERE("uid = #{uid} " );
                }
            }.toString();
        }
    }
}



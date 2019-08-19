package com.wootion.mapper;

import com.wootion.model.Job;
import com.wootion.model.Role;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Mapper
public interface RoleMapper {
    @SelectKey(statement="SELECT replace(uuid(), '-', '')", keyProperty="uid", before=true, resultType = String.class)
    @Options(keyProperty = "uid", useGeneratedKeys = true)
    @InsertProvider(type = RoleSqlProvider.class, method = "insert")
    int insert(Role record);

    @UpdateProvider(type = RoleSqlProvider.class, method = "update")
    int update(Role record);

    @Delete("delete from role where uid=#{uid}")
    int delete(@Param("uid") String uid);

    @Select("select * from role where uid=#{uid}")
    Role select(@Param("uid") String uid);

    @Select("select * from role  ")
    List<Role> selectAll();
    @Select("SELECT a.* FROM role a, user_info b  WHERE a.uid = b.roleid and b.uid = #{userId})  ")
    List<Role> selectRoleByUserId(@Param("userId") String userId);


    @SelectProvider(type = RoleSqlProvider.class, method = "findListWhere")
    List<Role> selectRole(@Param("roleName") String roleName);

    class RoleSqlProvider {
        public String findListWhere(@Param("roleName") String roleName) {
            String sql = new SQL(){{
                SELECT("*");
                FROM(" role ");
                if (roleName!=null && !"".equals(roleName)) {
                    WHERE(" role like \"%"+roleName+"%\" ");
                }
            }}.toString();
            return sql;
        }
        public String insert(Role record) {
            return new SQL() {
                {
                    INSERT_INTO("role");
                    if (record.getUid()!= null) {
                        VALUES("uid", "#{uid}");
                    }
                    if (record.getRole()!= null) {
                        VALUES("role", "#{role}");
                    }
                    if (record.getDescription()!= null) {
                        VALUES("description", "#{description}");
                    }
                    if (record.getAvailable()!= null) {
                        VALUES("available", "#{available}");
                    }
                }
            }.toString();
        }

        public String update(Role record) {
            return new SQL() {
                {
                    UPDATE("role");
                    if (record.getUid()!= null) {
                        SET("uid = #{uid}");
                    }
                    if (record.getRole()!= null) {
                        SET("role = #{role}");
                    }
                    if (record.getDescription()!= null) {
                        SET("description = #{description}");
                    }
                    if (record.getAvailable()!= null) {
                        SET("available = #{available}");
                    }
                    WHERE("uid = #{uid} " );
                }
            }.toString();
        }
    }
}



package com.wootion.mapper;

import com.wootion.model.Role;
import com.wootion.model.UserInfo;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: btrmg
 * @Date: 2018/12/20
 * @Version 1.0
 */

@Component
@Mapper
public interface UserInfoMapper {

    @SelectKey(statement="SELECT replace(uuid(), '-', '')", keyProperty="uid", before=true, resultType = String.class)
    @Options(keyProperty = "uid", useGeneratedKeys = true)
    @Insert("INSERT INTO user_info(uid, username, deptid, roleid, workcord,password,loginname," +
            "faxno,flagsex,emailno,officialno,msisdn,position,address,modifytime,priority," +
            "reserv2,reserv1,status)" +
            " VALUES(#{uid}, #{username}, #{deptid}, #{roleid}, #{workcord},#{password},#{loginname}," +
            "#{faxno},#{flagsex},#{emailno},#{officialno},#{msisdn},#{position},#{address},#{modifytime}," +
            "null,null,null,0)")
    int insert(UserInfo userInfo);


    @Select("select * from user_info where uid=#{uid}")
    UserInfo select(String uid);

    @Select("select * from user_info ")
    List<UserInfo> selectAll();

    @Select("select * from user_info where login_name=#{name}")
    UserInfo selectByName(String name);

    @Select("select a.*,b.role as rolename from user_info a, role b where deptid=#{deptid} and a.roleid=b.uid" )
    List<UserInfo> findUserByDeptId(String deptid);


    @Update("UPDATE user_info SET username=#{username},deptid= #{deptid},roleid=#{roleid}," +
            "workcord=#{workcord}, password=#{password}, loginname=#{loginname}, faxno=#{faxno}," +
            "flagsex=#{flagsex},emailno=#{emailno},officialno=#{officialno},msisdn=#{msisdn}," +
            "position=#{position},address=#{address}, modifytime=#{modifytime} WHERE uid=#{uid}")
    int update(UserInfo userInfo);

    @Delete("delete from user_info where uid=#{uid}")
    int deleteUserInfo(String uid);

    @Select("select * from role")
    List<Role> getAllRole( );

    @Select("select user.*, dept.name as dept_name from user_info user, dept where user.loginname=#{loginname} and user.deptid=dept.uid")
    UserInfo getUserByLoginname(String loginname);

    @Select("select a.* FROM role a, user_info b  WHERE a.uid = b.roleid and b.uid =#{userid}")
    List<Role> getRoleByUserId(String userid );

    @SelectProvider(type = SqlProvider.class, method = "findListWhere")
    List<UserInfo> queryByText(@Param("searchText") String searchText);

    @Update("update user_info  set password = #{password} where loginname=#{loginname}")
    int changePassword(@Param("loginName") String loginName, @Param("password") String password);

    class SqlProvider {
        public String findListWhere(@Param("searchText") String searchText) {
            String sql = new SQL() {{
                SELECT("*");
                FROM(" user_info ");
                if (searchText != null && !"".equals(searchText)) {
                    WHERE("     ( username like \"%"+searchText+"%\" or" +
                            "                workcord  like \"%"+searchText+"%\" or" +
                            "                loginname like\"%"+searchText+"%\"  )");
                }
            }}.toString();
            return sql;
        }
    }
}



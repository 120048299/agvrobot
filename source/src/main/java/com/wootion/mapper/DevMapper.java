package com.wootion.mapper;

import com.wootion.model.Dev;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component
@Mapper
public interface DevMapper {
    @SelectKey(statement = " select concat('dev',getTableId('dev'))", keyProperty = "uid", before = true, resultType = String.class)
    @InsertProvider(type = DevSqlProvider.class, method = "insert")
    @Options(keyProperty = "uid", useGeneratedKeys = true)
    int insert(Dev record);

    @Update("UPDATE dev SET name=#{name},code= #{code} WHERE uid=#{uid}")
    int update(Dev record);

    @Delete("delete from dev where uid=#{uid}")
    int delete(String uid);

    @Select( "  select * from dev where uid=#{uid} ")
    Dev select(@Param("uid") String uid);

    @Select( "  select * from dev where dev.site_id=#{siteId} ")
    List<Dev> selectAll(@Param("siteId") String siteId);

    @Select( " SELECT  * FROM dev WHERE parent_id = #{parentId} order by order_number")
    List<Dev> selectDevByParentId(@Param("parentId") String parentId);

    @SelectProvider(type = DevSqlProvider.class, method = "selectBigDev")
    List<Dev> selectBigDev(@Param("siteId") String siteId,
                           @Param("bayId") String bayId,@Param("searchText") String searchText);

    @Update(" update dev  set  params =#{params} where uid=#{devId}")
    int clearDevParams(@Param("devId") String devId ,@Param("params") String params);

    @Select( "  select uid,name,params,dev_type_id from dev where dev.site_id=#{siteId} and params !='' and is_system=0 " )
    List<Map> getDevIconList(@Param("siteId") String siteId);

    @Select( "   select * from dev     where parent_id=#{uid}     and site_id=#{siteId}" )
    List<Dev> selectSmallDevListData(@Param("uid") String uid,@Param("siteId") String siteId);

    @Select(" select ifnull(max(order_number),1) from dev where site_id=#{siteId}")
    Integer getMaxOrderNumber(@Param("siteId") String siteId);

    @Select( "   select * from dev where code=#{code} " )
    Dev getDevByCode(@Param("code") String code);

    class DevSqlProvider {
        public String selectBigDev(@Param("siteId") String siteId,
                                          @Param("bayId") String bayId,@Param("searchText") String searchText) {
            String sql = new SQL(){{
                SELECT(  "* ");
                FROM(" dev ");
                WHERE(" site_id=#{siteId}");
                if (bayId!=null) {
                    WHERE("  parent_id = #{bayId} ");
                }
                if (searchText!=null && !"".equals(searchText)) {
                    WHERE(" name like \"%"+searchText+"%\" ");
                }
                ORDER_BY("  parent_id,order_number ");
            }}.toString();
            return sql;
        }

        public String insert(Dev record) {
            return new SQL() {
                {
                    INSERT_INTO("dev");
                    if (record.getUid() != null) {
                        VALUES("uid", "#{uid}");
                    }
                    if (record.getName() != null) {
                        VALUES("name", "#{name}");
                    }
                    if (record.getParentId() != null) {
                        VALUES("parent_id", "#{parentId}");
                    }
                    if (record.getDevTypeId() != null) {
                        VALUES("dev_type_id", "#{devTypeId}");
                    }
                    if (record.getParams() != null) {
                        VALUES("params", "#{params}");
                    }
                    if (record.getStatus() != null) {
                        VALUES("status", "#{status}");
                    }
                    if (record.getIsSystem() != null) {
                        VALUES("is_system", "#{isSystem}");
                    }
                    if (record.getSiteId() != null) {
                        VALUES("site_id", "#{siteId}");
                    }
                    if (record.getOrderNumber() != null) {
                        VALUES("order_number", "#{orderNumber}");
                    }
                    if (record.getCode() != null) {
                        VALUES("code", "#{code}");
                    }
                }
            }.toString();
        }

        public String update(Dev record) {
            return new SQL() {
                {
                    UPDATE("task");
                    if (record.getUid() != null) {
                        SET("uid = #{uid}");
                    }
                    if (record.getName() != null) {
                        SET("name = #{name}");
                    }
                    if (record.getParentId() != null) {
                        SET("parent_id = #{parentId}");
                    }
                    if (record.getDevTypeId() != null) {
                        SET("dev_type_id = #{devTypeId}");
                    }
                    if (record.getParams() != null) {
                        SET("params = #{params}");
                    }
                    if (record.getStatus() != null) {
                        SET("status = #{status}");
                    }
                    if (record.getIsSystem() != null) {
                        SET("is_system = #{isSystem}");
                    }
                    if (record.getSiteId() != null) {
                        SET("site_id = #{siteId}");
                    }
                    if (record.getOrderNumber() != null) {
                        SET("order_number = #{orderNumber}");
                    }
                    if (record.getCode() != null) {
                        SET("code = #{code}");
                    }
                    WHERE("uid = #{uid} ");
                }
            }.toString();
        }

    }

}

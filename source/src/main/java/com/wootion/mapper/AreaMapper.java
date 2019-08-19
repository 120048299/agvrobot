package com.wootion.mapper;

import com.wootion.model.Area;
import com.wootion.model.Dev;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component
@Mapper
public interface AreaMapper {
    @SelectKey(statement = " select concat('area',getTableId('area'))", keyProperty = "uid", before = true, resultType = String.class)
    @InsertProvider(type = SqlProvider.class, method = "insert")
    @Options(keyProperty = "uid", useGeneratedKeys = true)
    int insert(Area record);

    @UpdateProvider(type = SqlProvider.class, method = "update")
    int update(Area record);

    @Delete("delete from area where uid=#{uid}")
    int delete(String uid);

    @Select( "  select * from area where uid=#{uid} ")
    Area select(@Param("uid") String uid);

    @Select( "  select * from area where site_id=#{siteId} order by order_number")
    List<Area> selectAll(@Param("siteId") String siteId);

    @Select(" select ifnull(max(order_number),1) from area where site_id=#{siteId}")
    Integer getMaxOrderNumber(@Param("siteId") String siteId);

    class SqlProvider {
        public String insert(Area record) {
            return new SQL() {
                {
                    INSERT_INTO("area");
                    if (record.getUid() != null) {
                        VALUES("uid", "#{uid}");
                    }
                    if (record.getName() != null) {
                        VALUES("name", "#{name}");
                    }
                    if (record.getParams() != null) {
                        VALUES("params", "#{params}");
                    }
                    if (record.getSiteId() != null) {
                        VALUES("site_id", "#{siteId}");
                    }
                    if (record.getOrderNumber() != null) {
                        VALUES("order_number", "#{orderNumber}");
                    }
                }
            }.toString();
        }

        public String update(Area record) {
            return new SQL() {
                {
                    UPDATE("area");
                    if (record.getUid() != null) {
                        SET("uid = #{uid}");
                    }
                    if (record.getName() != null) {
                        SET("name = #{name}");
                    }
                    if (record.getParams() != null) {
                        SET("params = #{params}");
                    }
                    if (record.getSiteId() != null) {
                        SET("site_id = #{siteId}");
                    }
                    if (record.getOrderNumber() != null) {
                        SET("order_number = #{orderNumber}");
                    }
                    WHERE("uid = #{uid} ");
                }
            }.toString();
        }

    }

}

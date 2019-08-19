package com.wootion.mapper;

import com.wootion.model.RunMark;
import com.wootion.model.Task;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@Mapper
public interface RunMarkMapper {
    @SelectKey(statement=" select getTableId('run_mark')", keyProperty="uid", before=true, resultType = String.class)
    @InsertProvider(type = SqlProvider.class, method = "insert")
    @Options(keyProperty = "uid", useGeneratedKeys = true)
    int insert( RunMark record);

    @UpdateProvider(type = SqlProvider.class, method = "update")
    int update( RunMark record);

    @Update("  update run_mark set status=#{status} where  uid=#{uid} " )
    int updateStatus(@Param("uid") String uid, @Param("status") int status);

    @Delete("delete from run_mark where uid=#{uid}")
    int delete(String uid);

    @Select("select * from run_mark where uid=#{uid}")
    RunMark select(String uid);

    @Select("select * from run_mark where site_id=#{siteId}")
    List< RunMark> getRunMarkBySiteId(@Param("siteId") String siteId);

    class SqlProvider {
        public String insert(RunMark record) {
            return new SQL() {
                {
                    INSERT_INTO("run_mark");
                    if (record.getUid() != null) {
                        VALUES("uid", "#{uid}");
                    }
                    if (record.getMarkName() != null) {
                        VALUES("mark_name", "#{markName}");
                    }
                    if (record.getStatus() != null) {
                        VALUES("status", "#{status}");
                    }

                    if (record.getLon() != null) {
                        VALUES("lon", "#{lon}");
                    }
                    if (record.getLat() != null) {
                        VALUES("lat", "#{lat}");
                    }
                    if (record.getSiteId() != null) {
                        VALUES("site_id", "#{siteId}");
                    }
                    if (record.getMoveStyle() != null) {
                        VALUES("move_style", "#{moveStyle}");
                    }
                }
            }.toString();
        }

        public String update(RunMark record) {
            return new SQL() {
                {
                    UPDATE("run_mark");
                    if (record.getMarkName() != null) {
                        SET("mark_name=#{markName}");
                    }
                    if (record.getStatus() != null) {
                        SET("status=#{status}");
                    }

                    if (record.getLon() != null) {
                        SET("lon=#{lon}");
                    }
                    if (record.getLat() != null) {
                        SET("lat=#{lat}");
                    }
                    if (record.getSiteId() != null) {
                        SET("site_id=#{siteId}");
                    }
                    if (record.getMoveStyle() != null) {
                        SET("move_style=#{moveStyle}");
                    }
                    WHERE("uid = #{uid} ");
                }
            }.toString();
        }
    }

}
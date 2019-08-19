package com.wootion.mapper;

import com.wootion.model.RunLine;
import com.wootion.model.RunMark;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Mapper
public interface RunLineMapper {
    @SelectKey(statement=" select concat('L',getTableId('run_line'))", keyProperty="uid", before=true, resultType = String.class)
    @InsertProvider(type = SqlProvider.class, method = "insert")
    @Options(keyProperty = "uid", useGeneratedKeys = true)
    int insert(RunLine record);

    @UpdateProvider(type = SqlProvider.class, method = "update")
    int update(RunLine record);

    @Update("  update run_line set status=#{status} where  uid=#{uid} " )
    int updateStatus(@Param("uid") String uid, @Param("status") int status);

    @Delete("delete from run_line where uid=#{uid}")
    int delete(String uid);

    @Delete("delete from run_line where mark_id1 = #{markId} or mark_id2= #{markId}")
    int deleteByMark(@Param("markId") String markId);

    @Delete("delete from run_line where (mark_id1 = #{markId1} or mark_id2= #{markId2} ) " +
            " or (mark_id1 = #{markId2} or mark_id2= #{markId1} ) ")
    int deleteByTwoMark(@Param("markId1") String markId1,@Param("markId2") String markId2);


    @Select("select * from run_line where uid=#{uid}")
    RunLine select(@Param("uid") String uid);

    @Select("select * from run_line where site_id=#{siteId}")
    List< RunLine> getRunLineBySiteId(@Param("siteId") String siteId);

    @Select(" select ifnull(max(line_Id),0) as line_Id  from run_line where  site_id=#{siteId}")
    Integer maxRunLineID(@Param("siteId") String siteId);

    @Select("  select l.line_name as linename, m.uid as markuid, m.mark_name as markname, m.lon, m.lat " +
            "  from run_mark m, run_line l " +
            "  where m.uid in (l.mark_id1,l.mark_id2) and m.site_id=#{siteId} and l.site_id=#{siteId} " +
            "  order by l.line_name ")
    List<Map> getRunlineListBySiteId(@Param("siteId") String siteId);

    class SqlProvider {
        public String insert(RunLine record) {
            return new SQL() {
                {
                    INSERT_INTO("run_line");
                    if (record.getUid() != null) {
                        VALUES("uid", "#{uid}");
                    }
                    if (record.getLineName() != null) {
                        VALUES("line_name", "#{lineName}");
                    }
                    if (record.getStatus() != null) {
                        VALUES("status", "#{status}");
                    }
                    if (record.getLineId() != null) {
                        VALUES("line_id", "#{lineId}");
                    }
                    if (record.getMarkId1() != null) {
                        VALUES("mark_id1", "#{markId1}");
                    }
                    if (record.getMarkId2() != null) {
                        VALUES("mark_id2", "#{markId2}");
                    }
                    if (record.getSiteId() != null) {
                        VALUES("site_id", "#{siteId}");
                    }
                    if (record.getMaxVel() != null) {
                        VALUES("max_vel", "#{maxVel}");
                    }
                }
            }.toString();
        }

        public String update(RunLine record) {
            return new SQL() {
                {
                    UPDATE("run_line");
                    if (record.getLineName() != null) {
                        SET("line_name=#{lineName}");
                    }
                    if (record.getStatus() != null) {
                        SET("status=#{status}");
                    }
                    if (record.getLineId() != null) {
                        SET("line_id=#{lineId}");
                    }
                    if (record.getMarkId1() != null) {
                        SET("mark_id1=#{markId1}");
                    }
                    if (record.getMarkId2() != null) {
                        SET("mark_id2=#{markId2}");
                    }
                    if (record.getSiteId() != null) {
                        SET("site_id=#{siteId}");
                    }
                    if (record.getMaxVel() != null) {
                        SET("max_vel=#{maxVel}");
                    }

                    WHERE("uid = #{uid} ");
                }
            }.toString();
        }
    }

}
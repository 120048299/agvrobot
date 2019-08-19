package com.wootion.mapper;

import com.wootion.model.JobPathSection;
import com.wootion.model.JobPathSectionMark;
import com.wootion.model.TaskPtz;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component
@Mapper
public interface JobPathSectionMarkMapper {

    @Insert("INSERT INTO job_path_section_mark ( section_id,mark_id,must,mark_order)" +
            "VALUES(#{sectionId},  #{markId}, #{must},#{markOrder})")
    int insert(JobPathSectionMark record);

    @InsertProvider(type = SqlProvider.class, method = "insertInBatch")
    int insertBatch(@Param("sectionId") String sectionId,@Param("markIdList") List<Map>  markList);

    @Select("select * from job_path_section_mark where section_id=#{sectionId}  order by mark_order")
    List<JobPathSectionMark> select(@Param("sectionId") String sectionId);

    @Delete("delete from job_path_section_mark where section_id=#{sectionId}")
    int delete(@Param("sectionId") String sectionId);


    class SqlProvider {
        public String insertInBatch(@Param("sectionId") String sectionId,@Param("markIdList") List<Map> markList) {
            String sql = "insert into job_path_section_mark values";
            for (int i = 0; i < markList.size(); i++) {
                Map data=  markList.get(i);
                String markId = (String) data.get(i);
                Integer must = (Integer) data.get(i);
                String recordSql = "('" + sectionId + "','" +markId + "',"+ must + " ," + i + " )";
                if (i != markList.size() - 1) {
                    sql += recordSql + ",";
                } else {
                    sql += recordSql + ";";
                }
            }
            return sql;
        }
    }
}



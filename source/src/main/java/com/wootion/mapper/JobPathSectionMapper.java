package com.wootion.mapper;

import com.wootion.model.Dept;
import com.wootion.model.JobPathSection;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@Component
@Mapper
public interface JobPathSectionMapper {

    @SelectKey(statement="SELECT replace(uuid(), '-', '')", keyProperty="uid", before=true, resultType = String.class)
    @Options(keyProperty = "uid", useGeneratedKeys = true)
    @Insert("INSERT INTO job_path_section (uid,job_id, section_order)" +
            "VALUES(#{uid}, #{jobId}, #{sectionOrder})")
    int insert(JobPathSection record);

    @Select("select * from job_path_section where job_id=#{jobId}  order by section_order")
    List<JobPathSection> select(@Param("jobId") String jobId);

    @Delete("delete from job_path_section where job_id=#{jobId}")
    int delete(@Param("jobId") String jobId);



}



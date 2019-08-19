package com.wootion.mapper;

import com.wootion.model.RunMark;
import com.wootion.model.TaskPeriod;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component
@Mapper
public interface TaskPeriodMapper {



    @SelectKey(statement="  select replace(uuid(), '-', '')", keyProperty="uid", before=true, resultType = String.class)
    @InsertProvider(type = SqlProvider.class, method = "insert")
    @Options(keyProperty = "uid", useGeneratedKeys = true)
    int insert( TaskPeriod record);

    @UpdateProvider(type = SqlProvider.class, method = "update")
    int update( TaskPeriod record);

    @Delete("delete from task_period where uid=#{uid}")
    int delete(String uid);

    @Select("select * from task_period where uid=#{uid}")
    TaskPeriod select(String uid);

    @Select("select taskperiod.style,taskperiod.style_param as param,taskperiod.start_date,taskperiod.end_date,taskperiod.within_aday_time as run_times " +
            "from task_period  taskperiod where task_id=#{taskId}")
    List<Map> selectByTaskUid (@Param("taskId") String taskId);

    @Select("select *  from task_period  where task_id=#{taskId}")
    TaskPeriod selectByTaskId (@Param("taskId") String taskId);


    class SqlProvider {
        public String insert(TaskPeriod record) {
            return new SQL() {
                {
                    INSERT_INTO("task_period");
                    if (record.getUid() != null) {
                        VALUES("uid", "#{uid}");
                    }
                    if (record.getTaskId() != null) {
                        VALUES("task_id", "#{taskId}");
                    }
                    if (record.getStyle() != null) {
                        VALUES("style", "#{style}");
                    }
                    if (record.getStyleParam() != null) {
                        VALUES("style_param", "#{styleParam}");
                    }
                    if (record.getStartDate() != null) {
                        VALUES("start_date", "#{startDate}");
                    }
                    if (record.getEndDate() != null) {
                        VALUES("end_date", "#{endDate}");
                    }
                    if (record.getWithinAdayTime() != null) {
                        VALUES("within_aday_time", "#{withinAdayTime}");
                    }
                    if (record.getSiteId() != null) {
                        VALUES("site_id", "#{siteId}");
                    }
                }
            }.toString();
        }

        public String update(TaskPeriod record) {
            return new SQL() {
                {
                    UPDATE("task_period");
                    if (record.getTaskId() != null) {
                        SET("task_id=#{taskId}");
                    }
                    if (record.getStyle() != null) {
                        SET("style=#{style}");
                    }
                    if (record.getStyleParam() != null) {
                        SET("style_param=#{styleParam}");
                    }
                    if (record.getStartDate() != null) {
                        SET("start_date=#{startDate}");
                    }
                    if (record.getEndDate() != null) {
                        SET("end_date=#{endDate}");
                    }
                    if (record.getWithinAdayTime() != null) {
                        SET("within_aday_time=#{withinAdayTime}");
                    }
                    if (record.getSiteId() != null) {
                        SET("site_id=#{siteId}");
                    }
                    WHERE("uid = #{uid} ");
                }
            }.toString();
        }
    }
}

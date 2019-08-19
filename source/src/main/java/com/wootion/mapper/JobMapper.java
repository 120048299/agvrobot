package com.wootion.mapper;

import com.wootion.model.Job;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Mapper
public interface JobMapper {
    @InsertProvider(type = JobSqlProvider.class, method = "insert")
    int insert(Job job);

    @UpdateProvider(type = JobSqlProvider.class, method = "update")
    int update(Job job);

    @Delete("delete from job where uid=#{uid}")
    int delete(String uid);

    @Select("select * from job where uid=#{uid}")
    Job select(String uid);


    @Update("  update job set status=#{status} where  uid=#{uid} " )
    int updateStatus(@Param("uid") String uid,@Param("status") int status);

    @SelectProvider(type = JobSqlProvider.class, method = "findListWhere")
    List<Job> query(@Param("taskId") String taskId,@Param("siteId") String siteId, @Param("robotId") String robotId);

    @SelectProvider(type = JobSqlProvider.class, method = "findJobsList")
    List<Map> selectJobsList(@Param("taskId") String taskId,@Param("siteId") String siteId,@Param("fromDate") String fromDate,@Param("toDate") String toDate,
                                 @Param("taskName") String taskName,@Param("stautseList") List<Integer> stautseList,@Param("auditStatus") String auditStatus);

    @Select("select * from job where task_id=#{taskId}")
    List<Job> selectByTaskUid(String uid);

    @SelectProvider(type = JobSqlProvider.class, method = "selectByListMap")
    List<Map> queryTaskPlanListMap (@Param("siteId") String siteId);

    @SelectProvider(type = JobSqlProvider.class, method = "findJobCountByDate")
    List<Map> getJobCountByDate(@Param("beginDate") String beginDate,@Param("endDate") String endDate,@Param("siteId") String siteId,@Param("robotId") String robotId);

    @Select("select job.*  from task_log log,job job where log.job_id=job.uid  and job.audit_status=0 and job.status =4 and job.site_id=#{siteId}")
    List<Job> selectWaitAudit(String siteId);

    @Select("select * from job where site_id=#{siteId}  and status in (1,21,22,3,4,5,7)")
    List<Job> selectAllTaskPlan(String siteId);
    // Provider
    class JobSqlProvider {
        public String findListWhere(@Param("taskId") String taskId,
                                    @Param("siteId") String siteId,
                                    @Param("robotId") String robotId
        ) {
            String sql = new SQL(){{
                SELECT("*");
                FROM("job ");
                if (taskId!=null) {
                    WHERE(" task_id=#{taskId}");
                }
                if (siteId!=null) {
                    WHERE("site_id = #{siteId}");
                }
                if (robotId!=null) {
                    WHERE("robot_id = #{robot_id}");
                }
                ORDER_BY("plan_start_time DESC");
            }}.toString();
            return sql;
        }

        public String insert(Job job) {
            return new SQL() {
                {
                    INSERT_INTO("job");
                    if (job.getUid()!= null) {
                        VALUES("uid", "#{uid}");
                    }
                    if (job.getTaskId()!= null) {
                        VALUES("task_id", "#{taskId}");
                    }
                    if (job.getName()!= null) {
                        VALUES("name", "#{name}");
                    }
                    if (job.getCreateTime()!= null) {
                        VALUES("create_time", "#{createTime}");
                    }
                    if (job.getPlanStartTime()!= null) {
                        VALUES("plan_start_time", "#{planStartTime}");
                    }
                    if (job.getPlanEndTime()!= null) {
                        VALUES("plan_end_time", "#{planEndTime}");
                    }
                    if (job.getRealStartTime()!= null) {
                        VALUES("real_start_time", "#{realStartTime}");
                    }
                    if (job.getRealEndTime()!= null) {
                        VALUES("real_end_time", "#{realEndTime}");
                    }
                    if (job.getUserId()!= null) {
                        VALUES("user_id", "#{userId}");
                    }
                    VALUES("priority", "#{priority}");
                    VALUES("status", "#{status}");
                    if (job.getSiteId()!= null) {
                        VALUES("site_id", "#{siteId}");
                    }
                    if (job.getRobotId()!= null) {
                        VALUES("robot_id", "#{robotId}");
                    }
                    if (job.getPathImage()!= null) {
                        VALUES("path_image", "#{pathImage}");
                    }
                    if (job.getEndReason()!= null) {
                        VALUES("end_reason", "#{endReason}");
                    }

                }
            }.toString();
        }

        public String update(Job job) {
            return new SQL() {
                {
                    UPDATE("job");

                    if (job.getTaskId()!= null) {
                        SET("task_id = #{taskId}");
                    }
                    if (job.getName()!= null) {
                        SET("name = #{name}");
                    }
                    if (job.getCreateTime()!= null) {
                        SET("create_time = #{createTime}");
                    }
                    if (job.getPlanStartTime()!= null) {
                        SET("plan_start_time = #{planStartTime}");
                    }
                    if (job.getPlanEndTime()!= null) {
                        SET("plan_end_time = #{planEndTime}");
                    }
                    if (job.getRealStartTime()!= null) {
                        SET("real_start_time = #{realStartTime}");
                    }
                    if (job.getRealEndTime()!= null) {
                        SET("real_end_time = #{realEndTime}");
                    }
                    if (job.getUserId()!= null) {
                        SET("user_id = #{userId}");
                    }
                    SET("priority = #{priority}");
                    SET("status = #{status}");
                    if (job.getSiteId()!= null) {
                        SET("site_id = #{siteId}");
                    }
                    if (job.getRobotId()!= null) {
                        SET("robot_id = #{robotId}");
                    }
                    if (job.getPathImage()!= null) {
                        SET("path_image=#{pathImage}");
                    }
                    if (job.getEndReason()!= null) {
                        SET("end_reason=#{endReason}");
                    }
                    WHERE("uid = #{uid} " );
                }
            }.toString();
        }
        public String selectByListMap(@Param("siteId") String siteId) {
            String sql = new SQL(){{
                SELECT("plan.*,task.task_type_id");
                FROM(" job plan ,task");
                WHERE("plan.task_id=task.uid and plan.status!=3");
                if(siteId!=null){
                    WHERE("plan.site_id = #{siteId}");
                }
            }}.toString();
            return sql;
        }

        public String findJobsList(@Param("taskId") String taskId,@Param("siteId") String siteId,@Param("fromDate") String fromDate,@Param("toDate") String toDate,
                                       @Param("taskName") String taskName,@Param("stautseList") List<Integer> stautseList,@Param("auditStatus") String auditStatus
        ) {
            String sql = new SQL(){{
                SELECT(" plan.*,task.task_type_id");
                FROM("job plan ,task ");
                WHERE("plan.task_id=task.uid");
                if (taskId!=null ) {
                    WHERE(" plan.task_id=#{taskId}");
                }
                if (siteId!=null) {
                    WHERE("plan.site_id = #{siteId}");
                }
                if (fromDate!=null) {
                    String fromDateStr="";
                    fromDateStr="plan.plan_start_time >= "+"'"+fromDate+" 00:00:00"+"'";
                    WHERE(fromDateStr);
                }
                if (toDate!=null) {
                    WHERE("plan.plan_start_time <= #{toDate}");
                }
                if (taskName!=null && !taskName.equals("")) {
                    WHERE("task.name like \"%"+taskName+"%\" ");
                }
                if (stautseList!=null && stautseList.size()>0 ) {
                    String str = "";
                    for (int i = 0; i < stautseList.size(); i++) {
                        if (Objects.equals(str, "")) {
                            str = "('" + stautseList.get(i) + "'";
                        } else {
                            str += ",'" + stautseList.get(i) + "'";
                        }
                    }
                    str += ")";
                    WHERE("plan.status in " + str);
                }
                if (auditStatus!=null ) {
                    WHERE(" plan.audit_status=#{auditStatus}");
                }
            }}.toString();
            return sql;
        }

        public String findJobCountByDate(@Param("beginDate") String beginDate,@Param("endDate") String endDate,@Param("siteId") String siteId,@Param("robotId") String robotId
        ) {
            String sql = new SQL(){{
                String beginDateStr="";
                beginDateStr="plan_start_time > "+"'"+beginDate+" 00:00:00"+"'";
                String endDateStr="";
                endDateStr="plan_start_time > "+"'"+endDate+" 00:00:00"+"'";
                SELECT(" DATE_FORMAT(plan_start_time, '%Y-%m-%d') AS taskDate, count(1) as num");
                FROM("job");
                WHERE(beginDateStr);
                WHERE(endDateStr);
                WHERE("status !=3");
                WHERE("site_id =#{siteId}");
                WHERE("robot_id =#{robotId}");
                GROUP_BY("DATE_FORMAT(plan_start_time, '%Y-%m-%d')");
            }}.toString();
            return sql;
        }
    }

}



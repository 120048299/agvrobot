package com.wootion.mapper;

import com.github.pagehelper.PageRowBounds;
import com.wootion.model.TaskLog;
import net.sf.jsqlparser.statement.select.Limit;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Mapper
public interface TaskLogMapper {
    @SelectKey(statement="SELECT replace(uuid(), '-', '')", keyProperty="uid", before=true, resultType = String.class)
    @InsertProvider(type = SqlProvider.class, method = "insert")
    @Options(keyProperty = "uid", useGeneratedKeys = true)
    int insert(TaskLog record);

    @UpdateProvider(type = SqlProvider.class, method = "update")
    int update(TaskLog record);

    @Delete("delete from task_log where uid=#{uid}")
    int delete(String uid);
    @Delete("delete from task_log where dev_id=#{devId}")
    int deleteByDevId(String devId);


    @Select("select * from task_log where uid=#{uid}")
    TaskLog select(String uid);

    @Select("select * from task_log where site_id=#{siteId}")
    List<TaskLog> query(@Param("siteId") String siteId);

    @Select("select * from task_log where job_id=#{jobId} and ptz_set_id=#{ptzSetId}")
    TaskLog selectByJobPtz(@Param("jobId") String jobId,@Param("ptzSetId") String ptzSetId);


    @SelectProvider(type = SqlProvider.class, method = "selectLastTaskLogByPtzSetId")
    TaskLog selectLastTaskLogByPtzSetId(@Param("ptzSetId") String ptzSetId, @Param("beginTime") Date beginTime);

    @SelectProvider(type = SqlProvider.class, method = "selectTaskExecLogForPlanReport")
    List<Map> selectTaskExecLogForPlanReport(@Param("taskId") String taskId);

    @SelectProvider(type = SqlProvider.class, method = "selectTaskLogList")
    List<Map> selectTaskLogList(@Param("taskLogId") String taskLogId,@Param("ptzSetId") String ptzSetId,@Param("fromDate") String fromDate,@Param("toDate") String toDate,
    @Param("execFailReason") String execFailReason,@Param("auditStatus") String auditStatus,@Param("selectedJobId") String selectedJobId,
	@Param("alarmLevel") String alarmLevel,@Param("hasAlarm") Integer hasAlarm,@Param("forAudit") Integer forAudit,@Param("robotId") String robotId);

    @SelectProvider(type = SqlProvider.class, method = "selectTaskLogForAnalysis")
    List<Map> selectTaskLogForAnalysis(@Param("ptzSetIds") List ptzSetIds,@Param("saveType") String saveType,@Param("fromDate") String fromDate,@Param("toDate") String toDate
    );

    @Select("select * from task_log where task_exec_id=#{taskExecId}")
    TaskLog selectTaskLogByExecId(@Param("taskExecId") String taskExecId);

    @Select("select log.*from task_log log where log.audit_status=0 and log.regz_spot_id is not null and log.job_id=#{taskPlanId}")
    List<TaskLog> selectTaskLogWaitAudit(@Param("taskPlanId") String taskPlanId);

    @Select("select count(1) as totalNum,sum(audit_status) auditedNum from task_log log where log.job_id=#{taskPlanId} and log.regz_spot_id is not null")
    Map getTaskPlanLogAuditCount(@Param("taskPlanId") String taskPlanId);

    @Select("select * from task_log where ptz_set_id=#{ptzSetId} and job_id=#{taskPlanId} LIMIT 1")
    TaskLog selectTaskLogByptsSetAndPlan(@Param("ptzSetId") String ptzSetId,@Param("taskPlanId") String taskPlanId);


    @SelectProvider(type = SqlProvider.class, method = "selectTaskLogForReport")
    List<Map> selectTaskLogForReport(@Param("ptzSetIds") List ptzSetIds,@Param("saveState") String saveState,@Param("fromDate") String fromDate,@Param("toDate") String toDate,
                                     @Param("orderStyle") Integer orderStyle
    );

    @SelectProvider(type = SqlProvider.class, method = "selectTaskLogForDeviceAlarmReport")
    List<Map> selectTaskLogForDeviceAlarmReport(@Param("ptzSetIds") List ptzSetIds,@Param("opsTypes") List opsTypes,@Param("devTypeIds") List devTypeIds,@Param("alarmLevelId") List alarmLevelId,
                                                @Param("fromDate") String fromDate,@Param("toDate") String toDate,@Param("siteId") String siteId
    );


    @Select("select log.audit_status,job.uid,job.name,job.status,job.plan_start_time,job.plan_end_time,  \n" +
            "                         T1.TotalCount,T1.abnormallCount,T2.alarmCount,(T1.TotalCount-T2.normallCount) as unnormallCount from  \n" +
            "                         job job,task_log log,  \n" +
            "                         ( select  job.uid,sum(1) totalCount ,sum(log.audit_regz_abnormal) as abnormallCount \n" +
            "                            from job job,task_log log  \n" +
            "                            where log.job_id=job.uid  \n" +
            "                            and log.regz_spot_id is not null  \n" +
            "                            group by job.uid \n" +
            "\t\t\t\t\t\t) as T1  \n" +
            "                           left join  \n" +
            "                         (  \n" +
            "              select t3.uid,sum( if(t3.alarm_level>0,1,0)) as alarmCount,sum( if(t3.alarm_level=0,1,0)) as normallCount from  \n" +
            "                (  \n" +
            "                select t1.uid,t2.log_id, IFNULL(t2.alarm_level,0) as alarm_level from  \n" +
            "                 ( select job.uid,log.uid as loguid from  \n" +
            "                job job,task_log log  \n" +
            "                 where log.job_id=job.uid and log.regz_spot_id is not null  \n" +
            "                 ) as t1  \n" +
            "                 left join alarm_log t2  \n" +
            "                 on t1.loguid=t2.log_id \n" +
            "                ) as t3  \n" +
            "                group by t3.uid \n" +
            "                          ) as T2  \n" +
            "                         on T1.uid =T2.uid  \n" +
            "                         where job.uid=T1.uid and job.uid=T2.uid  \n" +
            "                         and job.site_id=#{siteId} \n" +
            "                         and log.job_id=job.uid  \n" +
            "                         and log.audit_status =0 \n" +
            "                         order by plan_start_time")
    List<Map> selectTaskLogListForDeviceAlarmForSuperUser(@Param("siteId") String siteId);

    @UpdateProvider(type = SqlProvider.class, method = "updateByIds")
    int updateByIds(@Param("idList") List idList);


    @SelectProvider(type = SqlProvider.class, method = "selectTaskLogByPtzId")
    List<Map> selectTaskLogByPtzId(@Param("ptzSetId") String ptzSetId, @Param("fromDate") String fromDate,@Param("toDate") String toDate
    );
    class SqlProvider {
        public String insert(TaskLog record) {
            return new SQL() {
                {
                    INSERT_INTO("task_log");
                    if (record.getUid()!= null) {
                        VALUES("uid", "#{uid}");
                    }
                    if (record.getTaskId()!= null) {
                        VALUES("task_id", "#{taskId}");
                    }
                    if (record.getJobId()!= null) {
                        VALUES("job_id", "#{jobId}");
                    }
                    if (record.getPtzSetId()!= null) {
                        VALUES("ptz_set_id", "#{ptzSetId}");
                    }
                    if (record.getRobotId()!= null) {
                        VALUES("robot_id", "#{robotId}");
                    }
                    if (record.getBeginTime()!= null) {
                        VALUES("begin_time", "#{beginTime}");
                    }
                    if (record.getFinishTime()!= null) {
                        VALUES("finish_time", "#{finishTime}");
                    }
                    if (record.getSiteId()!= null) {
                        VALUES("site_id", "#{siteId}");
                    }
                    if (record.getAreaId()!= null) {
                        VALUES("area_id", "#{areaId}");
                    }
                    if (record.getStatus()!= null) {
                        VALUES("status", "#{status}");
                    }
                    if (record.getMemo()!= null) {
                        VALUES("memo", "#{memo}");
                    }
                    if (record.getResult()!= null) {
                        VALUES("result", "#{result}");
                    }
                }
            }.toString();
        }

        public String update(TaskLog record) {
            return new SQL() {
                {
                    UPDATE("task_log");
                    if (record.getTaskId()!= null) {
                        SET("task_id = #{taskId}");
                    }
                    if (record.getJobId()!= null) {
                        SET("job_id = #{jobId}");
                    }
                    if (record.getPtzSetId()!= null) {
                        SET("ptz_set_id = #{ptzSetId}");
                    }
                    if (record.getBeginTime()!= null) {
                        SET("begin_time = #{beginTime}");
                    }
                    if (record.getFinishTime()!= null) {
                        SET("finish_time = #{finishTime}");
                    }
                    if (record.getSiteId()!= null) {
                        SET("site_id = #{siteId}");
                    }
                    if (record.getAreaId()!= null) {
                        SET("area_id = #{areaId}");
                    }
                    if (record.getStatus()!= null) {
                        SET("status = #{status}");
                    }
                    if (record.getMemo()!= null) {
                        SET("memo = #{memo}");
                    }
                    if (record.getResult()!= null) {
                        SET("result = #{result}");
                    }
                    WHERE("uid = #{uid} " );
                }
            }.toString();
        }

        public String selectLastTaskLogByPtzSetId(@Param("ptzSetId") String ptzSetId, @Param("beginTime") Date beginTime) {
            String sql = new SQL(){{
                SELECT(" *");
                FROM("task_log");
                if (ptzSetId!=null && !ptzSetId.equals("")) {
                    WHERE("ptz_set_id=#{ptzSetId} and regz_spot_id is not null");
                }
                if (beginTime!=null) {
                    WHERE("begin_time>#{beginTime}");
                }
               ORDER_BY("begin_time desc +\" LIMIT 1\"");

            }}.toString();
            return sql;
        }

        public String selectTaskExecLogForPlanReport(@Param("taskId") String taskId) {
            String sql = new SQL(){{
                SELECT(" t1.* ,t2.uid as alarm_uid,t2.time , t2.status  as alarmlog_status ,t2.alarm_code_id ,t2.alarm_level ,\n" +
                        "        t2.audit_alarm_code_id ,t2.is_new_alarm");
                String str="";
                str = "(select log.*,ptz.order_num ptzOrderNum\n" +
                        "        from task_log log,ptz_set ptz\n" +
                        "        where log.job_id=#{taskId} and log.ptz_set_id=ptz.uid and log.regz_spot_id is not null) t1";
                FROM(str);
                LEFT_OUTER_JOIN("alarm_log t2 on  t1.uid=t2.log_id order by t1.ptzOrderNum");

            }}.toString();
            return sql;
        }

        public String selectTaskLogList(@Param("taskLogId") String taskLogId,@Param("ptzSetId") String ptzSetId,@Param("fromDate") String fromDate,@Param("toDate") String toDate,
                                        @Param("execFailReason") String execFailReason,@Param("auditStatus") String auditStatus,@Param("selectedJobId") String selectedJobId,
                                        @Param("alarmLevel") String alarmLevel,@Param("hasAlarm") Integer hasAlarm,@Param("forAudit") Integer forAudit,@Param("robotId") String robotId) {
            String sql = new SQL(){{
                SELECT("t1.* ,t2.uid as alarm_uid,t2.time , t2.status  as alarmlog_status ,t2.alarm_code_id ,t2.alarm_level ,t2.audit_alarm_code_id ,t2.is_new_alarm");
                FROM("task_log t1");
                LEFT_OUTER_JOIN("alarm_log t2 on  t1.uid=t2.log_id");
                WHERE("1=1 and  t1.regz_spot_id is not null");
                if (taskLogId!=null && !taskLogId.equals("")) {
                    WHERE("t1.uid = #{taskLogId}");
                }
                if (ptzSetId!=null && !ptzSetId.equals("")) {
                    WHERE("t1.ptz_set_id = #{ptzSetId}");
                }
                if (fromDate!=null && !fromDate.equals("")) {
                    WHERE("t1.begin_time >= #{fromDate}");
                }
                if (toDate!=null && !toDate.equals("")) {
                    WHERE("t1.begin_time < #{toDate}");
                }
                if (execFailReason!=null && !execFailReason.equals("") &&  !execFailReason.equals("-1")) {
                    WHERE("t1.exec_fail_reason = #{execFailReason}");
                }
                if (auditStatus!=null && !auditStatus.equals("") && !auditStatus.equals("-1")) {
                    WHERE("t1.audit_status = #{auditStatus}");
                }
                if (selectedJobId!=null && !selectedJobId.equals("") && !selectedJobId.equals("-1")) {
                    WHERE("t1.job_id= #{selectedJobId}");
                }
                if (robotId!=null && !robotId.equals("") ) {
                    WHERE("t1.robot_id= #{robotId}");
                }
                if (alarmLevel!=null && !alarmLevel.equals("") && !alarmLevel.equals("-1") && !alarmLevel.equals("0")) {
                    WHERE("t2.alarm_level = #{alarmLevel}");
                }
                if (alarmLevel!=null && !alarmLevel.equals("") && alarmLevel.equals("0")) {
                    WHERE("t2.alarm_level is null");
                }
                if (hasAlarm!=null && !hasAlarm.equals("")) {
                    WHERE("t2.alarm_level > 0");
                }
                if (forAudit!=null && !forAudit.equals("")) {

                }
                ORDER_BY("t1.begin_time desc");
            }}.toString();
            return sql;
        }

        public String selectTaskLogForAnalysis(@Param("ptzSetIds") List ptzSetIds,@Param("saveType") String saveType,@Param("fromDate") String fromDate,@Param("toDate") String toDate) {
            String sql = new SQL(){{
               SELECT(" log.*,job.uid,t2.uid as alarm_uid,t2.time , t2.status  as alarmlog_status ,t2.alarm_code_id ,t2.alarm_level ,\n" +
                       "        t2.audit_alarm_code_id,t2.is_new_alarm");
               FROM("job job,task_log log");
               LEFT_OUTER_JOIN("alarm_log t2 on  log.uid=t2.log_id");
               WHERE("log.status = 2 and log.job_id=job.uid and log.regz_spot_id is not null");
                if (ptzSetIds!=null && ptzSetIds.size()>0 ) {
                    String str = "";
                    for (int i = 0; i < ptzSetIds.size(); i++) {
                        if (Objects.equals(str, "")) {
                            str = "('" + ptzSetIds.get(i) + "'";
                        } else {
                            str += ",'" + ptzSetIds.get(i) + "'";
                        }
                    }
                    str += ")";
                    WHERE("log.ptz_set_id in " + str);
                }
                if(saveType!=null && !saveType.equals("") && saveType.equals("0")){
                    WHERE("log.img_file is not null ");
                }
                if(saveType!=null && !saveType.equals("") && saveType.equals("1")){
                    WHERE("log.infrared_file is not null ");
                }
                if(saveType!=null && !saveType.equals("") && saveType.equals("2")){
                    WHERE("log.video_file is not null ");
                }
                if(saveType!=null && !saveType.equals("") && saveType.equals("3")){
                    WHERE("log.audio_file is not null ");
                }
                if (fromDate!=null && !fromDate.equals("")) {
                    WHERE("log.begin_time >= #{fromDate}");
                }
                if (toDate!=null && !toDate.equals("")) {
                    WHERE("log.begin_time < #{toDate}");
                }
                ORDER_BY("log.begin_time desc");
            }}.toString();
            return sql;
        }

        public String selectTaskLogForReport(@Param("ptzSetIds") List ptzSetIds,@Param("saveState") String saveState,@Param("fromDate") String fromDate,@Param("toDate") String toDate,
                                             @Param("orderStyle") Integer orderStyle) {
            String sql = new SQL(){{
                SELECT(" log.*,ptz.description,t2.uid as alarm_uid,t2.time , t2.status  as alarmlog_status ,t2.alarm_code_id ,t2.alarm_level ,t2.alarm_desc, t2.audit_alarm_code_id");
                FROM("ptz_set ptz,task_log log");
                LEFT_OUTER_JOIN("alarm_log t2 on  log.uid=t2.log_id");
                WHERE("log.status = 2 and log.ptz_set_id=ptz.uid and log.regz_spot_id is not null");
                if (ptzSetIds!=null && ptzSetIds.size()>0 ) {
                    String str = "";
                    for (int i = 0; i < ptzSetIds.size(); i++) {
                        if (Objects.equals(str, "")) {
                            str = "('" + ptzSetIds.get(i) + "'";
                        } else {
                            str += ",'" + ptzSetIds.get(i) + "'";
                        }
                    }
                    str += ")";
                    WHERE("log.ptz_set_id in " + str);
                }
                if(saveState!=null && !saveState.equals("") ){
                    WHERE("log.audit_regz_abnormal = #{saveState} ");
                }
                if (fromDate!=null && !fromDate.equals("")) {
                    WHERE("log.begin_time >= #{fromDate}");
                }
                if (toDate!=null && !toDate.equals("")) {
                    WHERE("log.begin_time < #{toDate}");
                }
                if(orderStyle!=null && !orderStyle.equals("") && orderStyle.equals("0")){
                   ORDER_BY("log.begin_time");
                }
                if(orderStyle!=null && !orderStyle.equals("") && orderStyle.equals("1")){
                    ORDER_BY("order by log.begin_time desc");
                }
                if(orderStyle!=null && !orderStyle.equals("") && orderStyle.equals("2")){
                    ORDER_BY("order by  log.description");
                }
            }}.toString();
            return sql;
        }

        public String selectTaskLogForDeviceAlarmReport(@Param("ptzSetIds") List ptzSetIds,@Param("opsTypes") List opsTypes,@Param("devTypeIds") List devTypeIds,@Param("alarmLevelId") List alarmLevelId,
                                                        @Param("fromDate") String fromDate,@Param("toDate") String toDate,@Param("siteId") String siteId) {
            String sql = new SQL(){{
                if(ptzSetIds != null && ptzSetIds.size() > 0) {
                    String str1="";
                        str1="(select log.*, regz.dev_type_id as smallDevTypeId,t2.uid as alarm_uid,t2.time , t2.status  as alarmlog_status ,t2.alarm_code_id ,IFNULL(t2.alarm_level,0) as\n" +
                                "alarm_level,t2.audit_alarm_code_id ,t2.is_new_alarm from ptz_set ptz,regz_spot regz,task_log log left join alarm_log t2 on  log.uid=t2.log_id\n" +
                                " where  ptz.uid=log.ptz_set_id  and log.regz_spot_id=regz.uid and log.status = 2) t1";
                    SELECT(" *");
                    FROM(str1);
                    if (ptzSetIds != null && ptzSetIds.size() > 0) {
                        String str2 = "";
                        for (int i = 0; i < ptzSetIds.size(); i++) {
                            if (Objects.equals(str2, "")) {
                                str2 = "('" + ptzSetIds.get(i) + "'";
                            } else {
                                str2 += ",'" + ptzSetIds.get(i) + "'";
                            }
                        }
                        str2 += ")";
                        WHERE("t1.ptz_set_id in " + str2);
                    }
                    if (siteId != null && !siteId.equals("")) {
                        WHERE("t1.site_id = #{siteId} ");
                    }
                    if (fromDate != null && !fromDate.equals("")) {
                        WHERE("t1.begin_time >= #{fromDate}");
                    }
                    if (toDate != null && !toDate.equals("")) {
                        WHERE("t1.begin_time < #{toDate}");
                    }
                    if (alarmLevelId != null && alarmLevelId.size() > 0) {
                        String str3 = "";
                        for (int i = 0; i < alarmLevelId.size(); i++) {
                            if (Objects.equals(str3, "")) {
                                str3 = "('" + alarmLevelId.get(i) + "'";
                            } else {
                                str3 += ",'" + alarmLevelId.get(i) + "'";
                            }
                        }
                        str3 += ")";
                        WHERE("t1.alarm_level in " + str3);
                    }
                }else{
                    String str1="";
                    str1="(select log.*, regz.dev_type_id as smallDevTypeId,regz.ops_type,regz.dev_type_id,t2.uid as alarm_uid,t2.time , t2.status  as alarmlog_status,t2.alarm_code_id ,IFNULL(t2.alarm_level,0) as\n" +
                            "alarm_level,t2.audit_alarm_code_id ,t2.is_new_alarm from ptz_set ptz,regz_spot regz,task_log log left join alarm_log t2 on  log.uid=t2.log_id\n" +
                            " where  ptz.uid=log.ptz_set_id  and log.regz_spot_id=regz.uid and log.status = 2) t1";
                    SELECT(" *");
                    FROM(str1);
                    if (opsTypes != null && opsTypes.size() > 0) {
                        String str2 = "";
                        for (int i = 0; i < opsTypes.size(); i++) {
                            if (Objects.equals(str2, "")) {
                                str2 = "('" + opsTypes.get(i) + "'";
                            } else {
                                str2 += ",'" + opsTypes.get(i) + "'";
                            }
                        }
                        str2 += ")";
                        WHERE("t1.ops_type in " + str2);
                    }
                    if (siteId != null && !siteId.equals("")) {
                        WHERE("t1.site_id = #{siteId} ");
                    }
                    if (fromDate != null && !fromDate.equals("")) {
                        WHERE("t1.begin_time >= #{fromDate}");
                    }
                    if (toDate != null && !toDate.equals("")) {
                        WHERE("t1.begin_time < #{toDate}");
                    }
                    if (devTypeIds != null && devTypeIds.size() > 0) {
                        String str3 = "";
                        for (int i = 0; i < devTypeIds.size(); i++) {
                            if (Objects.equals(str3, "")) {
                                str3 = "('" + devTypeIds.get(i) + "'";
                            } else {
                                str3 += ",'" + devTypeIds.get(i) + "'";
                            }
                        }
                        str3 += ")";
                        WHERE("t1.dev_type_id in " + str3);
                    }
                    if (alarmLevelId != null && alarmLevelId.size() > 0) {
                        String str4 = "";
                        for (int i = 0; i < alarmLevelId.size(); i++) {
                            if (Objects.equals(str4, "")) {
                                str4 = "('" + alarmLevelId.get(i) + "'";
                            } else {
                                str4+= ",'" + alarmLevelId.get(i) + "'";
                            }
                        }
                        str4 += ")";
                        WHERE("t1.alarm_level in " + str4);
                    }
                }
            }}.toString();
            return sql;
        }

        public String selectTaskLogByPtzId(@Param("ptzSetId") String ptzSetId, @Param("fromDate") String fromDate,@Param("toDate") String toDate) {
            String sql = new SQL(){{
                SELECT(" log.* ,t2.uid as alarm_uid,t2.time , t2.status  as alarmlog_status,t2.alarm_code_id ,t2.alarm_level ,\n" +
                        "        t2.audit_alarm_code_id ,t2.audit_alarm_level ,t2.is_new_alarm");
                FROM("task_log log");
                LEFT_OUTER_JOIN("alarm_log t2 on  log.uid=t2.log_id");
                WHERE("log.status = 2 and log.ptz_set_id=ptz.uid and log.regz_spot_id is not null");
                if(ptzSetId!=null && !ptzSetId.equals("") ){
                    WHERE("log.ptz_set_id=#{ptzSetId} ");
                }
                if (fromDate!=null && !fromDate.equals("")) {
                    WHERE("log.begin_time >= #{fromDate}");
                }
                if (toDate!=null && !toDate.equals("")) {
                    WHERE("log.begin_time < #{toDate}");
                }
            }}.toString();
            return sql;
        }

        public String updateByIds(@Param("idList") List idList) {
            return new SQL() {
                {
                    UPDATE("job job");
                    LEFT_OUTER_JOIN("task_log log on log.job_id=job.uid");
                    LEFT_OUTER_JOIN("alarm_log on alarm_log.log_id=log.uid");
                    SET("job.audit_status=1");
                    SET("log.audit_status=1");
                    SET("alarm_log.alarm_level=0");
                    if (idList!=null && idList.size()>0 ) {
                        String str = "";
                        for (int i = 0; i < idList.size(); i++) {
                            if (Objects.equals(str, "")) {
                                str = "('" + idList.get(i) + "'";
                            } else {
                                str += ",'" + idList.get(i) + "'";
                            }
                        }
                        str += ")";
                        WHERE("log.job_id in " + str);
                    }
                }
            }.toString();
        }
    }
}
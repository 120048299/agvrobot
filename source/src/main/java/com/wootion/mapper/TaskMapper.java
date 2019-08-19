package com.wootion.mapper;

import com.wootion.model.Task;
import com.wootion.model.TaskResult;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Mapper
public interface TaskMapper {
    @SelectKey(statement=" select concat('task',getTableId('task'))", keyProperty="uid", before=true, resultType = String.class)
    @InsertProvider(type = SqlProvider.class, method = "insert")
    @Options(keyProperty = "uid", useGeneratedKeys = true)
    int insert(Task record);

    @UpdateProvider(type = SqlProvider.class, method = "update")
    int update(Task record);

    @Update("  update task set status=#{status} where  uid=#{uid} " )
    int updateStatus(@Param("uid") String uid,@Param("status") int status);

    @Delete("delete from task where uid=#{uid}")
    int delete(String uid);

    @Select("select * from task where uid=#{uid}")
    Task select(String uid);

    @Select("select * from task where site_id=#{siteId}")
    List<Task> queryBySite(@Param("siteId") String siteId);

    @Select("select * from task where site_id=#{siteId}")
    List<Task> selectAll(@Param("siteId") String siteId);

    @Select("select * from task where site_id=#{siteId} and status !=3")
    List<Task> queryBySiteWithStatus(@Param("siteId") String siteId);

    @Select("select * from task where site_id=#{siteId} and robot_id=#{robotId}")
    List<Task> syncTasks(@Param("siteId") String siteId,@Param("robotId") String robotId);

    @Select("select * from task where uid=#{uid}")
    List<Task> queryByUid(@Param("uid") String uid);

   @UpdateProvider(type = SqlProvider.class, method = "updateTaskDataSynSuccess")
   int updateTaskDataSynSuccess(@Param("taskUidList")List<String> taskUidList);

    @UpdateProvider(type = SqlProvider.class, method = "updateTaskDataSynFail")
    int updateTaskDataSynFail(@Param("taskUidList")List<String> taskUidList);

    @SelectProvider(type = SqlProvider.class, method = "selectTaskList")
    List<Task> selectTaskList(@Param("taskTypeId") String taskTypeId,@Param("siteId") String siteId,@Param("robotId") String robotId,@Param("taskName") String taskName);

    @SelectProvider(type = SqlProvider.class, method = "getTaskListData")
    List<Map> getTaskListData(@Param("siteId") String siteId,@Param("fromDate") String fromDate,@Param("toDate") String toDate,
                              @Param("taskName") String taskName,@Param("robotId") String robotId);

    class SqlProvider {
        public String insert(Task record) {
            return new SQL() {
                {
                    INSERT_INTO("task");
                    if (record.getUid() != null) {
                        VALUES("uid", "#{uid}");
                    }
                    if (record.getName() != null) {
                        VALUES("name", "#{name}");
                    }
                    if (record.getUserId() != null) {
                        VALUES("user_id", "#{userId}");
                    }
                    if (record.getCreateTime() != null) {
                        VALUES("create_time", "#{createTime}");
                    }
                    if (record.getEditTime() != null) {
                        VALUES("edit_time", "#{editTime}");
                    }
                    if (record.getStatus() != null) {
                        VALUES("status", "#{status}");
                    }
                    if (record.getDescription() != null) {
                        VALUES("description", "#{description}");
                    }
                    if (record.getSiteId() != null) {
                        VALUES("site_id", "#{siteId}");
                    }
                    if (record.getRobotId() != null) {
                        VALUES("robot_id", "#{robotId}");
                    }
                    if (record.getMapTask() != null) {
                        VALUES("map_task", "#{mapTask}");
                    }
                    if (record.getEmergency() != null) {
                        VALUES("emergency", "#{emergency}");
                    }
                    if (record.getEmergency() != null) {
                        VALUES("sync_status", "#{syncStatus}");
                    }
                }
            }.toString();
        }

        public String update(Task record) {
            return new SQL() {
                {
                    UPDATE("task");
                    if (record.getName() != null) {
                        SET("name = #{name}");
                    }
                    if (record.getUserId() != null) {
                        SET("user_id = #{userId}");
                    }
                    if (record.getCreateTime() != null) {
                        SET("create_time = #{createTime}");
                    }
                    if (record.getEditTime() != null) {
                        SET("edit_time = #{editTime}");
                    }
                    if (record.getStatus() != null) {
                        SET("status = #{status}");
                    }
                    if (record.getDescription() != null) {
                        SET("description = #{description}");
                    }
                    if (record.getSiteId() != null) {
                        SET("site_id = #{siteId}");
                    }
                    if (record.getRobotId() != null) {
                        SET("robot_id = #{robotId}");
                    }
                    if (record.getMapTask() != null) {
                        SET("map_task = #{mapTask}");
                    }
                    if (record.getEmergency() != null) {
                        SET("emergency = #{emergency}");
                    }
                    if (record.getEmergency() != null) {
                        SET("sync_status = #{syncStatus}");
                    }
                    WHERE("uid = #{uid} ");
                }
            }.toString();
        }

        public String updateTaskDataSynSuccess(@Param("taskUidList")List<String> taskUidList) {
            return new SQL() {
                {
                    UPDATE("task");
                    SET("sync_status = 1 ");
                    if (taskUidList != null && taskUidList.size() > 0) {
                        String str = "";
                        for (int i = 0; i < taskUidList.size(); i++) {
                            if (Objects.equals(str, "")) {
                                str = "('" + taskUidList.get(i) + "'";
                            } else {
                                str += ",'" + taskUidList.get(i) + "'";
                            }
                        }
                        str += ")";
                        WHERE("uid in " + str);
                    }
                }
            }.toString();
        }

        public String updateTaskDataSynFail(@Param("taskUidList")List<String> taskUidList) {
            return new SQL() {
                {
                    UPDATE("task");
                    SET("sync_status = 0 ");
                    if (taskUidList != null && taskUidList.size() > 0) {
                        String str = "";
                        for (int i = 0; i < taskUidList.size(); i++) {
                            if (Objects.equals(str, "")) {
                                str = "('" + taskUidList.get(i) + "'";
                            } else {
                                str += ",'" + taskUidList.get(i) + "'";
                            }
                        }
                        str += ")";
                        WHERE("uid in " + str);
                        System.out.println(str);
                    }
                }

            }.toString();

        }

        public String selectTaskList(@Param("taskTypeId") String taskTypeId,@Param("siteId") String siteId,@Param("robotId") String robotId,@Param("taskName") String taskName
        ) {
            String sql = new SQL(){{
                SELECT("*");
                FROM("task ");
                WHERE("status!=3 and status!=2");
                if (taskTypeId!=null) {
                    WHERE(" task_type_id=#{taskTypeId}");
                }
                if (siteId!=null) {
                    WHERE("site_id = #{siteId}");
                }
                if (robotId!=null) {
                    WHERE("robot_id = #{robotId}");
                }
                if (taskName!=null && !taskName.equals("")) {
                    WHERE("name like \"%"+taskName+"%\" ");
                }
                ORDER_BY("create_time DESC");
            }}.toString();
            return sql;
        }

        public String getTaskListData(@Param("siteId") String siteId,@Param("fromDate") String fromDate,@Param("toDate") String toDate,
                                   @Param("taskName") String taskName,@Param("robotId") String robotId
        ) {
            String sql = new SQL(){{
                SELECT(" job.*,task.task_type_id,task.edit_time,task.uid as taskuid");
                FROM("job job ,task ");
                WHERE("job.task_id=task.uid and job.status=4 and task.task_type_id=5");
                if (siteId!=null) {
                    WHERE("job.site_id = #{siteId}");
                }
                if (robotId!=null) {
                    WHERE("job.robot_id= #{robotId}");
                }
                if (fromDate!=null) {
                    String fromDateStr="";
                    fromDateStr="job.plan_start_time > "+"'"+fromDate+" 00:00:00"+"'";
                    WHERE(fromDateStr);
                }
                if (toDate!=null) {
                    WHERE("job.plan_start_time < #{toDate}");
                }
                if (taskName!=null && !taskName.equals("")) {
                    WHERE("task.name like \"%"+taskName+"%\" ");
                }
            }}.toString();
            return sql;
        }

        public String queryByTaskTypeId(@Param("taskTypeId") String taskTypeId,@Param("siteId") String siteId
        ) {
            String sql = new SQL(){{
                SELECT(" *");
                FROM("task ");
                if (taskTypeId!=null && !taskTypeId.equals("")) {
                    WHERE("task_type_id = #{taskTypeId}");
                }
                if (siteId!=null && !siteId.equals("")) {
                    WHERE("site_id= #{siteId}");
                }
            }}.toString();
            return sql;
        }
    }
}
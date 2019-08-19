package com.wootion.mapper;

import com.wootion.model.TaskPtz;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface TaskPtzMapper {
    @SelectKey(statement="SELECT replace(uuid(), '-', '')", keyProperty="uid", before=true, resultType = String.class)
    @InsertProvider(type = SqlProvider.class, method = "insert")
    @Options(keyProperty = "uid", useGeneratedKeys = true)
    int insert(TaskPtz record);

    @UpdateProvider(type = SqlProvider.class, method = "update")
    int update(TaskPtz record);


    @Delete("delete from task_ptz where uid=#{uid}")
    int delete(String uid);

    @Delete("delete from task_ptz where task_id=#{taskId}")
    int deleteByTaskId(String taskId);

    @Delete("delete from task_ptz where ptz_set_id=#{ptzSetId}")
    int deleteByPtzSetId(String ptzSetId);


    @Select("select * from task_ptz where uid=#{uid}")
    TaskPtz select(String uid);

    @Select("select * from task_ptz where task_id=#{taskId}")
    List<TaskPtz> selectListByTask(@Param("taskId") String taskId);
    //另外需要区分是否真实ptz
    /*
    <select id="selectTaskPtz" resultMap="BaseResultMap" >
    select t1.*  from task_ptz t1,ptz_set ptz
    where t1.task_id=#{taskId} and t1.ptz_set_id=ptz.uid
    and (
          (ptz.ptz_num=0 and (ptz.setted=1 and ptz.status=1) )
    or (ptz.ptz_num !=0)
            )
    </select>
     */

    @Select("select ptz_set_id from task_ptz where task_id=#{taskId}")
    List<String> selectListStringByTaskUid (@Param("taskId") String taskId);

    @InsertProvider(type = SqlProvider.class, method = "insertInBatch")
    int insertBatch(@Param("taskPtzList") List taskPtzList);


    class SqlProvider {

         public String insert(TaskPtz record) {
            return new SQL() {
                {
                    INSERT_INTO("task_ptz");
                    if (record.getUid()!= null) {
                        VALUES("uid", "#{uid}");
                    }
                    if (record.getTaskId()!= null) {
                        VALUES("task_id", "#{taskId}");
                    }
                    if (record.getPtzSetId()!= null) {
                        VALUES("ptz_set_id", "#{ptzSetId}");
                    }
                    if (record.getSiteId()!= null) {
                        VALUES("site_id", "#{siteId}");
                    }
                }
            }.toString();
        }

        public String update(TaskPtz record) {
            return new SQL() {
                {
                    UPDATE("task_ptz");
                    if (record.getTaskId()!= null) {
                        SET("task_id = #{taskId}");
                    }
                    if (record.getPtzSetId()!= null) {
                        SET("ptz_set_id = #{ptzSetId}");
                    }
                    if (record.getSiteId()!= null) {
                        SET("site_id = #{siteId}");
                    }
                    WHERE("uid = #{uid} " );
                }
            }.toString();
        }

        public String insertInBatch(@Param("taskPtzList") List taskPtzList) {
            String sql="insert into task_ptz values";
                for (int i = 0; i < taskPtzList.size(); i++) {
                    TaskPtz taskPtz=(TaskPtz)taskPtzList.get(i);
                    String recordSql="('"+taskPtz.getUid()+"','"+taskPtz.getTaskId()+"','"+taskPtz.getPtzSetId()+"','"+taskPtz.getSiteId()+"')";
                    if (i!=taskPtzList.size()-1) {
                        sql += recordSql+",";
                    } else {
                        sql += recordSql+";";
                    }
                }
            System.out.println(sql);
            return sql;
        }
    }
}
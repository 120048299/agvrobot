package com.wootion.mapper;

import com.wootion.model.PtzSet;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Mapper
public interface PtzSetMapper {
    @SelectKey(statement=" select concat('ptz',getTableId('ptz_set'))", keyProperty="uid", before=true, resultType = String.class)
    @InsertProvider(type = SqlProvider.class, method = "insert")
    @Options(keyProperty = "uid", useGeneratedKeys = true)
    int insert(PtzSet record);

    @SelectKey(statement=" select concat('ptz',getTableId('ptz_set_mark'))", keyProperty="uid", before=true, resultType = String.class)
    @InsertProvider(type = SqlProvider.class, method = "insert")
    @Options(keyProperty = "uid", useGeneratedKeys = true)
    int insertForMark(PtzSet record);

    @UpdateProvider(type = SqlProvider.class, method = "update")
    int update(PtzSet record);

    @Delete("delete from ptz_set where uid=#{uid}")
    int delete(String uid);

    @Delete("delete from ptz_set where mark_id=#{markId}")
    int deleteByMarkId(String uid);

    @Select("select * from ptz_set where uid=#{uid}")
    PtzSet select(String uid);

    @Select("select * from ptz_set where site_id=#{siteId}")
    List<PtzSet> selectListBySite(@Param("siteId") String siteId);

    @Select("select * from ptz_set where area_id=#{areaId}")
    List<PtzSet> selectListByArea(@Param("areaId") String areaId);

    @Select("select * from ptz_set where mark_id=#{markId}")
    List<PtzSet> findPtzSetListByMarkId(@Param("markId") String markId);

    @Select(" select ifnull(max(order_num),1) from ptz_set where site_id=#{siteId}")
    Integer getMaxOrderNum(@Param("siteId") String siteId);

    @SelectProvider(type = SqlProvider.class, method = "findListByIds")
    List<PtzSet> selectListByIds(@Param("ptzSetIds")  List ptzSetIds);

    @SelectProvider(type = SqlProvider.class, method = "findListByIds")
    List<Map> selectMapListByIds(@Param("ptzSetIds")  List ptzSetIds);

    @Select("  select t1.* from ptz_set t1, dev t2 where  t1.dev_id=t2.uid and t2.parent_id= #{bayId}")
    List<PtzSet> findPtzSetListByParentId(@Param("bayId")  String bayId);


    @SelectProvider(type = SqlProvider.class, method = "selectPtzSetByIds")
    List<Map> selectPtzSetByIds(@Param("ptzSetIds")  List ptzSetIds,@Param("status") int status);

    //任务编制页面查询ptz  完整树 和 筛选
   /* @SelectProvider(type = SqlProvider.class, method = "selectPtzListForTree")
    List<Map> selectPtzListForTree(Map params);*/


    //按taskId 查询ptz
  /*  @SelectProvider(type = SqlProvider.class, method = "selectPtzListForTreeByTaskId")
    List<Map> selectPtzListForTreeByTaskId(String siteId,String taskId);*/

    //任务结果浏览页面:按taskPlanId 查询ptz  todo wait 是否修改为任务的ptz
    /*@SelectProvider(type = SqlProvider.class, method = "selectPtzListForTreeByTaskPlanId")
    List<Map> selectPtzListForTreeByTaskPlanId(String siteId,String taskPlanId,String searchText);
*/
    class SqlProvider {


        public String selectPtzSetByIds(@Param("ptzSetIds") List ptzSetIds) {
            String sql = new SQL(){{
                SELECT("*");
                FROM(" ptz_set ");
                WHERE(" status = #{status} ");
                if (ptzSetIds!=null && ptzSetIds.size()>0 ) {
                    String str = "";
                    for (int i = 0; i < ptzSetIds.size(); i++) {
                        if (Objects.equals(str, "")) {
                            str = "('"+ptzSetIds.get(i)+"'";
                        } else {
                            str += ",'"+ptzSetIds.get(i)+"'";
                        }
                    }
                    str += ")";
                    WHERE("uid in "+str);
                }
            }}.toString();
            return sql;
        }

        public String findListByIds(@Param("ptzSetIds") List ptzSetIds) {
            String sql = new SQL(){{
                SELECT("*");
                FROM("ptz_set ");
                if (ptzSetIds!=null && ptzSetIds.size()>0 ) {
                    String str = "";
                    for (int i = 0; i < ptzSetIds.size(); i++) {
                        if (Objects.equals(str, "")) {
                            str = "('"+ptzSetIds.get(i)+"'";
                        } else {
                            str += ",'"+ptzSetIds.get(i)+"'";
                        }
                    }
                    str += ")";
                    WHERE("uid in "+str);
                }else{
                    WHERE("uid ='-1' " );
                }
            }}.toString();
            return sql;
        }

        public String insert(PtzSet record) {
            return new SQL() {
                {
                    INSERT_INTO("ptz_set");
                    if (record.getUid()!= null) {
                        VALUES("uid", "#{uid}");
                    }
                    if (record.getPtzType()!= null) {
                        VALUES("ptz_type", "#{ptzType}");
                    }
                    if (record.getRobotAngle()!= null) {
                        VALUES("robot_angle", "#{robotAngle}");
                    }
                    if (record.getPtzPan()!= null) {
                        VALUES("ptz_pan", "#{ptzPan}");
                    }
                    if (record.getPtzTilt()!= null) {
                        VALUES("ptz_tilt", "#{ptzTilt}");
                    }
                    if (record.getMarkId()!= null) {
                        VALUES("mark_id", "#{markId}");
                    }
                    if (record.getMarkId()!= null) {
                        VALUES("area_id", "#{areaId}");
                    }
                    if (record.getDescription()!= null) {
                        VALUES("description", "#{description}");
                    }
                    if (record.getScan()!= null) {
                        VALUES("scan", "#{scan}");
                    }
                    if (record.getSiteId()!= null) {
                        VALUES("site_id", "#{siteId}");
                    }
                    if (record.getStatus()!= null) {
                        VALUES("status", "#{status}");
                    }
                    if (record.getSetted()!= null) {
                        VALUES("setted", "#{setted}");
                    }
                }
            }.toString();
        }

        public String update(PtzSet record) {
            return new SQL() {
                {
                    UPDATE("ptz_set");
                    if (record.getPtzType()!= null) {
                        SET("ptz_type = #{ptzType}");
                    }
                    if (record.getRobotAngle()!= null) {
                        SET("robot_angle = #{robotAngle}");
                    }
                    if (record.getPtzPan()!= null) {
                        SET("ptz_pan = #{ptzPan}");
                    }
                    if (record.getPtzTilt()!= null) {
                        SET("ptz_tilt = #{ptzTilt}");
                    }
                    if (record.getMarkId()!= null) {
                        SET("mark_id = #{markId}");
                    }
                    if (record.getAreaId()!= null) {
                        SET("area_id = #{areaId}");
                    }
                    if (record.getDescription()!= null) {
                        SET("description = #{description}");
                    }
                    if (record.getScan()!= null) {
                        SET("scan = #{scan}");
                    }
                    if (record.getSiteId()!= null) {
                        SET("site_id = #{siteId}");
                    }
                    if (record.getStatus()!= null) {
                        SET("status = #{status}");
                    }
                    if (record.getSetted()!= null) {
                        SET("setted = #{setted}");
                    }
                    WHERE("uid = #{uid} " );
                }
            }.toString();
        }

        private String buildInString(List list,boolean isNumber){
            if (list!=null && list.size()>0 ) {
                String str = "";
                for (int i = 0; i < list.size(); i++) {
                    if (Objects.equals(str, "")) {
                        if(isNumber){
                            str = "("+list.get(i)+" ";
                        }else{
                            str = "('"+list.get(i)+"'";
                        }
                    } else {
                        if(isNumber){
                            str += ","+list.get(i)+" ";
                        }else{
                            str += ",'"+list.get(i)+"'";
                        }
                    }
                }
                str += ")";
                return str;
            }else{
                return null;
            }
        }

    }
}
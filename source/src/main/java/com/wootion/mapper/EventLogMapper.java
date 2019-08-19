package com.wootion.mapper;

import com.wootion.model.EventLog;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface EventLogMapper {
    @SelectKey(statement="SELECT replace(uuid(), '-', '')", keyProperty="uid", before=true, resultType = String.class)
    @InsertProvider(type = SqlProvider.class, method = "insert")
    @Options(keyProperty = "uid", useGeneratedKeys = true)
    int insert(EventLog record);

    @Select("select * from event_log where uid=#{uid}")
    EventLog select(String uid);

    @Select("select * from event_log where site_id=#{siteId}")
    List<EventLog> queryBySite(@Param("siteId") String siteId);

    class SqlProvider {
        public String insert(EventLog record) {
            return new SQL() {
                {
                    INSERT_INTO("event_log");
                    if (record.getUid()!= null) {
                        VALUES("uid", "#{uid}");
                    }
                    if (record.getSiteId()!= null) {
                        VALUES("site_id", "#{siteId}");
                    }
                    if (record.getRobotId()!= null) {
                        VALUES("robot_id", "#{robotId}");
                    }
                    if (record.getEventType()!= null) {
                        VALUES("event_type", "#{eventType}");
                    }
                    if (record.getEventType()!= null) {
                        VALUES("event_level", "#{eventLevel}");
                    }
                    if (record.getEventTime()!= null) {
                        VALUES("event_time", "#{eventTime}");
                    }
                    if (record.getEventDesc()!= null) {
                        VALUES("event_desc", "#{eventDesc}");
                    }
                }
            }.toString();
        }
    }
}
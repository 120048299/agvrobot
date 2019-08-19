package com.wootion.dao.impl;

import com.wootion.dao.IAlarmDao;
import com.wootion.dao.ITaskDao;
import com.wootion.model.AlarmMsg;
import org.springframework.stereotype.Repository;

import java.util.List;

@SuppressWarnings("unchecked")
@Repository
public class AlarmDaoImpl extends  BaseDaoImpl implements IAlarmDao{

    @Override
    public List<AlarmMsg> selectMsgToSend(){
        // select * from alarm_msg where to_send_time &gt; now()  and  status &lt;= 0 order by to_send_time desc ;
        return (List<AlarmMsg>) super.selectAll("AlarmMsg.selectMsgToSend");
    }


}

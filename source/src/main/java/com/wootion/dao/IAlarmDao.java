package com.wootion.dao;

import com.wootion.model.*;

import java.util.List;


public interface IAlarmDao extends  IDao{
    List<AlarmMsg> selectMsgToSend();
}

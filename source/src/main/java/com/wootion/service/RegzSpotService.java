package com.wootion.service;

import com.github.pagehelper.PageInfo;
import com.wootion.model.RegzSpot;

import java.util.List;
import java.util.Map;

public interface RegzSpotService {

    List<Map> getRegzSpotList(String devTypeId);

    int updateSpotName(String spotName,String uid,Integer opsType,Integer meterType,Integer heattype,Integer savetype);
    int addRegzSpotDate(String spotName,String devType,String smallDevType,Integer opsType,Integer meterType,Integer heattype,Integer savetype);


    }

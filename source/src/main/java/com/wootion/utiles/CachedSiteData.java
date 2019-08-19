package com.wootion.utiles;

import com.wootion.model.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CachedSiteData {
    private String siteId;


    private Map<String,RegzSpot> regzSpotMap=new HashMap<>();
    private Map<String,PtzSet> ptzSetMap=new HashMap<>();
    private Map<String,PtzSet> ptzSetMapByUid=new HashMap<>();
    private Map<String,List<PtzSet>> ptzSetMapByRunMark=new HashMap<>();
    private Map<String,Map<String,List<String>>> ptzSetIdListByDevGroup=new HashMap<>();
    private Map<String,Dev> devMap=new HashMap<>();
    private Map<String,Area> areaMap=new HashMap<>();
    private List<RunMark> filtedRunMarkList;  // 非完整的,只有有连线的,删除了孤立点,有效的，给路径规划用
    private List<RunLine> filtedRunLineList;  //同上

    private Map<String, RunMark> filtedRunMarkMap; //用于判断点是否独立点？
    private Map<String, RunLine> filtedRunLineMap; //用于判断线是否已经存在？

    private List<RunMark> runMarkList;  // 完整的
    private List<RunLine> runLineList;  // 完整的
    private Map<String,RunMark > runMarkMap =new HashMap<>(); // 完整的

    private Map<String,Job> jobMap =new HashMap<>();

    private List<MaintainArea> maintainAreaList;

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public Map<String, PtzSet> getPtzSetMap() {
        return ptzSetMap;
    }

    public void setPtzSetMap(Map<String, PtzSet> ptzSetMap) {
        this.ptzSetMap = ptzSetMap;
    }

    public Map<String, PtzSet> getPtzSetMapByUid() {
        return ptzSetMapByUid;
    }

    public void setPtzSetMapByUid(Map<String, PtzSet> ptzSetMapByUid) {
        this.ptzSetMapByUid = ptzSetMapByUid;
    }


    public Map<String, List<PtzSet>> getPtzSetMapByRunMark() {
        return ptzSetMapByRunMark;
    }

    public void setPtzSetMapByRunMark(Map<String, List<PtzSet>> ptzSetMapByRunMark) {
        this.ptzSetMapByRunMark = ptzSetMapByRunMark;
    }

    public Map<String, Map<String, List<String>>> getPtzSetIdListByDevGroup() {
        return ptzSetIdListByDevGroup;
    }

    public void setPtzSetIdListByDevGroup(Map<String, Map<String, List<String>>> ptzSetIdListByDevGroup) {
        this.ptzSetIdListByDevGroup = ptzSetIdListByDevGroup;
    }

    public Map<String, Dev> getDevMap() {
        return devMap;
    }

    public void setDevMap(Map<String, Dev> devMap) {
        this.devMap = devMap;
    }

    public Map<String, Area> getAreaMap() {
        return areaMap;
    }

    public void setAreaMap(Map<String, Area> areaMap) {
        this.areaMap = areaMap;
    }

    public List<RunMark> getRunMarkList() {
        return runMarkList;
    }

    public void setRunMarkList(List<RunMark> runMarkList) {
        this.runMarkList = runMarkList;
    }

    public List<RunLine> getRunLineList() {
        return runLineList;
    }

    public void setRunLineList(List<RunLine> runLineList) {
        this.runLineList = runLineList;
    }

    public Map<String, RunMark> getRunMarkMap() {
        return runMarkMap;
    }

    public void setRunMarkMap(Map<String, RunMark> runMarkMap) {
        this.runMarkMap = runMarkMap;
    }

    public Map<String, Job> getJobMap() {
        return jobMap;
    }

    public void setJobMap(Map<String, Job> jobMap) {
        this.jobMap = jobMap;
    }

    public List<RunMark> getFiltedRunMarkList() {
        return filtedRunMarkList;
    }

    public void setFiltedRunMarkList(List<RunMark> filtedRunMarkList) {
        this.filtedRunMarkList = filtedRunMarkList;
    }

    public List<RunLine> getFiltedRunLineList() {
        return filtedRunLineList;
    }

    public void setFiltedRunLineList(List<RunLine> filtedRunLineList) {
        this.filtedRunLineList = filtedRunLineList;
    }

    public Map<String, RunLine> getFiltedRunLineMap() {
        return filtedRunLineMap;
    }

    public void setFiltedRunLineMap(Map<String, RunLine> filtedRunLineMap) {
        this.filtedRunLineMap = filtedRunLineMap;
    }

    public Map<String, RunMark> getFiltedRunMarkMap() {
        return filtedRunMarkMap;
    }

    public void setFiltedRunMarkMap(Map<String, RunMark> filtedRunMarkMap) {
        this.filtedRunMarkMap = filtedRunMarkMap;
    }

    public List<MaintainArea> getMaintainAreaList() {
        return maintainAreaList;
    }

    public void setMaintainAreaList(List<MaintainArea> maintainAreaList) {
        this.maintainAreaList = maintainAreaList;
    }
}

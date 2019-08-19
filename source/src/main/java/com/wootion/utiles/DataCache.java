package com.wootion.utiles;

import com.wootion.commons.ALARM_SOURCE_TYPE;
import com.wootion.mapper.*;
import com.wootion.model.*;
import com.wootion.task.alarm.SysAlarmHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;


@Component
public class DataCache extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(com.wootion.utiles.DataCache.class);

    @Autowired
    private SysParamMapper sysParamMapper;
    @Autowired
    private SiteMapper siteMapper;
    @Autowired
    private RobotMapper robotMapper;
    @Autowired
    private RobotParamMapper robotParamMapper;
    @Autowired
    private ChargeRoomMapper chargeRoomMapper;
    @Autowired
    private WeatherStationMapper weatherStationMapper;
    @Autowired
    private SysAlarmConfigMapper sysAlarmConfigMapper;
    @Autowired
    private SysAlarmLogMapper sysAlarmLogMapper;
    @Autowired
    private MaintainAreaMapper maintainAreaMapper;
    @Autowired
    private JobMapper jobMapper;
    @Autowired
    private PtzSetMapper ptzSetMapper;
    @Autowired
    private RunMarkMapper runMarkMapper;
    @Autowired
    private RunLineMapper runLineMapper;
    @Autowired
    private DevMapper devMapper;
    @Autowired
    private DictMapper dictMapper;
    @Autowired
    private DictTypeMapper dictTypeMapper;

    @Autowired
    private AreaMapper areaMapper;

    private static SysAlarmLogMapper staticSysAlarmLogMapper;

    //不区分site
    private static Map<String,List<Dict>> dictMap =new HashMap<>();
    private static Map<String,DevType> devTypeMap=new HashMap<>();
    private static List<RegzSpot> regzSpotsList;
    private static Map<String,RegzSpot> regzSpotMap=new HashMap<>();;
    private static Map<String, String> sysParamMap =new HashMap<>();
    private static Map<String, RegzObject> regzObjectMap =new HashMap<>();
    private static Map<String, RegzObjectType> regzObjectTypeMap =new HashMap<>();
    private static Map<String, SysAlarmConfig> sysAlarmConfigMap =new HashMap<>();
    private static Map<String, Site> siteMap =new HashMap<>();
    private static Map<String, Robot> robotMap =new HashMap<>();
    private static Map<String, List<RobotParam>> robotParamMap =new HashMap<>();
    private static Map<String, ChargeRoom> chargeRoomMap =new HashMap<>();
    private static Map<String, WeatherStation> weatherStationMap =new HashMap<>();
    private static Map<String, CachedSiteData> siteDataMap =new HashMap<>();
    private static Map<String, SysAlarmLog> sysAlarmLogMap = null; // 只读1次, 读取时初始化


    private int dataCacheTime;

    public int getDataCacheTime() {
        return dataCacheTime;
    }

    public void setDataCacheTime(int dataCacheTime) {
        this.dataCacheTime = dataCacheTime;
    }

    private boolean running = true;
    private static boolean reloadFlag = false;

    private static ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    private boolean finished = false;

    public boolean isFinished() {
        return finished;
    }



    @PostConstruct
    public void init() {
        staticSysAlarmLogMapper = sysAlarmLogMapper;
    }

    @Override
    public void run() {
        logger.info("datacache started! dataCacheTime="+dataCacheTime);
        while (running) {
            try {
                dbToCache();
                for (int i=0;i<dataCacheTime;i++){
                    if(reloadFlag){
                        reloadFlag=false;
                        break;
                    }
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                logger.warn("Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void reload(){
        reloadFlag=true;
    }

    public void dbToCache(){
        logger.info("dbToCache ...");
        try{
            rwl.writeLock().lock();
            List<DictType> dictTypeList=dictTypeMapper.selectAll();
            dictMap =new HashMap<>();
            if(dictTypeList!=null){
                dictMap=new HashMap<>();
                for(DictType item : dictTypeList){
                    Map params =new HashMap();
                    params.put("dictCode",item.getDictCode());
                    List<Dict> dictList=(List<Dict>) dictMapper.selectDict(item.getDictCode());
                    if(dictList!=null && dictList.size()>0){
                        dictMap.put(item.getDictCode(),dictList);
                    }
                }
            }

            List<SysAlarmConfig> sysAlarmConfigs = sysAlarmConfigMapper.findAll();
            if (sysAlarmConfigs!=null){
                sysAlarmConfigMap=new HashMap<>();
                for(SysAlarmConfig item : sysAlarmConfigs){
                    sysAlarmConfigMap.put(item.getAlarmCode(), item);
                }
            }


            List<SysParam> sysParamList = sysParamMapper.findAll();
            if(sysParamList!=null){
                sysParamMap=new HashMap<>();
                for(SysParam item : sysParamList){
                    sysParamMap.put(item.getKey(), item.getValue());
                }
            }

            List<Robot> robots = robotMapper.findAll();
            if(robots !=null){
                for(Robot item : robots ){
                    robotMap.put(item.getUid(),item);
                }
            }
            if(robots !=null){
                for(Robot item : robots ){
                    List<RobotParam> robotParamList = robotParamMapper.getRobotParam(item.getUid());
                    robotParamMap.put(item.getUid(),robotParamList);
                }
            }
            List<ChargeRoom> chargeRooms = chargeRoomMapper.findAll();
            if(chargeRooms !=null){
                for(ChargeRoom item : chargeRooms ){
                    chargeRoomMap.put(item.getUid(),item);
                }
            }

            List<WeatherStation> weatherStations = weatherStationMapper.findAll();
            if(weatherStations !=null){
                for(WeatherStation item : weatherStations ){
                    weatherStationMap.put(item.getUid(),item);
                }
            }

            List<Site> sites = siteMapper.findAll();
            if (sites!=null){
                siteMap=new HashMap<>();
                for(Site item : sites){
                    String siteId = item.getUid();
                    CachedSiteData cachedSiteData=querySiteData(siteId);
                    siteMap.put(siteId, item);
                    siteDataMap.put(siteId,cachedSiteData);
                }
            }

            if (sysAlarmLogMap==null) {
                sysAlarmLogMap = new HashMap<>();
                List<SysAlarmLog> sysAlarmLogList = sysAlarmLogMapper.findListWhere(null,0);
                if(sysAlarmLogList !=null){
                    for(SysAlarmLog item : sysAlarmLogList){
                        String key = SysAlarmHelper.getKey(item.getSiteId(),item.getSourceType(),item.getSourceId(),item.getAlarmCode());
                        sysAlarmLogMap.put(key, item);
                    }
                }
            }

            if (!finished) {
                finished = true;
                logger.info("dbToCache finished!");
            }
        }
        catch (Exception e) {
            logger.warn("Exception: " + e.getMessage());
            e.printStackTrace();
        }finally {
            rwl.writeLock().unlock();
        }
    }

    private CachedSiteData querySiteData(String siteId) {
        CachedSiteData siteData=new CachedSiteData();
        siteData.setSiteId(siteId);

        List<PtzSet> ptzSetList = ptzSetMapper.selectListBySite(siteId);
        Map<String,PtzSet> ptzSetMap=new HashMap<>();
        Map<String,PtzSet> ptzSetMapByUid=new HashMap<>();
        Map<String,List<PtzSet>> ptzSetMapByRunMark=new HashMap<>();
        Map<String,Map<String,List<String>>> ptzSetIdListByDevGroup=new HashMap<>();
        if(ptzSetList!=null){
            for(PtzSet item : ptzSetList){
                ptzSetMapByUid.put(item.getUid(),item);
                if (item.getPtzType()==4) {
                    List<PtzSet> ptzSets = ptzSetMapByRunMark.get(item.getMarkId());
                    if (ptzSets==null) {
                        ptzSets = new ArrayList<>();
                    }
                    ptzSets.add(item);
                    ptzSetMapByRunMark.put(item.getMarkId(),ptzSets);
                }
            }
        }
        siteData.setPtzSetMap(ptzSetMap);
        siteData.setPtzSetMapByUid(ptzSetMapByUid);
        siteData.setPtzSetMapByRunMark(ptzSetMapByRunMark);
        siteData.setPtzSetIdListByDevGroup(ptzSetIdListByDevGroup);

        List<Dev> devList = devMapper.selectAll(siteId);
        Map<String,Dev> devMap=new HashMap<>();
        if(devList!=null){
            for(Dev item : devList){
                devMap.put(item.getUid(),item);
            }
        }
        siteData.setDevMap(devMap);

        List<Area> areaList = areaMapper.selectAll(siteId);
        Map<String,Area> areaMap=new HashMap<>();
        if(areaList!=null){
            for(Area item : areaList){
                areaMap.put(item.getUid(),item);
            }
        }
        siteData.setAreaMap(areaMap);
        List<RunMark> runMarkList = runMarkMapper.getRunMarkBySiteId(siteId);
        List<RunLine> runLineList =runLineMapper.getRunLineBySiteId(siteId);
        siteData.setRunLineList(runLineList);
        siteData.setRunMarkList(runMarkList);

        Map<String,RunMark > runMarkMap =new HashMap<>(); // 完整的
        if(runMarkList!=null){
            for(RunMark item : runMarkList){
                runMarkMap.put(item.getUid(),item);
            }
        }
        siteData.setRunMarkMap(runMarkMap);
        List<RunMark> filtedRunMarkList=new ArrayList<>();//有效的点(包含检修区域)
        List<RunLine> filtedRunLineList=new ArrayList<>();//有效的线
        Map<String, RunMark> filtedRunMarkMap = new HashMap<>();
        Map<String, RunLine> filtedRunLineMap = new HashMap<>();
        if(runMarkList!=null && runLineList!=null){
            //处理规避 孤立的run_mark
            Iterator<RunMark> it = runMarkList.iterator();
            while(it.hasNext()){
                RunMark runMark = it.next();
                boolean have=false;
                for (RunLine runLine: runLineList){
                    if(runLine.getMarkId1().equals(runMark.getUid()) || runLine.getMarkId2().equals(runMark.getUid())){
                        have=true;
                        break;
                    }
                }
                if(have){
                    filtedRunMarkList.add(runMark);
                    filtedRunMarkMap.put(runMark.getUid(), runMark);
                }
            }
            //处理规避 空线，指向不存在的run_mark
            Iterator<RunLine> lineIt = runLineList.iterator();
            while(lineIt.hasNext()){
                RunLine runLine = lineIt.next();
                boolean have=false;
                for (RunMark runMark : filtedRunMarkList){
                    if(runLine.getMarkId1().equals(runMark.getUid()) || runLine.getMarkId2().equals(runMark.getUid())){
                        have=true;
                        break;
                    }
                }
                if(have){
                    filtedRunLineList.add(runLine);
                }
            }
            //规避 两点之间线路重复，最多一条线
            lineIt = filtedRunLineList.iterator();
            while(lineIt.hasNext()){
                RunLine runLine = lineIt.next();
                String key = runLine.getMarkId1()+"---"+runLine.getMarkId2();
                if(filtedRunLineMap.containsKey(key)){
                    lineIt.remove();//删除重复的线
                    continue;
                }
                filtedRunLineMap.put(key,runLine);
            }
        }
        siteData.setFiltedRunLineList(filtedRunLineList);
        siteData.setFiltedRunMarkList(filtedRunMarkList);
        siteData.setFiltedRunLineMap(filtedRunLineMap);
        siteData.setFiltedRunMarkMap(filtedRunMarkMap);

        List<Job> jobList = jobMapper.query(null,siteId,null);
        Map<String,Job> jobMap =new HashMap<>();
        if(jobList !=null){
            for(Job job : jobList){
                jobMap.put(job.getUid(),job);
            }
        }
        siteData.setJobMap(jobMap);

        List<MaintainArea> maintainAreaList = maintainAreaMapper.getAll(siteId);
        siteData.setMaintainAreaList(maintainAreaList);
        return siteData;
    }

    public static List<Dict> findDict(String dictCode){
        return dictMap.get(dictCode);
    }

    public static String findDictNameByValue(String dictCode, String value){
        List<Dict> list=dictMap.get(dictCode);
        if(list!=null){
            for(Dict dict:list){
                if(dict.getValue().equals(value)){
                    return dict.getName();
                }
            }
        }
        return null;
    }

    public static DevType findDevType(String uid){
        return devTypeMap.get(uid);
    }

    public static String findSysParamByKey(String key){
        return sysParamMap.get(key);
    }

    public static String getSysParamStr(String key){
        String value = DataCache.findSysParamByKey(key);
        return value;
    }

    public static int getSysParamInt(String key){
        String value = DataCache.findSysParamByKey(key);
        return Integer.valueOf(value).intValue();
    }

    public static double getSysParamDouble(String key){
        String value = DataCache.findSysParamByKey(key);
        return Double.valueOf(value).doubleValue();
    }

    public static float getSysParamFloat(String key){
        String value = DataCache.findSysParamByKey(key);
        return Float.valueOf(value).floatValue();
    }

    public static String getSysParamStr(String key, String def){
        String value = DataCache.findSysParamByKey(key);
        if (value==null) {
            return def;
        }
        return value;
    }

    public static int getSysParamInt(String key, int def){
        String value = DataCache.findSysParamByKey(key);
        if (value==null) {
            return def;
        }
        return Integer.valueOf(value).intValue();
    }

    public static double getSysParamDouble(String key, double def){
        String value = DataCache.findSysParamByKey(key);
        if (value==null) {
            return def;
        }
        return Double.valueOf(value).doubleValue();
    }

    public static float getSysParamFloat(String key, float def){
        String value = DataCache.findSysParamByKey(key);
        if (value==null) {
            return def;
        }
        return Float.valueOf(value).floatValue();
    }

    public static RegzObject findRegzObject(String regzObjectId){
        RegzObject regzObject=regzObjectMap.get(regzObjectId);
        return regzObject;
    }

    public static RegzObjectType findRegzObjectType(String regzObjectTypeId){
        RegzObjectType regzObjectType=regzObjectTypeMap.get(regzObjectTypeId);
        return regzObjectType;
    }


    public static SysAlarmConfig findSysAlarmConfig(String alarmCode){
        SysAlarmConfig sysAlarmConfig =sysAlarmConfigMap.get(alarmCode);
        return sysAlarmConfig;
    }

    public static Site getSite(String uid){
        Site site =siteMap.get(uid);
        return site;
    }

    public static Robot getRobot(String uid){
        Robot robot =(Robot)robotMap.get(uid);
        return robot;
    }
    public static List<RobotParam> getRobotParam(String robotId){
        List<RobotParam> robotParamList =robotParamMap.get(robotId);
        return robotParamList;
    }

    public static ChargeRoom getChargeRoom(String uid){
        ChargeRoom chargeRoom =(ChargeRoom)chargeRoomMap.get(uid);
        return chargeRoom;
    }

    public static WeatherStation getWeatherStation(String uid){
        WeatherStation weatherStation =(WeatherStation)weatherStationMap.get(uid);
        return weatherStation;
    }

    public static String getSysAlarmSourceName(Integer sourceType, String sourceId){
        switch (ALARM_SOURCE_TYPE.fromInt(sourceType)) {
            case SYSTEM:
                return "系统(" + sourceId + ")";
            case ROBOT:
                Robot robot = getRobot(sourceId);
                if (robot!=null) {
                    return robot.getName();
                } else {
                    return "机器人(" + sourceId + ")";
                }
            case CHARGE_ROOM:
                ChargeRoom chargeRoom = getChargeRoom(sourceId);
                if (chargeRoom!=null) {
                    return chargeRoom.getName();
                } else {
                    return "充电房(" + sourceId + ")";
                }
            case WEATHER_STATION:
                WeatherStation weatherStation = getWeatherStation(sourceId);
                if (weatherStation!=null) {
                    return weatherStation.getName();
                } else {
                    return "气象站(" + sourceId + ")";
                }
        }
        return "未知";
    }


    public static RegzSpot findRegzSpot(String uid){
        try {
            rwl.readLock().lock();
            return regzSpotMap.get(uid);
        }finally {
            rwl.readLock().unlock();
        }
    }


    //应该没有用了
   /*public static PtzSet findPtzSet(String siteId,String uid){
        if(siteId==null){
            return null;
        }
        try {
            rwl.readLock().lock();
            return siteDataMap.get(siteId).getPtzSetMap().get(uid);
        }finally {
            rwl.readLock().unlock();
        }
    }*/

    public static PtzSet findPtzSetByUid(String siteId,String uid){
        if(siteId==null){
            return null;
        }
        try {
            rwl.readLock().lock();
            return siteDataMap.get(siteId).getPtzSetMapByUid().get(uid);
        }finally {
            rwl.readLock().unlock();
        }
    }

    public static List<PtzSet> findPtzSetByRunMark(String siteId,String runMarkUid){
        if(siteId==null){
            return null;
        }
        try {
            rwl.readLock().lock();
            return siteDataMap.get(siteId).getPtzSetMapByRunMark().get(runMarkUid);
        }finally {
            rwl.readLock().unlock();
        }
    }

    public static List<String> findPtzSetIdListByDevGroup(String siteId, String devId, String groupId){
        if(siteId==null){
            return null;
        }
        try {
            rwl.readLock().lock();
            Map<String, List<String>> groupByDev = siteDataMap.get(siteId).getPtzSetIdListByDevGroup().get(devId);
            if (groupByDev!=null) {
                return groupByDev.get(groupId);
            }
            return null;
        }finally {
            rwl.readLock().unlock();
        }
    }

    public static Dev findDev(String siteId,String uid){
        if(siteId==null){
            return null;
        }
        try {
            rwl.readLock().lock();
            return siteDataMap.get(siteId).getDevMap().get(uid);
        }finally {
            rwl.readLock().unlock();
        }
    }


    public static Area findArea(String siteId,String uid){
        if(siteId==null){
            return null;
        }
        try {
            rwl.readLock().lock();
            return siteDataMap.get(siteId).getAreaMap().get(uid);
        }finally {
            rwl.readLock().unlock();
        }
    }


    public static Map<String,Dev> getDevMap(String siteId){
        if(siteId==null){
            return null;
        }
        try {
            rwl.readLock().lock();
            return siteDataMap.get(siteId).getDevMap();
        }finally {
            rwl.readLock().unlock();
        }
    }

    public static List<RunMark> getRunMarkList(String siteId) {
        if(siteId==null){
            return null;
        }
        try {
            rwl.readLock().lock();
            return siteDataMap.get(siteId).getRunMarkList();
        }finally {
            rwl.readLock().unlock();
        }
    }

    public static List<RunLine> getRunLineList(String siteId) {
        if(siteId==null){
            return null;
        }
        try {
            rwl.readLock().lock();
            return siteDataMap.get(siteId).getRunLineList();
        }finally {
            rwl.readLock().unlock();
        }
    }

    public static List<RunMark> getFiltedRunMarkList(String siteId) {
        if(siteId==null){
            return null;
        }
        try {
            rwl.readLock().lock();
            return siteDataMap.get(siteId).getFiltedRunMarkList();
        }finally {
            rwl.readLock().unlock();
        }
    }

    public static List<RunLine> getFiltedRunLineList(String siteId) {
        if(siteId==null){
            return null;
        }
        try {
            rwl.readLock().lock();
            return siteDataMap.get(siteId).getFiltedRunLineList();
        }finally {
            rwl.readLock().unlock();
        }
    }

    public static RunMark getFiltedRunMark(String siteId, String runMarkId) {
        if(siteId==null){
            return null;
        }
        try {
            rwl.readLock().lock();
            return siteDataMap.get(siteId).getFiltedRunMarkMap().get(runMarkId);
        }finally {
            rwl.readLock().unlock();
        }
    }

    public static RunLine findFiltedRunLine(String siteId, String markId1, String markId2){
        if(siteId==null){
            return null;
        }
        try {
            rwl.readLock().lock();
            String key = markId1+"---"+markId2;
            RunLine runLine = siteDataMap.get(siteId).getFiltedRunLineMap().get(key);
            if (runLine!=null) {
                return runLine;
            }
            key = markId2+"---"+markId1;
            runLine = siteDataMap.get(siteId).getFiltedRunLineMap().get(key);
            if (runLine!=null) {
                return runLine;
            }
            return null;
        }finally {
            rwl.readLock().unlock();
        }
    }

    public static RunMark findRunMark(String siteId,String uid){
        if(siteId==null){
            return null;
        }
        try {
            rwl.readLock().lock();
            return siteDataMap.get(siteId).getRunMarkMap().get(uid);
        }finally {
            rwl.readLock().unlock();
        }
    }

    public static Job findJob(String siteId, String uid){
        if(siteId==null){
            return null;
        }
        try {
            rwl.readLock().lock();
            return siteDataMap.get(siteId).getJobMap().get(uid);
        }finally {
            rwl.readLock().unlock();
        }
    }

    public static SysAlarmLog findFromSysAlarmLogMap(String key) {
        if(sysAlarmLogMap==null){
            return null;
        }
        try {
            rwl.readLock().lock();
            return sysAlarmLogMap.get(key);
        }finally {
            rwl.readLock().unlock();
        }
    }

    public static void addToSysAlarmLogMap(SysAlarmLog item) {
        if(sysAlarmLogMap==null){
            return;
        }

        staticSysAlarmLogMapper.insert(item);

        try {
            rwl.readLock().lock();
            sysAlarmLogMap.put(SysAlarmHelper.getKey(item), item);
        }finally {
            rwl.readLock().unlock();
        }

    }

    public static void removeFromSysAlarmLogMap(SysAlarmLog item) {
        if(sysAlarmLogMap==null){
            return;
        }

        staticSysAlarmLogMapper.update(item);

        try {
            rwl.readLock().lock();
            sysAlarmLogMap.remove(SysAlarmHelper.getKey(item));
        }finally {
            rwl.readLock().unlock();
        }
    }

    public static List<MaintainArea> getMaintainAreaList(String siteId) {
        if(siteId==null){
            return null;
        }
        try {
            rwl.readLock().lock();
            return siteDataMap.get(siteId).getMaintainAreaList();
        }finally {
            rwl.readLock().unlock();
        }
    }

    public static String findRobotParam(String robotId,String key){
        List<RobotParam> robotParamList=robotParamMap.get(robotId);
        if(robotParamList!=null){
            for(RobotParam item:robotParamList){
                if(item.getKey().equals(key)){
                    return item.getValue();
                }
            }
        }
        return null;
    }

    public static String getRobotParamStr(String robotId,String key){
        String value=findRobotParam(robotId,key);
        if(value!=null){
            return value;
        }
        return "";
    }

    public static int getRobotParamInt(String robotId,String key){
        String value=findRobotParam(robotId,key);
        if(value!=null){
            return Integer.parseInt(value);
        }
        return 0;
    }

    public static double getRobotParamDouble(String robotId,String key){
        String value=findRobotParam(robotId,key);
        if(value!=null){
            return Double.valueOf(value).doubleValue();
        }
        return 0;
    }

    public static float getRobotParamFloat(String robotId,String key){
        String value=findRobotParam(robotId,key);
        if(value!=null){
            return Float.valueOf(value).floatValue();
        }
        return 0;
    }


}

package com.wootion.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wootion.agvrobot.utils.DateUtil;
import com.wootion.mapper.PtzSetMapper;
import com.wootion.mapper.TaskLogMapper;
import com.wootion.model.PtzSet;
import com.wootion.model.RegzSpot;
import com.wootion.model.TaskLog;
import com.wootion.protocols.robot.RosBridgeClient;
import com.wootion.protocols.robot.msg.Advertise;
import com.wootion.protocols.robot.msg.MsgNames;
import com.wootion.protocols.robot.msg.ReadScaleAckMsg;
import com.wootion.protocols.robot.msg.Subscribe;
import com.wootion.utiles.DataCache;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 读表队列：找表完成后，直接放到读表队列中来，立即可以开始下一个任务。读表对了可以延迟执行，总体节约了时间。
 */
@Component
public class ReadScaleQueue extends  Thread{
    private static final Logger logger = LoggerFactory.getLogger(ReadScaleQueue.class);


    private static LinkedBlockingQueue<Object> queue = new LinkedBlockingQueue<>();

    public static void addEvent(Object evt) {
        queue.add(evt);
    }

    @Autowired
    private TaskLogMapper taskLogMapper;

    @Autowired
    private PtzSetMapper ptzSetMapper;

    private  boolean  running = true;

    public void close() {
        running = false;
    }


    TaskLog taskLog;
    PtzSet ptzSet;
    RegzSpot regzSpot;

    private String readScaleRosUrl;
    private Channel ch;
    private boolean online=false;


    /**
     * 网络连接成功
     * @param ch
     */
    public void setRobotCh(Channel ch) {

        Channel oldCh = this.ch;
        if (oldCh != null && oldCh.isOpen()) {
            try {
                oldCh.close().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (ch == null) {
            //只要链路出问题即设置离线和停止任务
            logger.error(" *****read scale ros :  server  ROS channel  IS NULL ");
            online=false;
            this.ch=null;
            return ;
        }
        this.ch=ch;
        online=true;
        connectRosLink(new Advertise(MsgNames.topic_read_scale_command, MsgNames.topic_read_scale_command_type));
        connectRosLink(new Subscribe(MsgNames.topic_read_scale_ack, MsgNames.topic_read_scale_ack_type));
    }

    RosBridgeClient rosBridgeClient;

    public <T> void connectRosLink(T t) {
        if (ch == null || !ch.isOpen() || !ch.isActive()) {
            logger.error("read scale ros :connectRosLink channel is not active");
            this.online=false;
            return;
        }
        String msg = JSONObject.toJSON(t).toString();
        ChannelFuture channelFuture = ch.writeAndFlush(new TextWebSocketFrame(msg));
        logger.warn("robot init: " + msg);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    logger.info("read scale ros : write subscribe  status success"+msg);
                } else {
                    logger.warn("read scale ros : write subscribe robot status failed!"+msg);
                }
            }
        });
    }

    private void init(){
        String serverIp=DataCache.getSysParamStr("ros.serverIp");
        String url="ws://"+serverIp+":9090/";
        url="ws://"+"192.168.100.61"+":9090/";
        /*if(rosBridgeClient!=null){
            try{
                rosBridgeClient.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }*/
        logger.info("-------------read scale ros : start ros bridge ------------");
        rosBridgeClient = new RosBridgeClient(url);
        try {
            rosBridgeClient.open();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.warn("read scale ros : failed open in StartRosBridge, {}", e.getMessage());
            setRobotCh(null);
        }
    }

    private void checkOnline() {
        Runnable r = ()-> {
            int count=0;
            while (true){
                try{
                    Thread.sleep(1000);
                    //System.out.println("----------------------------------------");
                    count++;
                    if(count<10){
                        continue;
                    }
                    count=0;
                    if(!online || ch==null ||  !ch.isOpen() || !ch.isActive()){
                        System.out.println("online="+online+" ch==null "+(ch==null ));
                        if(ch!=null){
                            System.out.println(" ch.isOpen()="+ch.isOpen()+" ch.isActive()=" +ch.isActive() );
                        }
                        init();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        new Thread(r).start();
    }

    private void getConnected() {
        Runnable r = ()-> {
            int count=0;
            while (true){
                try{
                    Thread.sleep(1000);
                    Object evt = queue.take();
                    if(evt instanceof Channel){
                        logger.info("read scale ros : ros  init ok= ",evt.toString());
                        setRobotCh((Channel) evt);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        new Thread(r).start();
    }


    @Override
    public void run() {
        logger.info("read queque started!");
        init();
        checkOnline();
        getConnected();
        //处理读表队列
        while (running) {
            try {
                Thread.sleep(1000);
                if(!online){
                    continue;
                }
               /* List<FindScaleResult> findScaleResultList = findScaleResultMapper.selectWaitReadList();
                if(findScaleResultList==null){
                    continue;
                }
                for(FindScaleResult item:findScaleResultList){
                    if(!online){
                        break;
                    }
                    try{
                        FindResultValues findResultValues=parseFindResult(item.getFindResult());
                        readScale(item,findResultValues);
                        item.setStatus(1);
                        findScaleResultMapper.update(item);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }*/
            } catch (Exception e) {
                logger.warn("Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }
        logger.info("read scale queque stopped!");
    }


    private FindResultValues parseFindResult(String strFindResult){
        try {
            FindResultValues findResultValues=new FindResultValues();
            System.out.println(strFindResult);
            JSONObject resultObj=JSONObject.parseObject(strFindResult);
            String fileName = resultObj.getString("filename");
            String infraredFileName = resultObj.getString("infrared_filename");
            //JSONArray foreignFileNameArray=resultObj.getJSONArray("foreign_filename");
            JSONArray foreignFileNameArray = resultObj.getJSONArray("foreign_filename");
            JSONArray roiVertexArray = resultObj.getJSONArray("roi_vertex");
            JSONArray roiVertexThermalArray = resultObj.getJSONArray("infrared_roi_vertex");
            String foreignFileNames[] = null;
            if (foreignFileNameArray != null) {
                foreignFileNames = new String[foreignFileNameArray.size()];
                for (int i = 0; i < foreignFileNameArray.size(); i++) {
                    foreignFileNames[i] = foreignFileNameArray.getString(i);
                }
            }
            Integer roiVertexs[] = null;
            if (roiVertexArray != null) {
                roiVertexs = new Integer[roiVertexArray.size() * 2];
                for (int i = 0; i < roiVertexArray.size(); i++) {
                    int x = roiVertexArray.getJSONArray(i).getInteger(0);
                    int y = roiVertexArray.getJSONArray(i).getInteger(1);
                    roiVertexs[2*i] = x;
                    roiVertexs[2*i + 1] = y;
                }
            }
            Integer roiVertexThermals[] = null;
            if (roiVertexThermalArray != null) {
                roiVertexThermals = new Integer[roiVertexThermalArray.size() * 2];
                for (int i = 0; i < roiVertexThermalArray.size(); i++) {
                    int x = roiVertexThermalArray.getJSONArray(i).getInteger(0);
                    int y = roiVertexThermalArray.getJSONArray(i).getInteger(1);
                    roiVertexThermals[2*i] = x;
                    roiVertexThermals[2*i + 1] = y;
                }
            }

            findResultValues.setFileName(fileName);
            findResultValues.setInfraredFileName(infraredFileName);
            findResultValues.setForeignFileNames(foreignFileNames);
            findResultValues.setRoi_vertex(roiVertexs);
            findResultValues.setRoi_vertex_thermal(roiVertexThermals);
            int foreignDetect = DataCache.getSysParamInt("task.foreignDetect");
            findResultValues.setForeign_detect(foreignDetect);
            return  findResultValues;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
/*
    public int readScale(FindScaleResult findScaleResult,FindResultValues findResultValues){
        String taskLogId = findScaleResult.getTaskLogId();
        taskLog=taskLogMapper.select(taskLogId);
        //构造读表消息
        ptzSet = DataCache.findPtzSetByUid(taskLog.getSiteId(),taskLog.getPtzSetId());
        if(ptzSet==null){
            return -1;
        }
        regzSpot = DataCache.findRegzSpot(ptzSet.getRegzSpotId());
        if(regzSpot==null){
            return -1;
        }
        //如果识别类型是表计识别或者位置 ，则添加读表动作
        if (regzSpot.getOpsType() == OPS_TYPE.READ_SCALE.getValue()
                || regzSpot.getOpsType() == OPS_TYPE.POSTION_STATE.getValue()
                || regzSpot.getOpsType() == OPS_TYPE.INFRA.getValue()) {
            int opId = MemUtil.newOpId();
            String picPath = ptzSet.getImageProcessPath();
            ReadScaleCommandMsg commandMsg = new ReadScaleCommandMsg(opId, picPath);
            //todo wait 可能有问题，红外文件和可见光文件顺序？
            String filesNames[]=new String[2];
            filesNames[0]=findResultValues.getFileName();
            filesNames[1]=findResultValues.getInfraredFileName();
            commandMsg.setFilename(filesNames);
            commandMsg.setRoi_vertex(findResultValues.getRoi_vertex());
            commandMsg.setRoi_vertex_thermal(findResultValues.getRoi_vertex_thermal());
            commandMsg.setForeign_detect(findResultValues.getForeign_detect());
            commandMsg.setForeign_filename(findResultValues.getForeignFileNames());
            if(findResultValues.getForeign_detect()==1){
                String s[]=null;
                commandMsg.setFilename(s);
            }
            logger.info("send read cmd msg "+commandMsg.toString());
            ReadScaleOp readScaleOp = new ReadScaleOp(opId, commandMsg);
            int readScaleTimeLimit = DataCache.getSysParamInt("task.readScaleTimeout", 30);
            int ret=doReadStep(taskLog,readScaleOp,readScaleTimeLimit);
            updatePtzSetLastLogId(taskLog);
            if(ret==0 && findResultValues.getForeign_detect()!=1){
                // 计算告警
               beginCalcAlarm(taskLog);
            }
            //读表处理结束通知前端读取列表
            MemRobot memRobot=MemUtil.queryRobotById(taskLog.getRobotId());
            memRobot.pushTaskLogAllFinised();
        }
        return 1;
    }*/

    /**
     *
     * @param taskLog
     * @param op
     * @param timeOut
     * @return 1 读表成功
     */
   /* protected int doReadStep(TaskLog taskLog,ReadScaleOp op,int timeOut){
        int ret=-1;
        Integer transId=0;
        RosCommandExcecutor rosCommandExcecutor =new RosCommandExcecutor();
        rosCommandExcecutor.setCh(this.ch);
        Date beginTime=new Date();
        PtzSet ptzSet = DataCache.findPtzSetByUid(taskLog.getSiteId(), taskLog.getPtzSetId());
        ReadScaleCommandMsg commandMsg = op.getMsg();
        ReadScaleAckMsg ackMsg=null;
        if(Debug.haveReadScale==1){
            Result result =rosCommandExcecutor.publish(op,timeOut);
            if(result.getCode()==1) {
                JSONObject jobj = (JSONObject) result.getData();
                jobj = jobj.getJSONObject("msg");
                ackMsg = toReadScaleAckMsg(jobj);
            }else{
                logger.error("read failed:"+result.getMsg()+" commandMsg="+commandMsg);
                saveLog(-2,"读表失败");
            }
        }else{
            //模拟一个数据
            ackMsg=mockReadScaleAckMsg(ptzSet.getImageProcessPath());
        }
        if(ackMsg!=null){
            int foreign_detect=commandMsg.getForeign_detect();
            if(foreign_detect==1){
                //只读异物，成功失败
                int writeForeignResult=-1;
                writeForeignResult=writeForeignReadScaleResult(taskLog,ackMsg);
                if(writeForeignResult>=0){
                    saveLog(2,"读表成功");
                }else{
                    saveLog(-2,"读表失败");
                }
                return 1;
            }else{
                int writeResult=-1;
                Date endTime=new Date();
                logger.info("读表返回消息  "+ptzSet.toShortString()+",耗时： "+(endTime.getTime()-beginTime.getTime())+"ms" +ackMsg);
                writeResult = writeReadScaleResult(taskLog,ackMsg);
                if(foreign_detect==2){
                    writeForeignReadScaleResult(taskLog,ackMsg);
                }
                if(writeResult >= 0){
                    saveLog(2,"读表成功");
                    return 1;
                }else{
                    saveLog(-2,"读表失败");
                }
            }
        }
        return ret;
    }
*/
    /**
     * 记录识别结果
     * @param
     * @return
     */
  /*  private int writeReadScaleResult(TaskLog taskLog,ReadScaleAckMsg msg){
        String ptzSetId = taskLog.getPtzSetId();
        //构造读表消息
        PtzSet ptzSet = DataCache.findPtzSetByUid(taskLog.getSiteId(),ptzSetId);
        RegzSpot regzSpot = null;
        if (ptzSet != null) {
            regzSpot = DataCache.findRegzSpot(ptzSet.getRegzSpotId());
        }
        RegzObject regzObject = DataCache.findRegzObject(ptzSet.getRegzObjectId());
        List<RegzObjectField> fields=regzObject.getFieldList();
        int size=fields.size();
        if(size==0){
            logger.error("配置错误，"+ptzSet.toShortString()+regzObject.getName()+"regz_object没有定义字段field.");
            return -1;
        }

        String result=msg.getResult();
        if(result == null || result.equals("failed") ){
            logger.error("读表失败 readscale failed, "  +ptzSet.toShortString());
            return -2;
        }

        String picPath[] = msg.getPicture_path();

        //接口返回全路径; 读表结果存表/picture/uid/xxxx.jpg
        if(picPath!=null){
            // 可见光图片
            if(picPath.length>0 && picPath[0]!=null) {
                int k = picPath[0].indexOf("picture");
                if (k >= 0) {
                    picPath[0] = picPath[0].substring(k);
                }
                taskLog.setImgFile("/"+picPath[0]);
            }
            // 热像仪图片
            if(picPath.length>1 && picPath[1]!=null) {
                int k = picPath[1].indexOf("picture");
                if (k >= 0) {
                    picPath[1] = picPath[1].substring(k);
                }
                taskLog.setInfraredFile("/"+picPath[1]);
            }
            logger.info("读表"+ptzSet.toShortString()+" 接口返回图片文件路径:img= "+ taskLog.getImgFile()+" ,infrared="+taskLog.getInfraredFile());
        }

        if (taskLog.getImgFile()==null) {
            logger.error("读表未返回可见光图片 "+ptzSet.getDescription()+" "+regzObject.getName());
            return -3;
        }

        // 红外测温
        if (regzSpot.getOpsType() == 4 && taskLog.getInfraredFile()==null) {
            logger.error("读表未返回热像仪图片 "+ptzSet.getDescription() +" " +regzObject.getName());
            return -4;
        }
        double scales[] = msg.getScale();
        double posibilitys[] = msg.getPosibility();

        if(size!=scales.length){
            logger.error("读表返回结果数量和巡检点设置的识别对象的结果数量不同:"+ptzSet.toShortString()+regzObject.getName()
                    +"识别对象设置结果数量："+size+"读表识别返回结果数量："+scales.length);
            return -5;
        }
        taskLogMapper.update(taskLog);
        for(int i=0;i<scales.length;i++){
            saveResult(fields.get(i), taskLog,scales[i], posibilitys[i], fields.get(i).getUid());
            logger.info("读表返回结果 ok "+ptzSet.toShortString()+ " scales["+i+"]="+scales[i]+" posibilitys["+i+"]="+posibilitys[i]);
        }
        logger.info("读表保存数据成功 "+ptzSet.toShortString()+" "+regzObject.getName());
        return 1;
    }
*/



    private void saveLog(int status, String memo){
        taskLog.setStatus(status);
        taskLog.setMemo(memo);
        taskLog.setFinishTime(new Date());
        taskLogMapper.update(taskLog);
    }

    //记录最后一次成功执行的taskLogId到ptz_set表
    private void updatePtzSetLastLogId(TaskLog taskLog){
        PtzSet  ptzSet=new PtzSet();
        ptzSet.setUid(taskLog.getPtzSetId());
        ptzSetMapper.update(ptzSet);
    }

    private ReadScaleAckMsg toReadScaleAckMsg(JSONObject jsonObject) {
        ReadScaleAckMsg msg = new ReadScaleAckMsg();
        msg.setHeader(jsonObject.getJSONObject("header"));
        msg.setRobot_ip(jsonObject.getString("robot_ip"));
        msg.setTrans_id(jsonObject.getIntValue("trans_id"));
        msg.setPicture_path(jsonObject.getJSONArray("picture_path"));
        msg.setScale(jsonObject.getJSONArray("scale"));
        msg.setPosibility(jsonObject.getJSONArray("posibility"));
        msg.setResult(jsonObject.getString("result"));
        //foreign
        msg.setForeign_picture_path(jsonObject.getJSONArray("foreign_picture_path"));
        msg.setForeign_scale(jsonObject.getJSONArray("foreign_scale"));
        msg.setForeign_posibility(jsonObject.getJSONArray("foreign_posibility"));
        msg.setForeign_result(jsonObject.getString("foreign_result"));
        return msg;
    }


    /**
     *
     * @param ptzSetPath 全路径,以/结束
     * @return
     */
    private ReadScaleAckMsg mockReadScaleAckMsg(String ptzSetPath) {
        String picPath=ptzSetPath.replace("preset","picture");
        if(!picPath.endsWith("/")){
            picPath=picPath+"/";
        }
        ReadScaleAckMsg msg = new ReadScaleAckMsg();
        msg.setTrans_id(0);
        String [] path=new String[1];
        path[0]=picPath+ DateUtil.dateToString(new Date(),"yyyyMMddHHmmss")+".jpg";
        msg.setPicture_path(path);
        double[] scale=new double[1];
        scale[0]=45.67;
        msg.setScale(scale);
        double[] posibility=new double[1];
        posibility[0]=0.998;
        msg.setPosibility(posibility);
        msg.setResult("success");
        //foreign
        String [] fPath=new String[1];
        String fPicBase=picPath.substring(0,picPath.indexOf("picture"))+"picture/foreign_detect/"+picPath.substring(picPath.indexOf("picture")+8);
        fPath[0]=fPicBase+DateUtil.dateToString(new Date(),"yyyyMMddHHmmss")+".jpg";
        msg.setForeign_picture_path(fPath);
        double[] fScale=new double[1];
        fScale[0]=1;
        msg.setForeign_scale(fScale);
        double[] fPosibility=new double[1];
        fPosibility[0]=0.998;
        msg.setForeign_posibility(fPosibility);
        msg.setForeign_result("success");
        return msg;
    }



    }


    @Data
    class FindResultValues {
        private String fileName;
        private String infraredFileName;
        private Integer[]     roi_vertex;//预选框顶点
        private Integer[]     roi_vertex_thermal;//预选框顶点
        private String[] foreignFileNames;
        private int foreign_detect;
    }
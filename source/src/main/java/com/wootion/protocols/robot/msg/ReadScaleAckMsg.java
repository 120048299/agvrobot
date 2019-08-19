package com.wootion.protocols.robot.msg;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wootion.task.CONTROL_CMD;

import java.math.BigDecimal;
import java.util.Arrays;

public class ReadScaleAckMsg implements RosMsg {
    protected Header header;

    protected Integer trans_id;
    protected String robot_ip;
    private String result;
    private double posibility[];
    private String picture_path[];
    private double scale[];

    private String foreign_result;
    private double foreign_posibility[];
    private String foreign_picture_path[];
    private double foreign_scale[];

    private static final Logger logger = LoggerFactory.getLogger(ReadScaleAckMsg.class.toString());

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Integer getTrans_id() {
        return trans_id;
    }

    public void setTrans_id(Integer trans_id) {
        this.trans_id = trans_id;
    }

    public void setHeader(JSONObject header) {
        if (header == null || header.size() == 0) {
            //logger.info("header is null");
            return;
        }
        if (this.header == null) {
            this.header = new Header();
        }

//        this.header.setFrame_id((String) header.get("frame_id"));
//        this.header.setSeq(header.getInt("seq"));
//        this.header.setStamp(header.getJSONObject("stamp"));
        this.header.setFrame_id((String) header.get("frame_id"));
        this.header.setSeq(header.getIntValue("seq"));
        this.header.setStamp(header.getJSONObject("stamp"));
    }



    public String getRobot_ip() {
		return robot_ip;
	}

    public void setRobot_ip(String robot_ip) {
        this.robot_ip = robot_ip;
    }

    public double[] getPosibility() {
        return posibility;
    }
    public void setPosibility(double posibility[]) {
        this.posibility = posibility;
    }
    public void setPosibility(JSONArray posibility) {
        if (null == posibility || posibility.size() == 0) {
            return ;
        }
        int size = posibility.size();
        this.posibility =  new double[size];
        for (int i=0;i<size;i++){
            this.posibility[i] = ((BigDecimal) posibility.get(i)).floatValue();
        }
    }

    public String[] getPicture_path() {
        return picture_path;
    }

    public void setPicture_path(String[] picture_path) {
        this.picture_path = picture_path;
    }

    public void setPicture_path(JSONArray picture_path) {
        if (null == picture_path || picture_path.size() == 0) {
            return ;
        }
        int size = picture_path.size();
        this.picture_path =  new String[size];
        for (int i=0;i<size;i++){
            this.picture_path[i] = (String) picture_path.get(i);
        }
    }

    public double[] getScale() {
        return scale;
    }

    public void setScale(double[] scale) {
        this.scale = scale;
    }

    public void setScale(JSONArray scale) {
        if (null == scale || scale.size() == 0) {
            return ;
        }
        int size = scale.size();
        this.scale =  new double[size];
        for (int i=0;i<size;i++){
            this.scale[i] = ((BigDecimal) scale.get(i)).floatValue();
        }
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }


    public Short ctrlMode() {
        return 0;
    }


    public Short cmdMode() {
        return 0;
    }

    public static void main(String[] args) {
        ReadScaleAckMsg msg = new ReadScaleAckMsg();
        msg.setHeader(new Header());
        msg.setRobot_ip("192.168.1.222");
        double scale[]=new double[2];
        scale[0]=1.23;
        scale[1]=5;
        double posibility[]=new double[2];
        posibility[0]=90;
        posibility[1]=80;
        msg.setTrans_id(123);

        String picture_path[]=new String[2];
        picture_path[0]="path1";
        picture_path[1]="path2";

        msg.setPicture_path(picture_path);
        msg.setScale(scale);
        msg.setPosibility(posibility);
        System.out.println(JSONObject.toJSON(msg));
    }

    public String getForeign_result() {
        return foreign_result;
    }

    public void setForeign_result(String foreign_result) {
        this.foreign_result = foreign_result;
    }

    public double[] getForeign_posibility() {
        return foreign_posibility;
    }

    public void setForeign_posibility(double[] foreign_posibility) {
        this.foreign_posibility = foreign_posibility;
    }
    public void setForeign_posibility(JSONArray foreign_posibility) {
        if (null == foreign_posibility || foreign_posibility.size() == 0) {
            return ;
        }
        int size = foreign_posibility.size();
        this.foreign_posibility =  new double[size];
        for (int i=0;i<size;i++){
            this.foreign_posibility[i] = ((BigDecimal) foreign_posibility.get(i)).floatValue();
        }
    }


    public String[] getForeign_picture_path() {
        return foreign_picture_path;
    }

    public void setForeign_picture_path(String[] foreign_picture_path) {
        this.foreign_picture_path = foreign_picture_path;
    }

    public void setForeign_picture_path(JSONArray foreign_picture_path) {
        if (null == foreign_picture_path || foreign_picture_path.size() == 0) {
            return ;
        }
        int size = foreign_picture_path.size();
        this.foreign_picture_path =  new String[size];
        for (int i=0;i<size;i++){
            this.foreign_picture_path[i] = (String) foreign_picture_path.get(i);
        }
    }


    public double[] getForeign_scale() {
        return foreign_scale;
    }

    public void setForeign_scale(double[] foreign_scale) {
        this.foreign_scale = foreign_scale;
    }

    public void setForeign_scale(JSONArray foreign_scale) {
        if (null == foreign_scale || foreign_scale.size() == 0) {
            return ;
        }
        int size = foreign_scale.size();
        this.foreign_scale =  new double[size];
        for (int i=0;i<size;i++){
            this.foreign_scale[i] = ((BigDecimal) foreign_scale.get(i)).floatValue();
        }
    }



    @Override
    public String toString() {
        return "ReadScaleAckMsg{" +
                "header=" + header +
                ", trans_id=" + trans_id +
                ", robot_ip='" + robot_ip + '\'' +
                ", result='" + result + '\'' +
                ", posibility=" + Arrays.toString(posibility) +
                ", picture_path=" + Arrays.toString(picture_path) +
                ", scale=" + Arrays.toString(scale) +
                ", foreign_result='" + foreign_result + '\'' +
                ", foreign_posibility=" + Arrays.toString(foreign_posibility) +
                ", foreign_picture_path=" + Arrays.toString(foreign_picture_path) +
                ", foreign_scale=" + Arrays.toString(foreign_scale) +
                '}';
    }
}

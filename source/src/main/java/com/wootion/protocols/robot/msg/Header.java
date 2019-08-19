package com.wootion.protocols.robot.msg;


import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Header {
    protected long seq;
    protected TimePrimitive stamp;
    protected String frame_id = "123456";
    private static final Logger logger = LoggerFactory.getLogger(Header.class.toString());

    public Header(Long systemMillis) {
        this.setStamp(systemMillis);
    }

    public Header() {
        this.setStamp(System.currentTimeMillis());
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }

    public TimePrimitive getStamp() {
        return stamp;
    }

    public void setStamp(TimePrimitive stamp) {
        this.stamp = stamp;
    }

    public void setStamp(Long systemMillis) {
        if (this.stamp == null) {
            this.stamp = new TimePrimitive(systemMillis);
            return ;
        }
        this.stamp.loadSystemMillis(systemMillis);
    }

    public String getFrame_id() {
        return frame_id;
    }

    public void setFrame_id(String frame_id) {
        this.frame_id = frame_id;
    }

    public void setStamp(JSONObject stamp) {
        if (null == stamp) {
            logger.info("stamp is null");
            return ;
        }
        this.stamp.setSecs(stamp.getIntValue("secs"));
        this.stamp.setNsecs(stamp.getIntValue("nsecs"));
    }
}

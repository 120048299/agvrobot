package com.wootion.protocols.robot.msg;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class BaseMsg {
    protected Header  header;
    public void setHeader(JSONObject header) {
        if (header == null || header.size() == 0) {
            return ;
        }
        if (this.header == null) {
            this.header = new Header();
        }

        this.header.setFrame_id((String) header.get("frame_id"));
        this.header.setSeq(header.getIntValue("seq"));
        this.header.setStamp(header.getJSONObject("stamp"));
    }
    protected Integer trans_id;
}

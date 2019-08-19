package com.wootion.protocols.robot.msg;

/**
 * 录制巡检点时 旋转云台命令
 */
public class PresetRotateCommandMsg implements RosMsg{
    protected Header  header;
    protected String  sender = MsgNames.node_server;
    protected String  receiver = MsgNames.node_preset_rotate;
    protected Integer trans_id;
    protected Integer delta_x;//水平向右为正
    protected Integer delta_y;//向上为正
    protected Integer width;
    protected Integer height;

    public PresetRotateCommandMsg(){

    }

    public PresetRotateCommandMsg(Integer trans_id, Integer delta_x, Integer  delta_y,Integer width, Integer  height) {
        this.trans_id = trans_id;
        this.delta_x = delta_x;
        this.delta_y =  delta_y;
        this.width = width;
        this.height= height;
    }

    public Header getHeader() {
        return header;
    }
    public void setHeader(Header header) {
        this.header = header;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setTrans_id(Integer trans_id) {
        this.trans_id = trans_id;
    }

    @Override
    public Integer getTrans_id() {
        return this.trans_id;
    }

    public Short ctrlMode() {
        return null;
    }

    public Short cmdMode() {
        return null;
    }

    public Integer getDelta_x() {
        return delta_x;
    }

    public void setDelta_x(Integer delta_x) {
        this.delta_x = delta_x;
    }

    public Integer getDelta_y() {
        return delta_y;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public void setDelta_y(Integer delta_y) {
        this.delta_y = delta_y;
    }

    public String content() {
        return "TerraceCommandMsg{" +
                "header=" + header +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", trans_id=" + trans_id +
                ", delta_x='" + delta_x + '\'' +
                ", delta_y='" + delta_y + '\'' +
                ", width='" + width + '\'' +
                ", height='" + height + '\'' +
                '}';
    }
}

package com.wootion.protocols.robot.msg;


import com.wootion.commons.Constans;

public class PresetScaleCommandMsg implements RosMsg {
    protected String  sender = MsgNames.node_server;
    protected String  receiver = "preset_scale";
    private Integer trans_id;
    private String preset_id; //巡检点id
    private String type; //识别对象类型
    private short infrared;


    public PresetScaleCommandMsg(Integer trans_id, String preset_id, String type,short infrared) {
        this.trans_id = trans_id;
        this.preset_id = preset_id;
        this.type =  type;
        this.infrared=infrared;
    }

    public PresetScaleCommandMsg() {
    }

    public String getPreset_id() {
        return preset_id;
    }

    public void setPresetId(String preset_id) {
        this.preset_id = preset_id;
    }

    @Override
    public Integer getTrans_id() {
        return this.trans_id;
    }

    @Override
    public void setTrans_id(Integer transId) {
        this.trans_id = transId;
    }


    public Short ctrlMode() {
        return 0;
    }

    public Short cmdMode() {
        return null;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String content() {
        return toString();
    }

    public short getInfrared() {
        return infrared;
    }

    public void setInfrared(short infrared) {
        this.infrared = infrared;
    }

    @Override
    public String toString() {
        return "PresetScaleCommandMsg{" +
                "sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", trans_id=" + trans_id +
                ", preset_id='" + preset_id + '\'' +
                ", type='" + type + '\'' +
                ", infrared=" + infrared +
                '}';
    }
}

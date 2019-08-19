package com.wootion.protocols.robot.msg;

import com.wootion.commons.Constans;
import com.wootion.commons.MSG_TYPE;

import java.util.Arrays;

/**
 * todo
 */
public class RobotCommandMsg implements RosMsg {
    protected final Header header;
    protected String  sender = MsgNames.node_server;
    protected String  receiver = MsgNames.node_cmd_interface;
    protected Integer trans_id = -1;


    protected final Short control_mode;
    protected final Short cmd_mode;

    protected final Float velocity_x;
    protected final Float velocity_yaw;
    protected final Float[] position;
    protected final Float orientation;
    protected String directory; //巡检点路径 ,只有找表的移动时有用，提前旋转云台


    public RobotCommandMsg(Header header, Integer trans_id, Short control_mode, Short cmd_mode, String robot_ip,
                           Float velocity_x, Float velocity_yaw, Float[] position, Float orientation,String directory) {
        this.header = header;
        this.trans_id = trans_id;
        this.control_mode = control_mode;
        this.cmd_mode = cmd_mode;

        this.velocity_x = velocity_x;
        this.velocity_yaw = velocity_yaw;
        this.position = position;
        this.orientation = orientation;
        this.directory = directory;
    }

    public Header getHeader() {
        return header;
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

    public Short getControl_mode() {
        return control_mode;
    }

    public Short getCmd_mode() {
        return cmd_mode;
    }


    public Float getVelocity_x() {
        return velocity_x;
    }

    public Float getVelocity_yaw() {
        return velocity_yaw;
    }

    public Float[] getPosition() {
        return position;
    }

    public Float getOrientation() {
        return orientation;
    }


	@Override
	public void setTrans_id(Integer transId) {
		this.trans_id = transId;
    }
    
    @Override
    public Integer getTrans_id() {
        if (trans_id == null) {
            return -1;
        }
        return trans_id;
    }

    public Short ctrlMode() {
        if (getControl_mode() == null) {
            return 0;
        }
        return getControl_mode();
    }

    public Short cmdMode() {
        if (getCmd_mode() == null) {
            return 0;
        }
        return getCmd_mode();
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    /*@Override
    public int responseCode() {
        if (this.velocity_x > 0.0f) {
            return MSG_TYPE.MT_CONTROL_FORWARD.getValue() + Constans.MSG_TYPE_RESP;
        }
        if (this.velocity_x < 0.0f) {
            return MSG_TYPE.MT_CONTROL_BACKWARD.getValue() + Constans.MSG_TYPE_RESP;
        }
        if (this.velocity_yaw > 0.0f) {
            return MSG_TYPE.MT_CONTROL_LEFT.getValue() + Constans.MSG_TYPE_RESP;
        }
        if (this.velocity_yaw < 0.0f) {
            return MSG_TYPE.MT_CONTROL_RIGHT.getValue() + Constans.MSG_TYPE_RESP;
        }
        return MSG_TYPE.MT_CONTROL_STOP.getValue() + Constans.MSG_TYPE_RESP;
    }
*/

}

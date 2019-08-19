package com.wootion.protocols.robot.msg;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wootion.commons.Constans;
import com.wootion.protocols.robot.operation.ReadScaleOp;
import com.wootion.task.CONTROL_CMD;

import java.util.Arrays;


public class ReadScaleCommandMsg implements RosMsg {
	protected String  sender = MsgNames.node_server;
	protected String  receiver = "read_scale";
	protected Integer trans_id;
	private String directory;
	private Integer foreign_detect =0;//0 只读表,1 只读异物,2同时读
	private String[] filename; // 图片文件名	找表完成后保存的图片名
	private String[] foreign_filename; // 异物
	protected Integer[]     roi_vertex; //预选框顶点
	protected Integer[]     roi_vertex_thermal;//预选框顶点

	public ReadScaleCommandMsg(Integer trans_id, String directory) {
		this.trans_id = trans_id;
		this.directory = directory;

	}

	public ReadScaleCommandMsg() {
	}


	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public String[] getFilename() {
		return filename;
	}

	public void setFilename(String[] filename) {
		this.filename = filename;
	}

	public void setFilename(JSONArray filename) {
		if (null == filename || filename.size() == 0) {
			return ;
		}
		int size = filename.size();
		this.filename =  new String[size];
		for (int i=0;i<size;i++){
			this.filename[i] = (String) filename.get(i);
		}
	}

	@Override
	public Integer getTrans_id() {
		return this.trans_id;
	}

	public Short ctrlMode() {
		return 0;
	}

	public Short cmdMode() {
		return 0;
	}

	public static void main(String[] args) {

	}

	public String content() {
		return toString();
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

	public Integer[] getRoi_vertex() {
		return roi_vertex;
	}

	public void setRoi_vertex(Integer[] roi_vertex) {
		this.roi_vertex = roi_vertex;
	}

	public Integer[] getRoi_vertex_thermal() {
		return roi_vertex_thermal;
	}

	public void setRoi_vertex_thermal(Integer[] roi_vertex_thermal) {
		this.roi_vertex_thermal = roi_vertex_thermal;
	}

	public Integer getForeign_detect() {
		return foreign_detect;
	}

	public void setForeign_detect(Integer foreign_detect) {
		this.foreign_detect = foreign_detect;
	}

	public String[] getForeign_filename() {
		return foreign_filename;
	}

	public void setForeign_filename(String[] foreign_filename) {
		this.foreign_filename = foreign_filename;
	}

	public void setForeign_filename(JSONArray foreign_filename) {
		if (null == foreign_filename || foreign_filename.size() == 0) {
			return ;
		}
		int size = foreign_filename.size();
		this.foreign_filename =  new String[size];
		for (int i=0;i<size;i++){
			this.foreign_filename[i] = (String) foreign_filename.get(i);
		}
	}

	@Override
	public String toString() {
		return "ReadScaleCommandMsg{" +
				"sender='" + sender + '\'' +
				", receiver='" + receiver + '\'' +
				", trans_id=" + trans_id +
				", directory='" + directory + '\'' +
				", foreign_detect=" + foreign_detect +
				", filename=" + Arrays.toString(filename) +
				", foreign_filename=" + Arrays.toString(foreign_filename) +
				", roi_vertex=" + Arrays.toString(roi_vertex) +
				", roi_vertex_thermal=" + Arrays.toString(roi_vertex_thermal) +
				'}';
	}
}

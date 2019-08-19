package com.wootion.agvrobot.session;

import com.wootion.model.Robot;

import java.io.Serializable;

public class SessionRobot implements Serializable {

	private static final long serialVersionUID = -1072819755781866295L;
	
	private String uid;
	private String userId;
	private Robot robot;
	private String siteId;
	

	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Robot getRobot() {
		return robot;
	}

	public void setRobot(Robot robot) {
		this.robot = robot;
	}

	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

}

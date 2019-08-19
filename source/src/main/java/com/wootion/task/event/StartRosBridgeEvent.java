package com.wootion.task.event;

import com.wootion.robot.MemRobot;

public class StartRosBridgeEvent {
	private MemRobot memRobot;
	public MemRobot getMemRobot() {
		return memRobot;
	}

	public void setMemRobot(MemRobot memRobot) {
		this.memRobot = memRobot;
	}

	public StartRosBridgeEvent(MemRobot memRobot) {
		this.memRobot = memRobot;
	}

}

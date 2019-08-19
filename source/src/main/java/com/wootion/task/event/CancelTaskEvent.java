package com.wootion.task.event;

import com.wootion.robot.MemRobot;

public class CancelTaskEvent {
    private MemRobot memRobot;

 	int reason=3;//3 cancel,7 end

    public CancelTaskEvent(MemRobot memRobot) {
        this.memRobot = memRobot;
    }
	public CancelTaskEvent(MemRobot memRobot,int reason) {
		this.memRobot = memRobot;
		this.reason = reason;
	}

	/**
	 * @return the memRobot
	 */
	public MemRobot getMemRobot() {
		return memRobot;
	}

	/**
	 * @param memRobot the memRobot to set
	 */
	public void setMemRobot(MemRobot memRobot) {
		this.memRobot = memRobot;
	}

	public int getReason() {
		return reason;
	}

	public void setReason(int reason) {
		this.reason = reason;
	}
}

package com.wootion.protocols.robot.msg;

public class TimePrimitive {
    public int secs;  // when requesting this format from ROSbridge, it uses 'sec' (no 's')
    public int nsecs; // when requesting this format from ROSbridge, it uses 'nsec'

    public TimePrimitive(Long systemMillis) {
        this.loadSystemMillis(systemMillis);
    }

    public int getSecs() {
        return secs;
    }

    public void setSecs(int secs) {
        this.secs = secs;
    }

    public int getNsecs() {
        return nsecs;
    }

    public void setNsecs(int nsecs) {
        this.nsecs = nsecs;
    }

    public void loadSystemMillis(Long systemMillis) {
        this.secs = (int)Math.floorDiv(systemMillis, 1000);
        this.nsecs = (int) (Math.floorMod(systemMillis, this.secs) * 1000000);
    }
}

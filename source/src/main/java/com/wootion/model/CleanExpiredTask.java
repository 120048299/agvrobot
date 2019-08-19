package com.wootion.model;

import java.util.Date;

public class CleanExpiredTask {
    private String uid;

    private Date planCleanTasktime;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid == null ? null : uid.trim();
    }

    public Date getPlanCleanTasktime() {
        return planCleanTasktime;
    }

    public void setPlanCleanTasktime(Date planCleanTasktime) {
        this.planCleanTasktime = planCleanTasktime;
    }
}
package com.wootion.model;

import lombok.Data;

import java.util.Date;

@Data
public class UserInfo {
    private String uid;
    private String username;
    private String deptid;
    private String roleid;
    private String workcord;
    private String password;
    private String loginname;
    private String faxno;
    private String flagsex;
    private String emailno;
    private String officialno;
    private String msisdn;
    private String position;
    private String address;
    private Date modifytime;
    private Integer priority;
    private String reserv2;
    private Integer reserv1;
    private Integer status;
    private String rolename;
    private String deptName;
}
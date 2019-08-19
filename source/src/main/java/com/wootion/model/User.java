package com.wootion.model;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import java.util.Date;

public class User {
    private String uid;

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "工号不能为空")
    private String workcord;

    @NotBlank(message = "密码不能为空")
    @Length(min = 6,max = 16, message = "密码长度必须为6-16")
    private String password;

    @NotBlank(message = "登录名不能为空")
    private String loginname;


    private String faxno;
    @NotBlank(message = "性别不能为空")
    private String flagsex;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱不符合规则")
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid == null ? null : uid.trim();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getWorkcord() {
        return workcord;
    }

    public void setWorkcord(String workcord) {
        this.workcord = workcord == null ? null : workcord.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getLoginname() {
        return loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname == null ? null : loginname.trim();
    }

    public String getFaxno() {
        return faxno;
    }

    public void setFaxno(String faxno) {
        this.faxno = faxno == null ? null : faxno.trim();
    }

    public String getFlagsex() {
        return flagsex;
    }

    public void setFlagsex(String flagsex) {
        this.flagsex = flagsex == null ? null : flagsex.trim();
    }

    public String getEmailno() {
        return emailno;
    }

    public void setEmailno(String emailno) {
        this.emailno = emailno == null ? null : emailno.trim();
    }

    public String getOfficialno() {
        return officialno;
    }

    public void setOfficialno(String officialno) {
        this.officialno = officialno == null ? null : officialno.trim();
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn == null ? null : msisdn.trim();
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position == null ? null : position.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public Date getModifytime() {
        return modifytime;
    }

    public void setModifytime(Date modifytime) {
        this.modifytime = modifytime;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getReserv2() {
        return reserv2;
    }

    public void setReserv2(String reserv2) {
        this.reserv2 = reserv2 == null ? null : reserv2.trim();
    }

    public Integer getReserv1() {
        return reserv1;
    }

    public void setReserv1(Integer reserv1) {
        this.reserv1 = reserv1;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
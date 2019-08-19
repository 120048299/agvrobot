package com.wootion.bo;

import com.wootion.model.Role;
import com.wootion.model.User;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import java.util.Date;
import java.util.List;

public class UserRoles {

    private String uid;

    @NotBlank(message = "用户名不能为空")
    private String username;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getWorkcord() {
        return workcord;
    }

    public void setWorkcord(String workcord) {
        this.workcord = workcord;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLoginname() {
        return loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }

    public String getFaxno() {
        return faxno;
    }

    public void setFaxno(String faxno) {
        this.faxno = faxno;
    }

    public String getFlagsex() {
        return flagsex;
    }

    public void setFlagsex(String flagsex) {
        this.flagsex = flagsex;
    }

    public String getEmailno() {
        return emailno;
    }

    public void setEmailno(String emailno) {
        this.emailno = emailno;
    }

    public String getOfficialno() {
        return officialno;
    }

    public void setOfficialno(String officialno) {
        this.officialno = officialno;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
        this.reserv2 = reserv2;
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

    private List<Role> roles;

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}

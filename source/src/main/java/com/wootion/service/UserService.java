package com.wootion.service;

import com.github.pagehelper.PageInfo;
import com.wootion.agvrobot.dto.UserBean;
import com.wootion.model.UserInfo;
import com.wootion.vo.UserRoles;
import com.wootion.model.Role;
import com.wootion.model.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserService {

    UserBean getUserBean(String loginname);

}

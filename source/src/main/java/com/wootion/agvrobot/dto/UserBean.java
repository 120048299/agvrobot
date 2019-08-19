package com.wootion.agvrobot.dto;

import com.wootion.agvrobot.utils.CommonTree;
import com.wootion.model.Resource;
import com.wootion.model.UserInfo;
import java.util.List;
import lombok.Data;

@Data
public class UserBean {
    private String loginname;
    private String password;
    private UserInfo userInfo;
    private List<String> roles;
    private List<String> permissions;
    private CommonTree<Resource> menu;
}

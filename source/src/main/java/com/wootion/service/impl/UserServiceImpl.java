package com.wootion.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wootion.agvrobot.dto.UserBean;
import com.wootion.agvrobot.utils.CommonTree;
import com.wootion.dao.IDao;
import com.wootion.exceptions.UserException;
import com.wootion.mapper.RoleMapper;
import com.wootion.mapper.UserInfoMapper;
import com.wootion.model.Resource;
import com.wootion.model.Role;
import com.wootion.model.UserInfo;
import com.wootion.service.ResourceService;
import com.wootion.service.UserService;
import com.wootion.vo.UserRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    IDao iDao;

    @Autowired
    private ResourceService resourceService;
    @Autowired
    RoleMapper roleMapper;
    @Autowired
    UserInfoMapper userInfoMapper;



    @Override
    public UserBean getUserBean(String loginname) {
        UserBean userBean = new UserBean();
        UserInfo userInfo = userInfoMapper.getUserByLoginname(loginname);
        if (userInfo != null) {
            userBean.setLoginname(userInfo.getLoginname());
            userBean.setPassword(userInfo.getPassword());
            userBean.setUserInfo(userInfo);
        } else {
            return null;
        }

        List<Role> roleList = roleMapper.selectRoleByUserId(userInfo.getUid());
        List<String> roleNames = new ArrayList<>();
        for (Role role : roleList) {
            roleNames.add(role.getRole());
        }
        Set<String> permissions = resourceService.findPermissionsByRole(new HashSet<>(roleList));
        userBean.setPermissions(new ArrayList<>(permissions));
        userBean.setRoles(roleNames);
        List<Resource> resourceList = resourceService.findMenus(permissions);

        //菜单
        Resource r=new Resource();
        r.setUid("root");
        r.setName("系统导航");
        CommonTree<Resource> menu= new CommonTree<>(r);
        menu.addList(resourceList);
        /*Menu menu= new Menu(r);
        menu.addMenu(resourceList);
        userBean.setMenu(menu);
        */
        userBean.setMenu(menu);
        return userBean;
    }


}

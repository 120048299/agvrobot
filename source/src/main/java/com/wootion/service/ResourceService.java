package com.wootion.service;

import com.github.pagehelper.PageInfo;
import com.wootion.model.Resource;
import com.wootion.model.Role;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ResourceService {

    /**
     * 根据用户权限得到菜单
     * @param permissions
     * @return
     */
    List<Resource> findMenus(Set<String> permissions);

    /**
     *
     * @param roles
     * @return
     */
    Set<String> findPermissionsByRole(Set<Role> roles);

    List<Resource> selectAll();
    List<Resource> selectResource(String roleId);
}

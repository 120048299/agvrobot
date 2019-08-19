package com.wootion.service.impl;

import com.wootion.dao.IDao;
import com.wootion.mapper.ResourceMapper;
import com.wootion.model.Resource;
import com.wootion.model.Role;
import com.wootion.service.ResourceService;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("resourceService")
public class ResourceServiceImpl implements ResourceService {
    @Autowired
    IDao dao;
    @Autowired
    ResourceMapper resourceMapper;


    @Override
    public List<Resource> findMenus(Set<String> permissions) {
        List<Resource> allResources = selectAll();
        List<Resource> menus = new ArrayList<Resource>();
        for(Resource resource : allResources) {
            if(resource.getParentId() == null) {
                continue;
            }
            if(resource.getType()!=0) { //0 菜单
                continue;
            }
            if(!hasPermission(permissions, resource)) {
                continue;
            }
            menus.add(resource);
        }
        return menus;
    }
    private boolean hasPermission(Set<String> permissions, Resource resource) {
        if(StringUtils.isEmpty(resource.getPermission())) {
            return true;
        }
        for(String permission : permissions) {
            WildcardPermission p1 = new WildcardPermission(permission);
            WildcardPermission p2 = new WildcardPermission(resource.getPermission());
            if(p1.implies(p2) || p2.implies(p1)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param roles
     * @return
     */
    @Override
    public Set<String> findPermissionsByRole(Set<Role> roles) {
        Set<String> permissions = new HashSet<>();
        for (Role role : roles) {
            List<Resource> list=resourceMapper.selectResourceByRoleId(role.getUid());
            if (list != null) {
                for(Resource res:list) {
                    permissions.add(res.getPermission());
                }
            }
        }
        return permissions;
    }

    @Override
    public List<Resource> selectAll() {
        return  resourceMapper.selectAll();
    }

    @Override
    public List<Resource> selectResource(String roleId) {
        return   resourceMapper.selectResourceByRoleId(roleId);
    }
}

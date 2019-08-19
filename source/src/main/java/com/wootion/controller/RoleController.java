package com.wootion.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wootion.commons.Result;
import com.wootion.mapper.RoleMapper;
import com.wootion.mapper.RoleResourceMapper;
import com.wootion.model.Role;
import com.wootion.model.RoleResource;
import com.wootion.utiles.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @Author: ZhangJiqiang
 * @Description:
 * @Date: Created in 下午4:55 18-5-2
 * @Modified By:
 */
@RequestMapping("/req_svr/roles")
@RestController
public class RoleController {

    @Autowired
    RoleMapper roleMapper;
    @Autowired
    RoleResourceMapper roleResourceMapper;
    /**
     * 保存角色和其资源列表
     * @param params
     * @return
     */
    @ResponseBody
    @PostMapping("save")
    public Result addOrUpdateRole(@RequestBody Map params) {
        Role role = new Role ();
        if(params.get("uid")!=null){
            role.setUid((String)params.get("uid"));
        }
        role.setRole((String)params.get("role"));
        role.setDescription((String)params.get("description"));
        if (StringUtils.isEmpty(role.getUid())){
            roleMapper.insert(role);
        }else {
            roleMapper.update(role);
        }
        List<String> resIds =(List<String>)params.get("resourceIds");
        roleResourceMapper.deleteByRoleId(role.getUid());
        for (String newResourceId : resIds) {
            RoleResource roleResource = new RoleResource();
            roleResource.setResourceId(newResourceId);
            roleResource.setRoleId(role.getUid());
            roleResourceMapper.insert(roleResource);
        }
        return ResultUtil.success(role.getUid());
    }

    /**
     *
     * @param params 名称，页，页大小
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("selectRoleList")
    public PageInfo selectRoleList(@RequestBody Map params, HttpServletRequest request){
        int pageNum=0;
        if(params.get("pageNum")!=null)
        {
            pageNum= (int) params.get("pageNum");
        }
        int pageSize=10;
        if(params.get("pageSize")!=null)
        {
            pageSize= (int) params.get("pageSize");
        }
        String roleName=(String)params.get("roleName");
        PageHelper.startPage(pageNum, pageSize);
        List<Role> list = roleMapper.selectRole(roleName);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    @ResponseBody
    @GetMapping("getAllRoles")
    public List<Role> getAllRoles(){
        List<Role>  list = roleMapper.selectAll();
        return list;
    }

    @ResponseBody
    @GetMapping("deleteRoles/{roleIds}")
    public Result<String> deleteRoles(@PathVariable List<String> roleIds){
        int count = 0;
        for (String roleId :roleIds) {

            count += roleMapper.delete(roleId);
        }
        if(count<roleIds.size()){
            return ResultUtil.build(-1,"删除了"+count+"个角色。没有删除的角色在使用中，请先删除角色和用户，资源的引用关系。",null);
        }
        return ResultUtil.success();
    }



}

package com.wootion.controller;

import com.wootion.agvrobot.utils.CommonTree;
import com.wootion.agvrobot.utils.UUIDUtil;
import com.wootion.commons.Result;

import com.wootion.exceptions.UserException;
import com.wootion.mapper.DeptMapper;

import com.wootion.mapper.UserInfoMapper;
import com.wootion.model.Dept;

import com.wootion.model.Role;
import com.wootion.model.User;
import com.wootion.model.UserInfo;
import com.wootion.utiles.DataCache;
import com.wootion.utiles.ResultUtil;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/req_svr/dept")
public class DeptController {
    Logger logger = LoggerFactory.getLogger(DeptController.class);

    @Autowired
    DeptMapper deptMapper;

    @Autowired
    UserInfoMapper userInfoMapper;

    @ResponseBody
    @PostMapping("/saveDept")
    public Result saveDept (@RequestBody Map params,HttpServletRequest request){

        String name=(String) params.get("name");
        String parentId = (String) params.get("parentId");
        String uid = (String) params.get("uid");//如果有uid则是修改
        String code=(String) params.get("code");
        int level = (Integer) params.get("level");
        if(uid!=null && !"".equals(uid)){
            //update
            Dept dept = new Dept();

            if (code!=null && !"".equals(code)) {
                dept.setCode(code);
            }

            dept.setUid(uid);
            dept.setName(name);
            dept.setParentId(parentId);
            dept.setLevel(level);
           // int ret= this.deptService.updateDept(dept);
            int ret = deptMapper.update(dept);
            if (ret==1) {
                DataCache.reload();
                return ResultUtil.build(0,"修改成功",null);
            } else {
                return ResultUtil.failed("修改失败");
            }
        }

        Dept saveDept = new Dept();

        if (code!=null && !"".equals(code)) {
            saveDept.setCode(code);
        }
        saveDept.setName(name);
        saveDept.setParentId(parentId);
        saveDept.setUid(UUIDUtil.getUUID());
        saveDept.setLevel(level);

       // Dept dept = deptService.addDept(saveDept);
        int ret1 = deptMapper.insert(saveDept);
        if (ret1 == 1) {
            DataCache.reload();
            return ResultUtil.build(0,"保存成功",saveDept);
        } else {
            return ResultUtil.failed("保存失败");
        }
    }

    @ResponseBody
    @PostMapping("/saveUser")
    public Result saveUser (@RequestBody Map params,HttpServletRequest request){

        String uid = (String) params.get("uid");//如果有uid则是修改
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername((String)params.get("username"));
        userInfo.setDeptid((String)params.get("deptid"));
        userInfo.setRoleid((String)params.get("roleid"));
        userInfo.setWorkcord((String)params.get("workcord"));
        userInfo.setPassword((String)params.get("password"));
        userInfo.setLoginname((String)params.get("loginname"));
        userInfo.setFaxno((String)params.get("faxno"));
        userInfo.setFlagsex((String)params.get("flagsex"));
        userInfo.setEmailno((String)params.get("emailno"));
        userInfo.setOfficialno((String)params.get("officialno"));
        userInfo.setMsisdn((String)params.get("msisdn"));
        userInfo.setPosition((String)params.get("position"));
        userInfo.setAddress((String)params.get("address"));
        userInfo.setModifytime(new Date());


        List<UserInfo> userList = userInfoMapper.selectAll();
        for (UserInfo item : userList) {
            if (item.getLoginname().equals(userInfo.getLoginname())) {
                return ResultUtil.failed("保存失败,用户名不能重复");
            } else if (item.getUsername().equals(userInfo.getUsername())) {
                return ResultUtil.failed("保存失败,登录名不能重复");
            } else if (item.getWorkcord().equals(userInfo.getWorkcord())) {
                return ResultUtil.failed("保存失败,工号不能重复");
            }
        }
        if (!"男".equals(userInfo.getFlagsex())&& !"女".equals(userInfo.getFlagsex())) {
            return ResultUtil.failed("保存失败,性别必须是男或女");
        }
        if(uid!=null && !"".equals(uid)){
            userInfo.setUid(uid);
            // int ret= this.deptService.updateDept(dept);
            int ret = userInfoMapper.update(userInfo);
            if (ret==1) {
                DataCache.reload();
                return ResultUtil.build(0,"修改成功",null);
            } else {
                return ResultUtil.failed("修改失败");
            }
        }
        userInfo.setUid(UUIDUtil.getUUID());
        int ret1 = userInfoMapper.insert(userInfo);
        if (ret1 == 1) {
            DataCache.reload();
            return ResultUtil.build(0,"保存成功",userInfo);
        } else {
            return ResultUtil.failed("保存失败");
        }
    }

    @ApiOperation(value = "部门组织树",notes = "完整的或者部分 params:devName 部门名称" )
    @ResponseBody
    @PostMapping("/getDeptTree")
    public CommonTree<Dept> getDeptTree(@RequestBody Map params,HttpServletRequest request){
        Dept rootdept = new Dept();
        rootdept = deptMapper.findCompany();
        CommonTree<Dept> deptTree= new CommonTree<>(rootdept);
        List<Dept> deptList = deptMapper.findAll();
        deptTree.addList(deptList);
        return deptTree;
    }

    /**
     *
     * @param
     * @return
     */
    @ResponseBody
    @GetMapping("/deleteDept/{id}")
    public Result deleteDev(@PathVariable String id){
        deptMapper.delete(id);
        DataCache.reload();
        return ResultUtil.build(0,"删除成功",null);
    }



    @ApiOperation(value = "部门员工",notes = "完整的或者部分 params:uid 部门ID" )
    @ResponseBody
    @PostMapping("/getUserInfoByDept/{uid}")
    public List<UserInfo> getUserInfoByDept(@PathVariable String uid, HttpServletRequest request){
        List<UserInfo> userInfos = userInfoMapper.findUserByDeptId(uid);
        return userInfos;
    }

    @ApiOperation(value = "子部门",notes = " params:uid 部门ID" )
    @ResponseBody
    @PostMapping("/getSubDept/{uid}")
    public List<Dept> getSubDept(@PathVariable String uid, HttpServletRequest request){
        List<Dept> dept = deptMapper.getSubDept(uid);
        return dept;
    }

    @ResponseBody
    @GetMapping("/delUser/{id}")
    public Result delUser(@PathVariable String id){
        userInfoMapper.deleteUserInfo(id);
        DataCache.reload();
        return ResultUtil.build(0,"删除成功",null);
    }

    @ApiOperation(value = "权限",notes = "完整的权限" )
    @ResponseBody
    @PostMapping("/getRoleList")
    public List<Role> getUserInfoByDept(HttpServletRequest request){
        List<Role> roleList = userInfoMapper.getAllRole( );
        return roleList;
    }
}


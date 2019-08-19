package com.wootion.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itextpdf.kernel.geom.PageSize;
import com.wootion.mapper.UserInfoMapper;
import com.wootion.model.UserInfo;
import com.wootion.vo.UserRoles;
import com.wootion.commons.Result;
import com.wootion.model.User;
import com.wootion.service.UserService;
import com.wootion.utiles.ResultUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/req_svr/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    UserInfoMapper userInfoMapper;
    @ResponseBody
    @PostMapping("/getUserList")
    public PageInfo getUserList(@RequestBody Map params){
        String condition=(String)params.get("condition");
        int pageNum=(Integer)params.get("pageNum");
        int pageSize=(Integer)params.get("pageSize");
        PageHelper.startPage(pageNum, pageSize);
        List<UserInfo> list=userInfoMapper.queryByText(condition);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

   /* *//**
     * 根据用户ID 查找用户信息
     * @param userId
     * @return
     *//*
    @ApiOperation(value = "查询用户单个用户",notes = "根据用户Id查询用户信息")
    @ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "String", paramType = "path")
    @ResponseBody
    @GetMapping("/{userId}")
    public Result<UserRoles> getUserRoles(@PathVariable String userId){
        System.out.println("getUserRoles "+userId);
        //UserRoles user = userService.findOneUserRoles(userId);
        //return (Result<UserRoles>)ResultUtil.success(user);
        return null;
    }*/

}

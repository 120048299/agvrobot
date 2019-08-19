package com.wootion.controller;

import com.wootion.agvrobot.dto.UserBean;
import com.wootion.agvrobot.utils.CommonTree;
import com.wootion.commons.Result;
import com.wootion.config.shiro.JWTUtil;
import com.wootion.mapper.SiteMapper;

import com.wootion.mapper.UserInfoMapper;
import com.wootion.model.Resource;
import com.wootion.model.Role;
import com.wootion.model.Site;
import com.wootion.model.UserInfo;
import com.wootion.service.ResourceService;
import com.wootion.service.UserService;
import com.wootion.utiles.ResultUtil;
import com.wootion.utiles.SessionManager;
import com.wootion.vo.LoginInfo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/req_svr")
public class LoginController {
    @Autowired
    UserService userService;


    @Autowired
    ResourceService resourceService;

    @Autowired
    SiteMapper siteMapper;

    @Autowired
    UserInfoMapper userInfoMapper;


    Logger logger = LoggerFactory.getLogger(LoginController.class);
    @ApiOperation(value = "登录",notes = "跳转登录页面")
    @GetMapping("/login")
    public Result login() {
        return ResultUtil.failed("未登录");
    }

    @ApiOperation(value = "实现登录",notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name="loginInfo", value = "登录信息", paramType = "body", required = true, dataType = "LoginInfo"),
    })

    @ResponseBody
    @PostMapping(value = "/loginUser")
    public Result<String> loginUser(@RequestBody LoginInfo loginInfo) {
        UserBean userBean = new UserBean();
        UserInfo userInfo = userInfoMapper.getUserByLoginname(loginInfo.getLoginname());
        if (userInfo != null) {
            userBean.setLoginname(userInfo.getLoginname());
            userBean.setPassword(userInfo.getPassword());
            userBean.setUserInfo(userInfo);
        }
        else {
            return  new Result(401,"用户名不存在",null);
        }
        List<Role> roleList = userInfoMapper.getRoleByUserId(userInfo.getUid());
        List<String> roleNames = new ArrayList<>();
        for (Role role : roleList) {
            roleNames.add(role.getRole());
        }
        Set<String> permissions = resourceService.findPermissionsByRole(new HashSet<>(roleList));
        if (!loginInfo.getLoginname().equals("2")) {
            permissions.remove("resultConfirm:browse:myTaskInfo");
            permissions.remove("resultConfirm:browse");
            permissions.remove("resultConfirm");
        }
        userBean.setPermissions(new ArrayList<>(permissions));
        userBean.setRoles(roleNames);
        List<Resource> resourceList = resourceService.findMenus(permissions);

        //菜单
        Resource r=new Resource();
        r.setUid("root");
        r.setName("系统导航");
        CommonTree<Resource> menu= new CommonTree<>(r);
        menu.addList(resourceList);

        userBean.setMenu(menu);
        Site site = siteMapper.findByUid(loginInfo.getSiteId());
        if (site==null) {
            return new Result(401,"站点不存在:siteId="+loginInfo.getSiteId(),null);
        }
        if (userBean == null) {
            return  new Result(401,"用户名不存在",null);
        }
        if (userBean.getPassword().equals(loginInfo.getPassword())) {
            String token = JWTUtil.sign(loginInfo.getLoginname(),loginInfo.getPassword());
            SessionManager.addOrUpdateSessionEntity(token,SessionManager.USER_BEAN,userBean);
            SessionManager.addOrUpdateSessionEntity(token,SessionManager.SITE,site);

            Map map=new HashMap();
            map.put("token",token);
            map.put("userBean",userBean);
            map.put("site",site);
            return new Result(200,"success", map);
        } else {
            throw new UnauthorizedException("账号或密码错误");
        }

    }


    @ApiOperation(value = "用户登出")
    @GetMapping("/logOut")
    public Result logOut(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        SessionManager.removeSessionEntity(token,SessionManager.USER_BEAN);
        return ResultUtil.success("登出成功");
    }


    @GetMapping("/checkTimeOut")
    @ResponseBody
    public Result checkTimeOut(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Map<String,Object> sessionBean = SessionManager.getSessionBean(token,false);
        if(sessionBean==null){
            return ResultUtil.build(1,"超时",null);
        }
        Date date1= (Date)sessionBean.get(SessionManager.LAST_USE_TIME);
        if(SessionManager.timeOut(date1,new Date())){
            //SessionManager.removeSessionBean(token);
            return ResultUtil.build(1,"超时",null);
        }
        return ResultUtil.build(0,"未超时",null);

    }

    @ResponseBody
    @PostMapping(value = "/chpwd")
    public Result chpwd(@RequestBody Map params) {
        UserInfo userInfo = new UserInfo();
        String loginName=(String)params.get("username");
        String password=(String)params.get("newpassword");
        if(loginName==null || "".equals(loginName) || password==null || "".equals(password)){
            return ResultUtil.failed("修改失败");
        }
        int ret = userInfoMapper.changePassword(loginName,password);
        if(ret > 0){
            return ResultUtil.success("修改成功");
        }
        else {
            return ResultUtil.success("修改失败");
        }

    }
}

package com.wootion.controller;

import com.github.pagehelper.PageInfo;
import com.wootion.agvrobot.dto.UserBean;
import com.wootion.agvrobot.utils.CommonTree;
import com.wootion.commons.Constans;
import com.wootion.commons.Result;
import com.wootion.model.Resource;
import com.wootion.service.ResourceService;
import com.wootion.utiles.ResultUtil;
import com.wootion.utiles.SessionManager;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


/**
 * @Author: ZhangJiqiang
 * @Description:
 * @Date: Created in 下午4:55 18-5-2
 * @Modified By:
 */
@RestController
@RequestMapping("/req_svr/resources")
public class ResourceController {

    @Autowired
    ResourceService resourceService;


/*
    @GetMapping("/menu")
    public Result<Resource> getMenu(HttpServletRequest request) {
        String token = request.getHeader(Constans.TOKEN);
        UserBean userBean = (UserBean) SessionManager.getSessionEntity(token, SessionManager.USER_BEAN);
        List<Resource> menu = resourceService.findMenus(new HashSet<>(userBean.getPermissions()));
        return (Result<Resource>) ResultUtil.success(menu);
    }*/

    /**
     * 资源树 可以按名称查询
     *
     * @param params
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("/tree")
    public CommonTree<Resource> tree(@RequestBody Map params, HttpServletRequest request) {
        List<Resource> list = resourceService.selectAll();
        if (list == null || list.size() == 0) {
            return null;
        }
        Resource root = new Resource();
        root.setUid("root");
        root.setName("权限");
        CommonTree<Resource> tree = new CommonTree<>(root);
        tree.addList(list);
        return tree;
    }

    @ResponseBody
    @GetMapping("/getResourceByRoleId/{roleId}")
    public List<Resource> getResourceByRoleId(@PathVariable String roleId) {
        List<Resource> list = resourceService.selectResource(roleId);
        return list;
    }
}
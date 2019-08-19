package com.wootion.controller;

import com.wootion.commons.Constans;
import com.wootion.model.Resource;
import com.wootion.model.Role;
import com.wootion.model.User;
import com.wootion.service.ResourceService;
import com.wootion.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;
@Controller
public class IndexController {
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private UserService userService;

/*
    @RequestMapping("/req_svr/index")
    public String index(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute(Constans.CURRENT_USER);
        List<Resource> menus = resourceService.findMenus(permissions);
        model.addAttribute("menus", menus);
        return "index";
    }*/

}

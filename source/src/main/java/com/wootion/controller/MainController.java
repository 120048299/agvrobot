package com.wootion.controller;

import com.wootion.commons.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class MainController {

    /*@GetMapping("/")
    public String root(){
        return "redirect:/login";
    }
*/

    @GetMapping("/req_svr/user")
    public String toUser(){
        return "user";
    }

    @ResponseBody
    @GetMapping("/401")
    public Result ackError() {
        return new Result(401,"have not login",null);
    }
    @ResponseBody
    @GetMapping("/403")
    public Result authenticate() {
        return new Result(403,"have no authority",null);
    }

}

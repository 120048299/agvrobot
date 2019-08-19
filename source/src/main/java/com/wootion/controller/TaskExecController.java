package com.wootion.controller;

import com.wootion.commons.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/req_svr/taskExec")
public class TaskExecController {

    @GetMapping
    Result getAllTaskPage(int pageNum, int pageSize) {
        return null;
    }
}

package com.wootion.controller;


import com.wootion.commons.Result;
import com.wootion.mapper.SysParamMapper;
import com.wootion.model.*;

import com.wootion.utiles.ResultUtil;

import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/req_svr/sysParams")
public class SysParamController {
    Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private SysParamMapper sysParamMapper;

    @ResponseBody
    @GetMapping("getAllParams")
    public List<SysParam> getAllParams(){
        List<SysParam> list = sysParamMapper.findAll();
        return list;
    }

    @ResponseBody
    @PostMapping(value = "/modifyParam")
    public Result modifyParam(@RequestBody Map params,  HttpServletRequest request) {
        // 获取参数
        String uid=(String)params.get("paramUid");
        String value=(String)params.get("paramValue");

        // 查询参数是否存在
        SysParam sysParam = sysParamMapper.findByUid(uid);
        if (sysParam == null) {
            return ResultUtil.failed("修改参数失败, 参数不存在。");
        }

        // 未变化则直接返回成功
        if (value.equals(sysParam.getValue())) {
            return ResultUtil.success(sysParam);
        }

        //  修改
        sysParam.setValue(value);
        int ret=sysParamMapper.update(sysParam);
        logger.info("modifyParam: ret= "+ret);
        if(ret<1){
            return ResultUtil.failed("修改参数失败，数据库操作错误。");
        }else{
            return ResultUtil.success(sysParam);
        }
    }

    @ResponseBody
    @PostMapping("/updateParam")
    public Result updateParam(@RequestBody List<Map<String,String>> params){
        // 获取参数
        int updateret=0;
        for(int i = 0;i < params.size();i++){
            Map<String,String> map=(Map) params.get(i);
            for (Map.Entry<String,String> entry : map.entrySet()) {
//                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                String key=entry.getKey();
                String value=entry.getValue();
                int ret=sysParamMapper.updateByKey(key,value);
                if(ret==1){
                    updateret=updateret+1;
                }
            }
        }

        if(updateret==params.size()) {
            return ResultUtil.build(0,"更新数据成功",null);
        }else{
            return ResultUtil.build(1,"更新数据失败",null);
        }
    }
}

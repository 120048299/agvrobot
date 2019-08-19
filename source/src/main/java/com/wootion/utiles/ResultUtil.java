package com.wootion.utiles;

import com.wootion.commons.Result;

public class ResultUtil {
    public static Result<?> success(Object data) {
        Result<?> result = new Result<>(0, "success", data);
        // result.setCode(0);
        // result.setMsg("success");
        // result.setData(data);
        return result;
    }

    public static Result success() {
        return success(null);
    }

    public static Result build(Integer code, String msg,Object data) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }
    public static Result failed(String msg){
        Result result = new Result();
        result.setCode(-1);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }
    public static Result failed(){
        return failed("failed");
    }
}

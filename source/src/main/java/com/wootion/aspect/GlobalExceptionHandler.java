package com.wootion.aspect;

import com.wootion.commons.Result;
import com.wootion.config.shiro.TokenNullException;
import com.wootion.exceptions.UserException;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(TokenNullException.class)
    @ResponseBody
    public Result handle401(HttpServletRequest req, Exception e) {
        return new Result<String>(401,e.getMessage(),null);
    }


    // 捕捉UnauthorizedException
    @ResponseBody
    @ExceptionHandler(UnauthorizedException.class)
    public Result handleUnauthorizedException(HttpServletRequest req, Exception e) {
        return new Result<String>(403, e.getMessage(), null);
    }

    @ResponseBody
    @ExceptionHandler(org.apache.shiro.authc.AuthenticationException.class)
    public Result haveNotLogin(HttpServletRequest req, Exception e) {
        return new Result<String>(401,e.getMessage(),null);
    }
    @ResponseBody
    @ExceptionHandler(UserException.class)
    public Result addUserWrong(HttpServletRequest req, Exception e) {
        return new Result<String>(-1,e.getMessage(),null);
    }







}

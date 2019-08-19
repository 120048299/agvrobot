package com.wootion.config.shiro;

import com.wootion.utiles.SessionManager;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTFilter extends BasicHttpAuthenticationFilter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String AUTH_TOKEN = "Authorization";

    /**
     * 判断用户是否想要登入。
     * 检测header里面是否包含Authorization字段即可
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        String authorization = req.getHeader(this.AUTH_TOKEN);
        if(authorization == null){
            return false;
        }
        // 如果不存在（过期或者服务器重启）
        Object sessionBean=null;
        if(((HttpServletRequest) request).getRequestURL().indexOf("checkTimeOut")>0){
            sessionBean=SessionManager.getSessionBean(authorization,false);
        }else{
            sessionBean=SessionManager.getSessionBean(authorization);
        }

        if(sessionBean==null){
            return false;
        }
        return true;
    }


    @Override
    protected boolean executeLogin (ServletRequest request, ServletResponse response){
        HttpServletRequest req = (HttpServletRequest) request;
        String authorization = req.getHeader(this.AUTH_TOKEN);
        if (authorization == null) {
            return false;
        }
        JWTToken token = new JWTToken(authorization);
        //交给realm进行登入,如果错误则会抛出异常

        getSubject(request,response).login(token);

        //没有异常则代表登录成功,返回true
        return true;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        boolean loggedIn = false; // false by default or we wouldn't be in this method
        try {
            if (isLoginAttempt(request, response)) {
                loggedIn = executeLogin(request, response);
            }
        } catch (UnauthorizedException e) {
            // response403(request, response);
            loggedIn = false;
        } catch (AuthenticationException ae) {
            System.out.println("catch ae2" + ae.getMessage());
            // response401(request, response);
            loggedIn = false;
        }
        if (!loggedIn) {
            sendChallenge(request, response);
        }
        return loggedIn;
    }



    @Override protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
       try {
           if (!isLoginAttempt(request, response) || !executeLogin(request,response)) {
               response401(request,response);
           }

       } catch (UnauthorizedException e) {
           response403(request,response);
           return false;
       } catch (AuthenticationException ae) {
           response401(request, response);
           return false;
       }

        return true;
    }



    /** * 未授权 403 */
    private void response403(ServletRequest req, ServletResponse resp) {
        try {
            HttpServletResponse httpServletResponse = (HttpServletResponse) resp;
            httpServletResponse.sendRedirect("/403");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
    /** *  未登录 401  */
    private void response401(ServletRequest req, ServletResponse resp) {
        try {
            HttpServletResponse httpServletResponse = (HttpServletResponse) resp;
            httpServletResponse.sendRedirect("/401");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }


}



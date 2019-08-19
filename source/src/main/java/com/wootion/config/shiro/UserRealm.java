package com.wootion.config.shiro;

import com.wootion.agvrobot.dto.UserBean;
import com.wootion.service.UserService;
import com.wootion.utiles.SessionManager;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Service("userRealm")
public class UserRealm extends AuthorizingRealm {


    @Autowired
    private UserService userService;


    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    /**
     * 权限匹配
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String token = principals.toString();
        String loginname = JWTUtil.getLoginname(token);
//        UserBean userBean = userService.getUserBean(loginname);
        UserBean userBean = (UserBean) SessionManager.getSessionEntity(token,SessionManager.USER_BEAN);
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        List<String> rs = userBean.getRoles();
        Set<String> roles;
        if (rs == null) {
            roles = new HashSet<>();
        } else {
            roles = new HashSet<>(rs);
        }
        simpleAuthorizationInfo.addRoles(roles);
        List<String> ps = userBean.getPermissions();
        Set<String> permissions = new HashSet<>();
        if (ps != null) {
            permissions = new HashSet<>(ps);
        }
        simpleAuthorizationInfo.addStringPermissions(permissions);
        return simpleAuthorizationInfo;

    }

    /**
     * 验证身份
     * @param auth
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
        String token = (String) auth.getCredentials();
        // 解密获得username，用于和数据库进行对比
        String loginname = JWTUtil.getLoginname(token);
        if (loginname == null) {
            throw new AuthenticationException("token can not match !");
        }
        UserBean userBean = (UserBean) SessionManager.getSessionEntity(token,SessionManager.USER_BEAN);

        if (userBean == null ) {
            userBean = userService.getUserBean(loginname);
            if (userBean == null) {
                throw new AuthenticationException("user " + loginname + " does not exist");
            } else {
                SessionManager.addOrUpdateSessionEntity(token,SessionManager.USER_BEAN,userBean);
            }

        }
        if (! JWTUtil.verify(token, loginname, userBean.getPassword())) {
                throw new AuthenticationException("username or password is wrong");
        }
        return new SimpleAuthenticationInfo(token, token, "userRealm");
    }


}

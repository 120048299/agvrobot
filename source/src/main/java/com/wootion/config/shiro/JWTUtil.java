package com.wootion.config.shiro;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.UnsupportedEncodingException;
import java.util.Date;

public class JWTUtil {
    private static final long EXPIRE_TIME =  24 * 3600  * 1000;

    private static final String systemSecret = "test";
    /**
     *
     * @Auther: ZhangJiqiang
     * @Description: 校验token是否正确
     * @Date: 上午9:56 18-5-7
     * @Params: token 秘钥, secret 用户的密码
     * @return: 是否正确
     * 
     */
    public static boolean verify(String token, String loginname, String secret){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim("loginname",loginname)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        }catch (Exception e) {
            return  false;
        }
    }


    public static String getLoginname(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("loginname").asString();
        }catch (JWTDecodeException e) {
            return null;
        }
    }

    public static String sign(String loginname, String  secret) {
        try {
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withClaim("loginname",loginname)
                    .withExpiresAt(date)
                    .sign(algorithm);
        }catch (UnsupportedEncodingException e) {
            return  null;
        }
    }
}

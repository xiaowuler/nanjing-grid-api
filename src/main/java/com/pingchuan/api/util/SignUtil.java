package com.pingchuan.api.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 登录 工具类
 * @author: XW
 * @create: 2019-11-11 13:12
 **/
public class SignUtil {

    private final static long EXPIRE_TIME = 24 * 60 * 60 * 1000;

    private final static String TOKEN_SECRET = "DA2E9DC0-F51D-4EC9-9795-275C7AC4846C";

    public static String sign(String username, String userCode){
        try{
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            Map<String, Object> header = new HashMap<>(2);
            header.put("typ", "JWT");
            header.put("alg", "HS256");
            return JWT.create()
                    .withHeader(header)
                    .withClaim("username", username)
                    .withClaim("userCode", userCode)
                    .withExpiresAt(date)
                    .sign(algorithm);
        }catch (UnsupportedEncodingException e){
            return null;
        }
    }

    public static boolean verify(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            verifier.verify(token);
            return true;
        }catch (UnsupportedEncodingException e){
            return false;
        }catch (TokenExpiredException tee){
            return false;
        }
    }

    public static String getClaim(String token, String name){
        try{
            DecodedJWT decode = JWT.decode(token);
            return decode.getClaim(name).asString();
        }catch (JWTDecodeException e){
            return null;
        }
    }

}

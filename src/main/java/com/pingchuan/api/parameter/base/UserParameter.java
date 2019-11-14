package com.pingchuan.api.parameter.base;

import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 登录 参数类
 * @author: XW
 * @create: 2019-11-11 13:52
 **/

@Data
public class UserParameter {

    private String username;

    private String secret;

    private String token;

    public List<String> checkLoginCode(){
        List<String> errors = new ArrayList<>();

        if (StringUtils.isEmpty(username))
            errors.add("username不能为空");

        if (StringUtils.isEmpty(secret))
            errors.add("secret不能为空");

        return errors;
    }

    public String checkLoginOutCode(){

        if (StringUtils.isEmpty(token))
            return "token不能为空";

        return null;
    }

}

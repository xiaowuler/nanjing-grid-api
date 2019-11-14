package com.pingchuan.api.controller;

import com.pingchuan.api.contants.ResultCode;
import com.pingchuan.api.dto.UserDTO;
import com.pingchuan.api.model.Caller;
import com.pingchuan.api.model.Token;
import com.pingchuan.api.parameter.base.UserParameter;
import com.pingchuan.api.service.CallerService;
import com.pingchuan.api.service.TokenService;
import com.pingchuan.api.util.ApiResponse;
import com.pingchuan.api.util.SignUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description: 用户访问层
 * @author: XW
 * @create: 2019-11-11 13:04
 **/

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private CallerService callerService;

    @Autowired
    private TokenService tokenService;

    //@Action(name = "/login", isNeedElementCode = true, apiId = 1)
    @RequestMapping("/login")
    public ApiResponse login(UserParameter user){
        List<String> errors = user.checkLoginCode();
        if (errors.size()>0)
            return new ApiResponse(ResultCode.PARAM_ERROR, String.join(",", errors), null);

        Caller caller = callerService.findOneByUsernameAndPassword(user.getUsername(), user.getSecret());
        if (caller == null)
            return new ApiResponse(ResultCode.LOGIN_ERROR, "用户名或密码错误", null);

        return new ApiResponse(ResultCode.SUCCESS, "登录成功", new UserDTO(caller.getLoginName(), caller.getRealName(), getToken(caller)));
    }

    @RequestMapping("/loginOut")
    public ApiResponse loginOut(UserParameter user){
        String error = user.checkLoginOutCode();
        if (!StringUtils.isEmpty(error))
            return new ApiResponse(ResultCode.PARAM_ERROR, error, null);

        String callerCode = SignUtil.getClaim(user.getToken(), "userCode");
        if (StringUtils.isEmpty(callerCode))
            return new ApiResponse(ResultCode.FAILED, "token不正确", null);

        tokenService.deleteOneByCallerCode(callerCode);

        return new ApiResponse(ResultCode.SUCCESS, "注销成功", null);
    }

    private String getToken(Caller caller){
        Token token = tokenService.findOneByCallerCode(caller.getCode());

        if (token == null || !SignUtil.verify(token.getToken()))
        {
            String key = SignUtil.sign(caller.getLoginName(), caller.getCode());
            tokenService.deleteOneByCallerCode(caller.getCode());
            tokenService.InsertOne(new Token(caller.getCode(), key));
            return key;
        }

        return token.getToken();
    }

}

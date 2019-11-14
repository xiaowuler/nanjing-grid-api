package com.pingchuan.api.aops;

import com.alibaba.fastjson.JSON;
import com.pingchuan.api.annotation.Action;
import com.pingchuan.api.annotation.CalcAction;
import com.pingchuan.api.contants.ResultCode;
import com.pingchuan.api.dao.InterfaceLogService;
import com.pingchuan.api.model.CallerInterface;
import com.pingchuan.api.model.Interface;
import com.pingchuan.api.model.InterfaceLog;
import com.pingchuan.api.parameter.Parameter;
import com.pingchuan.api.service.CallerInterfaceService;
import com.pingchuan.api.service.InterfaceService;
import com.pingchuan.api.util.ApiResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @description: 计算 切面
 * @author: XW
 * @create: 2019-11-13 15:34
 **/

@Component
@Aspect
public class CalcAop {
    @Autowired
    private InterfaceService interfaceService;

    @Autowired
    private InterfaceLogService interfaceLogService;

    @Autowired
    private CallerInterfaceService callerInterfaceService;

    private InterfaceLog interfaceLog = new InterfaceLog();

    @Pointcut("@annotation(com.pingchuan.api.annotation.CalcAction)")
    public void annotationPointCut(){}

    @Around("annotationPointCut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint){
        try{
            setInterfaceLogRequestInfo();
            MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
            CalcAction action = signature.getMethod().getAnnotation(CalcAction.class);
            interfaceLog.setInterfaceId(action.apiId());

            //验证接口访问
            Interface api = interfaceService.findOneById(action.apiId());
            if (api == null || api.getEnabled() == 0)
                return new ApiResponse(ResultCode.API_INVALID, "接口未开启", null);

            //验证token;
            Parameter parameter = (Parameter) proceedingJoinPoint.getArgs()[0];
            parameter.setCalcType(action.calcType());
            interfaceLog.setParameters(JSON.toJSONString(parameter));
            interfaceLog.setRegionCode(parameter.getAreaCode());
            if (!parameter.verifyToken())
            {
                setInterfaceLog("token无效或已过期，请重新登录", (byte) 0);
                interfaceLog.setCallerCode(parameter.getCallerCode());
                return new ApiResponse(ResultCode.FAILED, "token无效或已过期，请重新登录", null);
            }

            interfaceLog.setCallerCode(parameter.getCallerCode());
            //验证权限
            CallerInterface callerInterface = callerInterfaceService.findOneByCallerAndInterface(parameter.getCallerCode(), action.apiId());
            if (StringUtils.isEmpty(callerInterface))
            {
                setInterfaceLog("权限不足，不能访问本接口，请联系管理员", (byte) 0);
                return new ApiResponse(ResultCode.PERMISSION_DENIED, "权限不足，不能访问本接口，请联系管理员", null);
            }

            //验证参数
            List<String> errors = parameter.checkCode(true);
            if (errors.size() > 0)
            {
                String errorMsg = String.join(",", errors);
                setInterfaceLog(errorMsg, (byte) 0);
                return new ApiResponse(ResultCode.PARAM_ERROR, errorMsg, null);
            }

            interfaceLog.setStartTime(System.currentTimeMillis());
            ApiResponse apiResponse =  (ApiResponse) proceedingJoinPoint.proceed();
            interfaceLog.setEndTime(System.currentTimeMillis());

            if (StringUtils.isEmpty(apiResponse))
                return apiResponse;
            if (apiResponse.getRetCode() == ResultCode.SUCCESS)
                setInterfaceLog(apiResponse.getRetMsg(), (byte) 1);
            else
                setInterfaceLog(apiResponse.getRetMsg(), (byte) 0);
            return apiResponse;

        }catch (Exception e){

            if(!StringUtils.isEmpty(interfaceLog.getStartTime()))
                interfaceLog.setEndTime(System.currentTimeMillis());

            interfaceLog.setErrorMessage(e.getMessage());
            return new ApiResponse(ResultCode.FAILED, e.toString(), null);

        } catch (Throwable throwable) {
            if(!StringUtils.isEmpty(interfaceLog.getStartTime()))
                interfaceLog.setEndTime(System.currentTimeMillis());

            interfaceLog.setErrorMessage(throwable.getMessage());
            return new ApiResponse(ResultCode.FAILED, throwable.toString(), null);
        }
    }

    @After("annotationPointCut()")
    public void after(){
        interfaceLog.setStopTime(System.currentTimeMillis());
        if (!StringUtils.isEmpty(interfaceLog.getCallerCode()))
            interfaceLogService.insertOne(interfaceLog);
    }

    private void setInterfaceLog(String errorMsg, byte state){
        interfaceLog.setErrorMessage(errorMsg);
        interfaceLog.setState(state);
    }

    private void setInterfaceLogRequestInfo(){
        interfaceLog.setCreateTime(System.currentTimeMillis());
        ServletRequestAttributes sra = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        interfaceLog.setRequestType(request.getMethod());
        interfaceLog.setHostAddress(request.getRemoteAddr());
    }
}

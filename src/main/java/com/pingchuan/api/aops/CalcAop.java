package com.pingchuan.api.aops;

import com.alibaba.fastjson.JSON;
import com.pingchuan.api.annotation.CalcAction;
import com.pingchuan.api.contants.ResultCode;
import com.pingchuan.api.service.InterfaceLogService;
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
import java.sql.Timestamp;
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
            if (api == null || api.getEnabled() == 0) {
                interfaceLog.setResultCode(ResultCode.API_INVALID);
                return new ApiResponse(ResultCode.API_INVALID, "接口未开启", null);
            }

            //验证token;
            Parameter parameter = (Parameter) proceedingJoinPoint.getArgs()[0];
            parameter.setCalcType(action.calcType());
            interfaceLog.setParameters(JSON.toJSONString(parameter));
            interfaceLog.setRegionCode(parameter.getAreaCode());
            if (!parameter.verifyToken())
            {
                setInterfaceLog("token无效或已过期，请重新登录", ResultCode.TOKEN_INVALID);
                interfaceLog.setCallerCode(parameter.getCallerCode());
                return new ApiResponse(ResultCode.TOKEN_INVALID, "token无效或已过期，请重新登录", null);
            }

            interfaceLog.setCallerCode(parameter.getCallerCode());
            //验证权限
            CallerInterface callerInterface = callerInterfaceService.findOneByCallerAndInterface(parameter.getCallerCode(), action.apiId());
            if (StringUtils.isEmpty(callerInterface))
            {
                setInterfaceLog("权限不足，不能访问本接口，请联系管理员", ResultCode.PERMISSION_DENIED);
                return new ApiResponse(ResultCode.PERMISSION_DENIED, "权限不足，不能访问本接口，请联系管理员", null);
            }

            //验证参数
            List<String> errors = parameter.checkCode(action.isArea());
            if (errors.size() > 0)
            {
                String errorMsg = String.join(",", errors);
                setInterfaceLog(errorMsg, ResultCode.PARAM_ERROR);
                return new ApiResponse(ResultCode.PARAM_ERROR, errorMsg, null);
            }

            interfaceLog.setExecuteStartTime(new Timestamp(System.currentTimeMillis()));
            ApiResponse apiResponse =  (ApiResponse) proceedingJoinPoint.proceed();
            interfaceLog.setExecuteEndTime(new Timestamp(System.currentTimeMillis()));

            if (StringUtils.isEmpty(apiResponse)) {
                return apiResponse;
            }
            if (apiResponse.getRetCode() == ResultCode.SUCCESS) {
                setInterfaceLog(apiResponse.getRetMsg(), ResultCode.SUCCESS);
            } else {
                setInterfaceLog(apiResponse.getRetMsg(), ResultCode.EXCEPTION);
            }
            return apiResponse;

        }catch (Exception e){
            return getExceptionResult(e.toString());
        } catch (Throwable throwable) {
            return getExceptionResult(throwable.toString());
        }
    }

    @After("annotationPointCut()")
    public void after(){
        interfaceLog.setRequestEndTime(new Timestamp(System.currentTimeMillis()));
        if (!StringUtils.isEmpty(interfaceLog.getCallerCode())) {
            interfaceLogService.insertOne(interfaceLog);
        }
    }

    private ApiResponse getExceptionResult(String errorMsg){
        if(!StringUtils.isEmpty(interfaceLog.getExecuteStartTime())) {
            interfaceLog.setExecuteEndTime(new Timestamp(System.currentTimeMillis()));
        }

        interfaceLog.setResultCode(ResultCode.EXCEPTION);
        interfaceLog.setErrorMessage(errorMsg);
        return new ApiResponse(ResultCode.EXCEPTION, errorMsg, null);
    }

    private void setInterfaceLog(String errorMsg, int resultCode){
        interfaceLog.setErrorMessage(errorMsg);
        interfaceLog.setResultCode(resultCode);
    }

    private void setInterfaceLogRequestInfo(){
        interfaceLog.setState((byte) 0);
        interfaceLog.setRequestStartTime(new Timestamp(System.currentTimeMillis()));
        ServletRequestAttributes sra = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        interfaceLog.setRequestType(request.getMethod());
        interfaceLog.setHostAddress(request.getRemoteAddr());
    }
}

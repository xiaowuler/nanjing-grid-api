package com.pingchuan.api.controller;

import com.pingchuan.api.annotation.CalcAction;
import com.pingchuan.api.contants.ResultCode;
import com.pingchuan.api.dto.calc.CalcElementValue;
import com.pingchuan.api.parameter.calc.TimeEffectParameter;
import com.pingchuan.api.service.CalcForecastService;
import com.pingchuan.api.parameter.calc.ForecastParameter;
import com.pingchuan.api.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 统计 接口
 * @author: XW
 * @create: 2019-11-13 15:21
 **/

@RestController
@RequestMapping("/calcForecast")
public class CalcForecastController {

    @Autowired
    private CalcForecastService calcForecastService;

    @CalcAction(apiId = 11, isArea = true, calcType = "max")
    @RequestMapping("/findMaxElementValueByArea")
    public ApiResponse findMaxElementValueByArea(ForecastParameter forecast){
        CalcElementValue calcElementValue = calcForecastService.calcElementValueByArea(forecast);
        if (StringUtils.isEmpty(calcElementValue))
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);

        return  new ApiResponse(ResultCode.SUCCESS, "查询成功", calcElementValue);
    }

    @CalcAction(apiId = 12, isArea = true, calcType = "min")
    @RequestMapping("/findMinElementValueByArea")
    public ApiResponse findMinElementValueByArea(ForecastParameter forecast){
        CalcElementValue calcElementValue = calcForecastService.calcElementValueByArea(forecast);
        if (StringUtils.isEmpty(calcElementValue))
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);

        return  new ApiResponse(ResultCode.SUCCESS, "查询成功", calcElementValue);
    }

    @CalcAction(apiId = 13, isArea = true, calcType = "avg")
    @RequestMapping("/findAvgElementValueByArea")
    public ApiResponse findAvgElementValueByArea(ForecastParameter forecast){
        CalcElementValue calcElementValue = calcForecastService.calcElementValueByArea(forecast);
        if (StringUtils.isEmpty(calcElementValue))
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);

        return  new ApiResponse(ResultCode.SUCCESS, "查询成功", calcElementValue);
    }

    @CalcAction(apiId = 14, isArea = true, calcType = "max")
    @RequestMapping("/findMaxElementValueTimeEffectByArea")
    public ApiResponse findMaxElementValueTimeEffectByArea(TimeEffectParameter timeEffect){
        CalcElementValue calcElementValue = calcForecastService.calcElementValueTimeEffectByArea(timeEffect);
        if (StringUtils.isEmpty(calcElementValue))
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);

        return  new ApiResponse(ResultCode.SUCCESS, "查询成功", calcElementValue);
    }

    @CalcAction(apiId = 15, isArea = true, calcType = "min")
    @RequestMapping("/findMinElementValueTimeEffectByArea")
    public ApiResponse findMinElementValueTimeEffectByArea(TimeEffectParameter timeEffect){
        CalcElementValue calcElementValue = calcForecastService.calcElementValueTimeEffectByArea(timeEffect);
        if (StringUtils.isEmpty(calcElementValue))
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);

        return  new ApiResponse(ResultCode.SUCCESS, "查询成功", calcElementValue);
    }

    @CalcAction(apiId = 16, isArea = true, calcType = "avg")
    @RequestMapping("/findAvgElementValueTimeEffectByArea")
    public ApiResponse findAvgElementValueTimeEffectByArea(TimeEffectParameter timeEffect){
        CalcElementValue calcElementValue = calcForecastService.calcElementValueTimeEffectByArea(timeEffect);
        if (StringUtils.isEmpty(calcElementValue))
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);

        return  new ApiResponse(ResultCode.SUCCESS, "查询成功", calcElementValue);
    }

}

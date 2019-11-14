package com.pingchuan.api.controller;

import com.pingchuan.api.annotation.Action;
import com.pingchuan.api.contants.ResultCode;
import com.pingchuan.api.dao.InterfaceLogService;
import com.pingchuan.api.dto.base.LineInfo;
import com.pingchuan.api.dto.base.PointInfo;
import com.pingchuan.api.dto.base.ThresholdInfo;
import com.pingchuan.api.parameter.base.*;
import com.pingchuan.api.service.ForecastValueService;
import com.pingchuan.api.service.TokenService;
import com.pingchuan.api.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description: 查询控制层
 * @author: XW
 * @create: 2019-11-07 15:28
 **/
@RequestMapping("weatherSearchService")
@RestController
public class BaseForecastController {

    @Autowired
    private ForecastValueService forecastValueService;

    @Autowired
    private InterfaceLogService interfaceLogService;

    @Autowired
    private TokenService tokenService;

    @RequestMapping("/findNJGridsByArea")
    @Action(isNeedElementCode = true, apiId = 1)
    public ApiResponse findNJGridsByArea(AreaParameter area){
        PointInfo pointInfo = forecastValueService.findNJGridsByArea(area);
        if (StringUtils.isEmpty(pointInfo))
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);
        return new ApiResponse(ResultCode.SUCCESS, "查询成功", pointInfo);
    }

    @Action(isNeedElementCode = false, apiId = 2)
    @RequestMapping("/findNJGridsByAreaAllElement")
    public ApiResponse findNJGridsByAreaAllElement(AreaParameter area){
        PointInfo pointInfo = forecastValueService.findNJGridsByAreaAllElement(area);
        if (StringUtils.isEmpty(pointInfo))
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);
        return new ApiResponse(ResultCode.SUCCESS, "查询成功", pointInfo);
    }

    @Action(isNeedElementCode = true, apiId = 3)
    @RequestMapping("/findNJGridsByLocation")
    public ApiResponse findNJGridsByLocation(LocationParameter location){

        PointInfo pointInfo = forecastValueService.findNJGridsByLocation(location);
        if (StringUtils.isEmpty(pointInfo))
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);
        return new ApiResponse(ResultCode.SUCCESS, "查询成功", pointInfo);
    }

    @Action(isNeedElementCode = false, apiId = 4)
    @RequestMapping("/findNJGridsByLocationAllElement")
    public ApiResponse findNJGridsByLocationAllElement(LocationParameter location){

        PointInfo pointInfo = forecastValueService.findNJGridsByLocation(location);
        if (StringUtils.isEmpty(pointInfo))
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);
        return new ApiResponse(ResultCode.SUCCESS, "查询成功", pointInfo);
    }

    @Action(isNeedElementCode = true, apiId = 5)
    @RequestMapping("/findNJGridsByForecastTimeRange")
    public ApiResponse findNJGridsByForecastTimeRange(LineParameter line){
        LineInfo lineInfo =forecastValueService.findNJGridsByForecastTimeRange(line);
        if (StringUtils.isEmpty(lineInfo))
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);
        return new ApiResponse(ResultCode.SUCCESS, "查询成功", lineInfo);
    }

    @Action(isNeedElementCode = false, apiId = 6)
    @RequestMapping("/findNJGridsByForecastTimeRangeAllElement")
    public ApiResponse findNJGridsByForecastTimeRangeAllElement(LineParameter line){
        LineInfo lineInfo =forecastValueService.findNJGridsByForecastTimeRange(line);
        if (StringUtils.isEmpty(lineInfo))
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);
        return new ApiResponse(ResultCode.SUCCESS, "查询成功", lineInfo);
    }

    @Action(isNeedElementCode = false, apiId = 7)
    @RequestMapping("/findNJGridsByTimeEffect")
    public ApiResponse findNJGridsByTimeEffect(TimeEffectParameter area){

        PointInfo pointInfo = forecastValueService.findNJGridsByTimeEffect(area);

        if (StringUtils.isEmpty(pointInfo))
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);
        return new ApiResponse(ResultCode.SUCCESS, "查询成功", pointInfo);
    }

    @Action(isNeedElementCode = false, apiId = 8)
    @RequestMapping("/findNJGridsByTimeEffectAllElement")
    public ApiResponse findNJGridsByTimeEffectAllElement(TimeEffectParameter area){

        PointInfo pointInfo = forecastValueService.findNJGridsByTimeEffectAllElement(area);

        if (StringUtils.isEmpty(pointInfo))
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);
        return new ApiResponse(ResultCode.SUCCESS, "查询成功", pointInfo);
    }

    @Action(isNeedElementCode = true, apiId = 9)
    @RequestMapping("/findNJGridsByElementThresholdArea")
    public ApiResponse findNJGridsByElementThresholdArea(ThresholdAreaParameter thresholdArea){
        List<ThresholdInfo> thresholdInfos = forecastValueService.findNJGridsByElementThresholdArea(thresholdArea);
        if (thresholdInfos.size() == 0)
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);
        return new ApiResponse(ResultCode.SUCCESS, "查询成功", thresholdInfos);
    }

    @Action(isNeedElementCode = true, apiId = 10)
    @RequestMapping("/findNJGridsByElementThresholdLocation")
    public ApiResponse findNJGridsByElementThresholdLocation(ThresholdLocationParameter thresholdLocation){
        List<ThresholdInfo> thresholdInfos = forecastValueService.findNJGridsByElementThresholdLocation(thresholdLocation);
        if (thresholdInfos.size() == 0)
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);
        return new ApiResponse(ResultCode.SUCCESS, "查询成功", thresholdInfos);
    }

}

package com.pingchuan.api.controller;

import com.pingchuan.api.annotation.Action;
import com.pingchuan.api.contants.ResultCode;
import com.pingchuan.api.dao.InterfaceLogService;
import com.pingchuan.api.dto.base.Element;
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
        List<Element> elements = forecastValueService.findNJGridsByArea(area);
        if (elements.size() == 0) {
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);
        }
        return new ApiResponse(ResultCode.SUCCESS, "查询成功", elements.get(0));
    }

    @Action(isNeedElementCode = false, apiId = 2)
    @RequestMapping("/findNJGridsByAreaAllElement")
    public ApiResponse findNJGridsByAreaAllElement(AreaParameter area){
        List<Element> elements = forecastValueService.findNJGridsByAreaAllElement(area);
        if (elements.size() == 0) {
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);
        }
        return new ApiResponse(ResultCode.SUCCESS, "查询成功", elements.get(0));
    }

    @Action(isNeedElementCode = true, apiId = 3)
    @RequestMapping("/findNJGridsByLocation")
    public ApiResponse findNJGridsByLocation(LocationParameter location){

        List<Element> elements = forecastValueService.findNJGridsByLocation(location);
        if (elements.size() == 0) {
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);
        }
        return new ApiResponse(ResultCode.SUCCESS, "查询成功", elements.get(0));
    }

    @Action(isNeedElementCode = false, apiId = 4)
    @RequestMapping("/findNJGridsByLocationAllElement")
    public ApiResponse findNJGridsByLocationAllElement(LocationParameter location){

        List<Element> elements = forecastValueService.findNJGridsByLocation(location);
        if (elements.size() == 0) {
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);
        }
        return new ApiResponse(ResultCode.SUCCESS, "查询成功", elements.get(0));
    }

    @Action(isNeedElementCode = true, apiId = 5)
    @RequestMapping("/findNJGridsByForecastTimeRange")
    public ApiResponse findNJGridsByForecastTimeRange(LineParameter line){
        List<Element> elements =forecastValueService.findNJGridsByForecastTimeRange(line);
        if (elements.size() == 0) {
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);
        }
        return new ApiResponse(ResultCode.SUCCESS, "查询成功", elements.get(0));
    }

    @Action(isNeedElementCode = false, apiId = 6)
    @RequestMapping("/findNJGridsByForecastTimeRangeAllElement")
    public ApiResponse findNJGridsByForecastTimeRangeAllElement(LineParameter line){
        List<Element> elements =forecastValueService.findNJGridsByForecastTimeRange(line);
        if (elements.size() == 0) {
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);
        }
        return new ApiResponse(ResultCode.SUCCESS, "查询成功", elements.get(0));
    }

    @Action(isNeedElementCode = false, apiId = 7)
    @RequestMapping("/findNJGridsByTimeEffect")
    public ApiResponse findNJGridsByTimeEffect(TimeEffectParameter area){

        List<Element> elements = forecastValueService.findNJGridsByTimeEffect(area);
        if (elements.size() == 0) {
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);
        }
        return new ApiResponse(ResultCode.SUCCESS, "查询成功", elements.get(0));
    }

    @Action(isNeedElementCode = false, apiId = 8)
    @RequestMapping("/findNJGridsByTimeEffectAllElement")
    public ApiResponse findNJGridsByTimeEffectAllElement(TimeEffectParameter area){

        List<Element> elements = forecastValueService.findNJGridsByTimeEffectAllElement(area);
        if (elements.size() == 0) {
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);
        }
        return new ApiResponse(ResultCode.SUCCESS, "查询成功", elements.get(0));
    }

    @Action(isNeedElementCode = true, apiId = 9)
    @RequestMapping("/findNJGridsByElementThresholdArea")
    public ApiResponse findNJGridsByElementThresholdArea(ThresholdAreaParameter thresholdArea){
        List<Element> elements = forecastValueService.findNJGridsByElementThresholdArea(thresholdArea);
        if (elements.size() == 0) {
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);
        }
        return new ApiResponse(ResultCode.SUCCESS, "查询成功", elements);
    }

    @Action(isNeedElementCode = true, apiId = 10)
    @RequestMapping("/findNJGridsByElementThresholdLocation")
    public ApiResponse findNJGridsByElementThresholdLocation(ThresholdLocationParameter thresholdLocation){
        List<Element> elements = forecastValueService.findNJGridsByElementThresholdLocation(thresholdLocation);
        if (elements.size() == 0) {
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);
        }
        return new ApiResponse(ResultCode.SUCCESS, "查询成功", elements);
    }

}

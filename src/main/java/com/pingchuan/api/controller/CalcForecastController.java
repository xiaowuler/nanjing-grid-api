package com.pingchuan.api.controller;

import com.pingchuan.api.annotation.CalcAction;
import com.pingchuan.api.contants.ResultCode;
import com.pingchuan.api.dto.calc.CalcElementValue;
import com.pingchuan.api.dto.calc.TotalCalc;
import com.pingchuan.api.parameter.calc.TimeEffectParameter;
import com.pingchuan.api.parameter.calc.TimeRangeParameter;
import com.pingchuan.api.parameter.calc.TotalHourParameter;
import com.pingchuan.api.service.CalcForecastService;
import com.pingchuan.api.parameter.calc.ForecastParameter;
import com.pingchuan.api.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
        List<TotalCalc> totalCalcs = calcForecastService.calcElementValueByArea(forecast);
        if (totalCalcs.size() == 0) {
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);
        }

        return  new ApiResponse(ResultCode.SUCCESS, "查询成功", totalCalcs.get(0));
    }

    @CalcAction(apiId = 12, isArea = true, calcType = "min")
    @RequestMapping("/findMinElementValueByArea")
    public ApiResponse findMinElementValueByArea(ForecastParameter forecast){
        List<TotalCalc> totalCalcs = calcForecastService.calcElementValueByArea(forecast);
        if (totalCalcs.size() == 0) {
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);
        }

        return  new ApiResponse(ResultCode.SUCCESS, "查询成功", totalCalcs.get(0));
    }

    @CalcAction(apiId = 13, isArea = true, calcType = "avg")
    @RequestMapping("/findAvgElementValueByArea")
    public ApiResponse findAvgElementValueByArea(ForecastParameter forecast){
        List<TotalCalc> totalCalcs = calcForecastService.calcElementValueByArea(forecast);
        return getApiResponseByTotalCalc(totalCalcs);
    }

    private ApiResponse getApiResponseByTotalCalc(List<TotalCalc> totalCalcs){
        if (totalCalcs.size() == 0) {
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);
        }

        return  new ApiResponse(ResultCode.SUCCESS, "查询成功", totalCalcs.get(0));
    }

    @CalcAction(apiId = 14, isArea = true, calcType = "max")
    @RequestMapping("/findMaxElementValueTimeEffectByArea")
    public ApiResponse findMaxElementValueTimeEffectByArea(TimeEffectParameter timeEffect){
        List<TotalCalc> totalCalcs = calcForecastService.calcElementValueTimeEffectByArea(timeEffect);
        return getApiResponseByTotalCalc(totalCalcs);
    }

    @CalcAction(apiId = 15, isArea = true, calcType = "min")
    @RequestMapping("/findMinElementValueTimeEffectByArea")
    public ApiResponse findMinElementValueTimeEffectByArea(TimeEffectParameter timeEffect){
        List<TotalCalc> totalCalcs = calcForecastService.calcElementValueTimeEffectByArea(timeEffect);
        return getApiResponseByTotalCalc(totalCalcs);
    }

    @CalcAction(apiId = 16, isArea = true, calcType = "avg")
    @RequestMapping("/findAvgElementValueTimeEffectByArea")
    public ApiResponse findAvgElementValueTimeEffectByArea(TimeEffectParameter timeEffect){
        List<TotalCalc> totalCalcs = calcForecastService.calcElementValueTimeEffectByArea(timeEffect);
        return getApiResponseByTotalCalc(totalCalcs);
    }

    @CalcAction(apiId = 17, isArea = true, calcType = "max")
    @RequestMapping("/findMaxElementValueTimeRangeByLocation")
    public ApiResponse findMaxElementValueTimeRangeByLocation(TimeRangeParameter timeRange){
        List<TotalCalc> totalCalcs = calcForecastService.calcElementValueTimeRangeByLocation(timeRange);
        return getApiResponseByTotalCalc(totalCalcs);
    }

    @CalcAction(apiId = 18, isArea = true, calcType = "min")
    @RequestMapping("/findMinElementValueTimeRangeByLocation")
    public ApiResponse findMinElementValueTimeRangeByLocation(TimeRangeParameter timeRange){
        List<TotalCalc> totalCalcs = calcForecastService.calcElementValueTimeRangeByLocation(timeRange);
        return getApiResponseByTotalCalc(totalCalcs);
    }

    @CalcAction(apiId = 19, isArea = true, calcType = "avg")
    @RequestMapping("/findAvgElementValueTimeRangeByLocation")
    public ApiResponse findAvgElementValueTimeRangeByLocation(TimeRangeParameter timeRange){
        List<TotalCalc> totalCalcs = calcForecastService.calcElementValueTimeRangeByLocation(timeRange);
        return getApiResponseByTotalCalc(totalCalcs);
    }

    @CalcAction(apiId = 20, isArea = true, calcType = "sum")
    @RequestMapping("/findTotalRain12Hour")
    public ApiResponse findTotalRain12Hour(TotalHourParameter totalHour){
        totalHour.setElementCode("PRE");
        totalHour.setTimeInterval(12);

        List<TotalCalc> totalCalcs = calcForecastService.findTotalByTimeInterval(totalHour);
        return getApiResponseByTotalCalc(totalCalcs);
    }

    @CalcAction(apiId = 21, isArea = true, calcType = "sum")
    @RequestMapping("/findTotalRain24Hour")
    public ApiResponse findTotalRain24Hour(TotalHourParameter totalHour){
        totalHour.setElementCode("PRE");
        totalHour.setTimeInterval(24);

        List<TotalCalc> totalCalcs = calcForecastService.findTotalByTimeInterval(totalHour);
        return getApiResponseByTotalCalc(totalCalcs);
    }

    @CalcAction(apiId = 22, isArea = true, calcType = "max")
    @RequestMapping("/findMaxTmp24Hour")
    public ApiResponse findMaxTmp24Hour(TotalHourParameter totalHour){
        totalHour.setElementCode("T2M");
        totalHour.setTimeInterval(24);

        List<TotalCalc> totalCalcs = calcForecastService.findTotalByTimeInterval(totalHour);
        return getApiResponseByTotalCalc(totalCalcs);
    }

    @CalcAction(apiId = 23, isArea = true, calcType = "min")
    @RequestMapping("/findMinTmp24Hour")
    public ApiResponse findMinTmp24Hour(TotalHourParameter totalHour){
        totalHour.setElementCode("T2M");
        totalHour.setTimeInterval(24);

        List<TotalCalc> totalCalcs = calcForecastService.findTotalByTimeInterval(totalHour);
        return getApiResponseByTotalCalc(totalCalcs);
    }

    @CalcAction(apiId = 24, isArea = true, calcType = "max")
    @RequestMapping("/findMaxWindSpeed12Hour")
    public ApiResponse findMaxWindSpeed12Hour(TotalHourParameter totalHour){
        totalHour.setElementCode("U10M");
        totalHour.setTimeInterval(12);

        List<TotalCalc> totalCalcs = calcForecastService.findTotalByTimeInterval(totalHour);
        return getApiResponseByTotalCalc(totalCalcs);
    }

    @CalcAction(apiId = 25, isArea = true, calcType = "avg")
    @RequestMapping("/findAvgHum12Hour")
    public ApiResponse findAvgHum12Hour(TotalHourParameter totalHour){
        totalHour.setElementCode("R2M");
        totalHour.setTimeInterval(12);

        List<TotalCalc> totalCalcs = calcForecastService.findTotalByTimeInterval(totalHour);
        return getApiResponseByTotalCalc(totalCalcs);
    }

    @CalcAction(apiId = 26, isArea = true, calcType = "max")
    @RequestMapping("/findMaxHum12Hour")
    public ApiResponse findMaxHum12Hour(TotalHourParameter totalHour){
        totalHour.setElementCode("R2M");
        totalHour.setTimeInterval(12);

        List<TotalCalc> totalCalcs = calcForecastService.findTotalByTimeInterval(totalHour);
        return getApiResponseByTotalCalc(totalCalcs);
    }

    @CalcAction(apiId = 27, isArea = true, calcType = "min")
    @RequestMapping("/findMinHum12Hour")
    public ApiResponse findMinHum12Hour(TotalHourParameter totalHour){
        totalHour.setElementCode("R2M");
        totalHour.setTimeInterval(12);

        List<TotalCalc> totalCalcs = calcForecastService.findTotalByTimeInterval(totalHour);
        return getApiResponseByTotalCalc(totalCalcs);
    }

    @CalcAction(apiId = 28, isArea = true, calcType = "max")
    @RequestMapping("/findMaxVis12Hour")
    public ApiResponse findMaxVis12Hour(TotalHourParameter totalHour){
        totalHour.setElementCode("VIS");
        totalHour.setTimeInterval(12);

        List<TotalCalc> totalCalcs = calcForecastService.findTotalByTimeInterval(totalHour);
        return getApiResponseByTotalCalc(totalCalcs);
    }

    @CalcAction(apiId = 29, isArea = true, calcType = "min")
    @RequestMapping("/findMinVis12Hour")
    public ApiResponse findMinVis12Hour(TotalHourParameter totalHour){
        totalHour.setElementCode("VIS");
        totalHour.setTimeInterval(12);

        List<TotalCalc> totalCalcs = calcForecastService.findTotalByTimeInterval(totalHour);
        return getApiResponseByTotalCalc(totalCalcs);
    }
}

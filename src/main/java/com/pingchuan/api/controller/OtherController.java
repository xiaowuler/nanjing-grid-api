package com.pingchuan.api.controller;

import com.pingchuan.api.annotation.OtherAction;
import com.pingchuan.api.contants.ResultCode;
import com.pingchuan.api.dto.other.ElementTime;
import com.pingchuan.api.dto.other.Trapezoid;
import com.pingchuan.api.parameter.other.ForecastTimeParameter;
import com.pingchuan.api.parameter.other.NewestParameter;
import com.pingchuan.api.parameter.other.TrapezoidParameter;
import com.pingchuan.api.service.OtherService;
import com.pingchuan.api.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/otherSearch")
public class OtherController {

    @Autowired
    private OtherService otherService;

    @RequestMapping("/findNewestTime")
    @OtherAction(apiId = 30, isNeedTime = false)
    public ApiResponse findNewestTime(NewestParameter newest){
        List<ElementTime> elementTimes =  otherService.findNewestTime(newest);
        if (elementTimes.size() == 0) {
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);
        }
        return new ApiResponse(ResultCode.SUCCESS, "查询成功", elementTimes.get(0));
    }

    @RequestMapping("/findNewestTimeByDate")
    @OtherAction(apiId = 31, isNeedTime = true)
    public ApiResponse findNewestTimeByDate(NewestParameter newest){
        newest.setEndTime(addDay(newest.getStartTime(), 1));
        List<ElementTime> elementTimes =  otherService.findNewestTime(newest);
        if (elementTimes.size() == 0) {
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);
        }
        return new ApiResponse(ResultCode.SUCCESS, "查询成功", elementTimes.get(0));
    }

    @RequestMapping("/findNewestTimeByForecastTime")
    @OtherAction(apiId = 32, isNeedTime = true)
    public ApiResponse findNewestTimeByForecastTime(ForecastTimeParameter forecastTime){
        List<ElementTime> elementTimes =  otherService.findNewestTimeByForecastTime(forecastTime);
        if (elementTimes.size() == 0) {
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);
        }
        return new ApiResponse(ResultCode.SUCCESS, "查询成功", elementTimes.get(0));
    }

    @RequestMapping("/findAllTrapezoid")
    @OtherAction(apiId = 33, isNeedTime = false)
    public ApiResponse findAllTrapezoid(TrapezoidParameter trapezoid){
        List<Trapezoid> trapezoids = otherService.findAllTrapezoid(trapezoid);
        if (trapezoids.size() == 0) {
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);
        }
        return new ApiResponse(ResultCode.SUCCESS, "查询成功", trapezoids);
    }

    @RequestMapping("/findAllTrapezoidByAreaCode")
    @OtherAction(apiId = 34, isNeedTime = true)
    public ApiResponse findAllTrapezoidByAreaCode(TrapezoidParameter trapezoid){
        List<Trapezoid> trapezoids = otherService.findAllTrapezoid(trapezoid);
        if (trapezoids.size() == 0) {
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);
        }
        return new ApiResponse(ResultCode.SUCCESS, "查询成功", trapezoids);
    }

    private Date addDay(Date date, int day){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

}

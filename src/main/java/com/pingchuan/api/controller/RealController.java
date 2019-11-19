package com.pingchuan.api.controller;

import com.pingchuan.api.annotation.RealAction;
import com.pingchuan.api.contants.ResultCode;
import com.pingchuan.api.dto.real.Element;
import com.pingchuan.api.parameter.real.AreaParameter;
import com.pingchuan.api.parameter.real.LocationParameter;
import com.pingchuan.api.service.RealService;
import com.pingchuan.api.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/realSearch")
public class RealController {

    @Autowired
    private RealService realService;

    @RequestMapping("/findRealNJGridsByArea")
    @RealAction(apiId = 35, isNeedElementCode = true)
    public ApiResponse findRealNJGridsByArea(AreaParameter area){
        List<Element> areaElements = realService.findRealNJGridsByArea(area);
        if (areaElements.size() == 0) {
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);
        }
        return new ApiResponse(ResultCode.SUCCESS, "查询成功", areaElements);
    }

    @RequestMapping("/findRealNJGridsByAreaAllElement")
    @RealAction(apiId = 36, isNeedElementCode = false)
    public ApiResponse findRealNJGridsByAreaAllElement(AreaParameter area){
        List<Element> areaElements = realService.findRealNJGridsByArea(area);
        if (areaElements.size() == 0) {
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);
        }
        return new ApiResponse(ResultCode.SUCCESS, "查询成功", areaElements);
    }

    @RequestMapping("/findRealNJGridsByLocation")
    @RealAction(apiId = 37, isNeedElementCode = true)
    public ApiResponse findRealNJGridsByLocation(LocationParameter location){
        List<Element> elements = realService.findRealNJGridsByLocation(location);
        if (elements.size() == 0) {
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);
        }
        return new ApiResponse(ResultCode.SUCCESS, "查询成功", elements);
    }

    @RequestMapping("/findRealNJGridsByLocationAllElement")
    @RealAction(apiId = 38, isNeedElementCode = false)
    public ApiResponse findRealNJGridsByLocationAllElement(LocationParameter location){
        List<Element> elements = realService.findRealNJGridsByLocation(location);
        if (elements.size() == 0) {
            return new ApiResponse(ResultCode.NULL_VALUE, "未查询到值", null);
        }
        return new ApiResponse(ResultCode.SUCCESS, "查询成功", elements);
    }

}

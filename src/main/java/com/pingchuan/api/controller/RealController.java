package com.pingchuan.api.controller;

import com.pingchuan.api.annotation.RealAction;
import com.pingchuan.api.parameter.real.AreaParameter;
import com.pingchuan.api.service.RealService;
import com.pingchuan.api.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/real")
public class RealController {

    @Autowired
    private RealService realService;

    @RequestMapping("/findRealNJGridsByArea")
    @RealAction(apiId = 35, isNeedElementCode = true)
    public ApiResponse findRealNJGridsByArea(AreaParameter area){
        realService.findRealNJGridsByArea(area);
        return null;
    }

    @RequestMapping("/findRealNJGridsByAreaAllElement")
    @RealAction(apiId = 36, isNeedElementCode = false)
    public ApiResponse findRealNJGridsByAreaAllElement(AreaParameter area){
        realService.findRealNJGridsByArea(area);
        return null;
    }

}

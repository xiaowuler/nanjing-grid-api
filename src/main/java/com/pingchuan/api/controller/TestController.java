package com.pingchuan.api.controller;

import com.pingchuan.api.service.TestService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * @description: 测试控制层
 * @author: XW
 * @create: 2019-10-31 13:42
 **/
@RestController
@RequestMapping("test")
public class TestController {

    @Autowired
    private TestService testService;

    @RequestMapping("testSearch")
    public void testSearch(Date startTime, Date endTime){
        //testService.testSearch(startTime, endTime);
        testService.testRegion(startTime, endTime);
    }

    @RequestMapping("testSearchLine")
    public Document testLine(Date initialTime, Date startTime, Date endTime, double lon, double lat, String modeCode, String elementCode, String orgCode){
        //testService.testSearch(startTime, endTime);
        return testService.searchLine(initialTime, startTime, endTime, lon, lat, modeCode, elementCode, orgCode);
    }

    @RequestMapping("testSearchRegion")
    public Document testRegion(Date initialTime, Date forecastTime, double startLon, double endLon, double startLat, double endLat, String modeCode, String elementCode, String orgCode){
        //testService.testSearch(startTime, endTime);
        return testService.searchRegion(initialTime, forecastTime, startLon, endLon, startLat, endLat, modeCode, elementCode, orgCode);
    }

}

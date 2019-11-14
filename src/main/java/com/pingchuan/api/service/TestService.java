package com.pingchuan.api.service;

import org.bson.Document;

import java.util.Date;

public interface TestService {
    void testSearch(Date startTime, Date endTime);
    void testRegion(Date startTime, Date endTime);

    Document searchLine(Date initialTime, Date startTime, Date endTime, double lon, double lat, String modeCode, String elementCode, String orgCode);

    Document searchRegion(Date initialTime, Date forecastTime, double startLon, double endLon, double startLat, double endLat, String modeCode, String elementCode, String orgCode);
}

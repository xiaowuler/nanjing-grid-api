package com.pingchuan.api.dao;

import org.bson.Document;

import java.util.Date;
import java.util.List;

public interface TestDao {

    Document findLineBaseInfo(Date initialTime, double lon, double lat, String modeCode, String elementCode, String orgCode);

    List<Document> searchLine(Date initialTime, Date startTime, Date endTime, double lon, double lat, String modeCode, String elementCode, String orgCode);

    List<Document> searchLine(Date initialTime, long elementInfoId, String index, Date startTime, Date endTime);

    Document findRegionBaseInfo(Date initialTime, Date forecastTime, String modeCode, String elementCode, String orgCode);
}

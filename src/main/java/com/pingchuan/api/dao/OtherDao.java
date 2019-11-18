package com.pingchuan.api.dao;

import com.pingchuan.api.dto.other.ElementTime;
import com.pingchuan.api.dto.other.Trapezoid;

import java.util.Date;
import java.util.List;

public interface OtherDao {
    List<ElementTime> findNewestTime(String forecastModel);

    List<ElementTime> findNewestTime(String forecastModel, Date startTime, Date endTime);

    List<ElementTime> findNewestTimeByForecastTime(String forecastModel, Date forecastDate);

    List<Trapezoid> findAllTrapezoid(String areaCode);
}

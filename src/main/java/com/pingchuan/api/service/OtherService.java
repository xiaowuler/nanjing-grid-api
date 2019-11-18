package com.pingchuan.api.service;

import com.pingchuan.api.dto.other.ElementTime;
import com.pingchuan.api.dto.other.Trapezoid;
import com.pingchuan.api.parameter.other.ForecastTimeParameter;
import com.pingchuan.api.parameter.other.NewestParameter;
import com.pingchuan.api.parameter.other.TrapezoidParameter;

import java.util.List;

public interface OtherService {
    List<ElementTime> findNewestTime(NewestParameter newest);

    List<ElementTime> findNewestTimeByForecastTime(ForecastTimeParameter forecastTime);

    List<Trapezoid> findAllTrapezoid(TrapezoidParameter trapezoid);
}

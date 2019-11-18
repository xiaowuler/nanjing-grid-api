package com.pingchuan.api.dao;

import com.pingchuan.api.dto.base.Element;
import com.pingchuan.api.dto.base.LineInfo;
import com.pingchuan.api.dto.base.PointInfo;
import com.pingchuan.api.parameter.base.LineParameter;

import java.util.Date;
import java.util.List;

public interface ForecastValueDao {

    List<Element> findNJGridsByArea(Date updateTime, Date startTime, Date forecastTime, String areaCode, String elementCode, String forecastModel, double[] threshold, boolean isNeedElementCode);

    List<Element> findNJGridsByLocation(String elementCode, List<double[]> locations, Date startDate, Date updateDate, Date forecastDate, String forecastModel, double[] threshold, boolean isNeedElementCode);

    List<Element> findNJGridsByForecastTimeRange(LineParameter line);
}

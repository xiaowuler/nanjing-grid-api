package com.pingchuan.api.dao;


import com.pingchuan.api.dto.calc.CalcElementValue;

import java.util.Date;

public interface CalcForecastDao {

    CalcElementValue calcElementValueByArea(Date startDate, Date updateDate, Date forecastDate, String calcType, String areaCode, String forecastModel, String elementCode);

    CalcElementValue calcAvgElementValueByArea(Date startDate, Date updateDate, Date forecastDate, String calcType, String areaCode, String forecastModel, String elementCode);
}

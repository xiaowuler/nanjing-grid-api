package com.pingchuan.api.dao;


import com.pingchuan.api.dto.calc.CalcElementValue;
import com.pingchuan.api.dto.calc.TotalCalc;

import java.util.Date;
import java.util.List;

public interface CalcForecastDao {

    List<TotalCalc> calcElementValueByArea(Date startDate, Date updateDate, Date forecastDate, String calcType, String areaCode, String forecastModel, String elementCode);

    List<TotalCalc> calcAvgElementValueByArea(Date startDate, Date updateDate, Date forecastDate, String calcType, String areaCode, String forecastModel, String elementCode);

    List<TotalCalc> calcAvgElementValueByLocation(String calcType, Date startDate, Date updateDate, Date startForecastDate, Date endForecastDate, List<double[]> locations, String forecastModel, String elementCode);

    List<TotalCalc> calcElementValueByLocation(String calcType, Date startDate, Date updateDate, Date startForecastDate, Date endForecastDate, List<double[]> locations, String forecastModel, String elementCode);

    List<TotalCalc> findSumOrAvgByTimeInterval(int timeInterval, String elementCode, Date startDate, Date updateDate, String forecastModel, List<double[]> locations, String calcType, int totalHour);

    List<TotalCalc> findMaxOrMinByTimeInterval(int timeInterval, String elementCode, Date startDate, Date updateDate, String forecastModel, List<double[]> locations, String calcType, int totalHour);
}

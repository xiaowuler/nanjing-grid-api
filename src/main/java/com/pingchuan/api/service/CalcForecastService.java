package com.pingchuan.api.service;

import com.pingchuan.api.dto.calc.CalcElementValue;
import com.pingchuan.api.dto.calc.TotalCalc;
import com.pingchuan.api.parameter.calc.ForecastParameter;
import com.pingchuan.api.parameter.calc.TimeEffectParameter;
import com.pingchuan.api.parameter.calc.TimeRangeParameter;
import com.pingchuan.api.parameter.calc.TotalHourParameter;

import java.util.List;

public interface CalcForecastService {

    List<TotalCalc> calcElementValueByArea(ForecastParameter forecast);

    List<TotalCalc> calcElementValueTimeEffectByArea(TimeEffectParameter timeEffect);

    List<TotalCalc> calcElementValueTimeRangeByLocation(TimeRangeParameter timeRange);

    List<TotalCalc> findTotalByTimeInterval(TotalHourParameter totalHour);
}

package com.pingchuan.api.service.impl;

import com.pingchuan.api.contants.CalcType;
import com.pingchuan.api.dao.CalcForecastDao;
import com.pingchuan.api.dto.calc.CalcElementValue;
import com.pingchuan.api.dto.calc.Forecast;
import com.pingchuan.api.dto.calc.Location;
import com.pingchuan.api.dto.calc.TotalCalc;
import com.pingchuan.api.parameter.calc.ForecastParameter;
import com.pingchuan.api.parameter.calc.TimeEffectParameter;
import com.pingchuan.api.parameter.calc.TimeRangeParameter;
import com.pingchuan.api.parameter.calc.TotalHourParameter;
import com.pingchuan.api.service.CalcForecastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: 计算 服务 层
 * @author: XW
 * @create: 2019-11-13 16:06
 **/

@Service
@Transactional
public class CalcForecastServiceImpl implements CalcForecastService {

    @Autowired
    private CalcForecastDao calcForecastDao;

    @Override
    public List<TotalCalc> calcElementValueByArea(ForecastParameter forecast) {
        return getCalcElementValue(forecast.getCalcType(), forecast.getStartDate(), forecast.getUpdateDate(), forecast.getForecastDate(), forecast.getAreaCode(), forecast.getForecastModel(), forecast.getElementCode());
    }

    @Override
    public List<TotalCalc> calcElementValueTimeEffectByArea(TimeEffectParameter timeEffect) {
        return getCalcElementValue(timeEffect.getCalcType(), timeEffect.getStartDate(), timeEffect.getUpdateDate(), timeEffect.getForecastDate(), timeEffect.getAreaCode(), timeEffect.getForecastModel(), timeEffect.getElementCode());
    }

    @Override
    public List<TotalCalc> calcElementValueTimeRangeByLocation(TimeRangeParameter timeRange) {
        if (CalcType.avg.equals(timeRange.getCalcType())) {
            return calcForecastDao.calcAvgElementValueByLocation(timeRange.getCalcType(), timeRange.getStartDate(), timeRange.getUpdateDate(), timeRange.getStartForecastDate(),timeRange.getEndForecastDate(), timeRange.getLocations(), timeRange.getForecastModel(), timeRange.getElementCode());
        }
        return calcForecastDao.calcElementValueByLocation(timeRange.getCalcType(), timeRange.getStartDate(), timeRange.getUpdateDate(), timeRange.getStartForecastDate(),timeRange.getEndForecastDate(), timeRange.getLocations(), timeRange.getForecastModel(), timeRange.getElementCode());
    }

    @Override
    public List<TotalCalc> findTotalByTimeInterval(TotalHourParameter totalHour) {
        if (CalcType.avg.equals(totalHour.getCalcType()) || CalcType.sum.equals(totalHour.getCalcType())){
            return calcForecastDao.findSumOrAvgByTimeInterval(totalHour.getTimeInterval(), totalHour.getElementCode(), totalHour.getStartDate(), totalHour.getUpdateDate(), totalHour.getForecastModel(), totalHour.getLocations(), totalHour.getCalcType(), totalHour.getTotalHour());
        }

        return calcForecastDao.findMaxOrMinByTimeInterval(totalHour.getTimeInterval(), totalHour.getElementCode(), totalHour.getStartDate(), totalHour.getUpdateDate(), totalHour.getForecastModel(), totalHour.getLocations(), totalHour.getCalcType(), totalHour.getTotalHour());
    }

    private List<TotalCalc> getCalcElementValue(String calcType, Date startDate, Date updateDate, Date forecastDate, String areaCode, String forecastModel, String elementCode) {
        if ("avg".equals(calcType)) {
            return calcForecastDao.calcAvgElementValueByArea(startDate, updateDate, forecastDate, calcType, areaCode, forecastModel, elementCode);
        }
        return calcForecastDao.calcElementValueByArea(startDate, updateDate, forecastDate, calcType, areaCode, forecastModel, elementCode);
    }
}

package com.pingchuan.api.service.impl;

import com.pingchuan.api.dao.CalcForecastDao;
import com.pingchuan.api.dto.calc.CalcElementValue;
import com.pingchuan.api.parameter.calc.ForecastParameter;
import com.pingchuan.api.parameter.calc.TimeEffectParameter;
import com.pingchuan.api.service.CalcForecastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public CalcElementValue calcElementValueByArea(ForecastParameter forecast) {
        if ("avg".equals(forecast.getCalcType()))
            return calcForecastDao.calcAvgElementValueByArea(forecast.getStartDate(), forecast.getUpdateDate(), forecast.getForecastDate(), forecast.getCalcType(), forecast.getAreaCode(), forecast.getForecastModel(), forecast.getElementCode());
        return calcForecastDao.calcElementValueByArea(forecast.getStartDate(), forecast.getUpdateDate(), forecast.getForecastDate(), forecast.getCalcType(), forecast.getAreaCode(), forecast.getForecastModel(), forecast.getElementCode());
    }

    @Override
    public CalcElementValue calcElementValueTimeEffectByArea(TimeEffectParameter timeEffect) {
        if ("avg".equals(timeEffect.getCalcType()))
            return calcForecastDao.calcAvgElementValueByArea(timeEffect.getStartDate(), timeEffect.getUpdateDate(), timeEffect.getForecastDate(), timeEffect.getCalcType(), timeEffect.getAreaCode(), timeEffect.getForecastModel(), timeEffect.getElementCode());
        return calcForecastDao.calcElementValueByArea(timeEffect.getStartDate(), timeEffect.getUpdateDate(), timeEffect.getForecastDate(), timeEffect.getCalcType(), timeEffect.getAreaCode(), timeEffect.getForecastModel(), timeEffect.getElementCode());

    }

}

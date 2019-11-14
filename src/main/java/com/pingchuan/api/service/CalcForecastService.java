package com.pingchuan.api.service;

import com.pingchuan.api.dto.calc.CalcElementValue;
import com.pingchuan.api.parameter.calc.ForecastParameter;
import com.pingchuan.api.parameter.calc.TimeEffectParameter;

public interface CalcForecastService {

    CalcElementValue calcElementValueByArea(ForecastParameter forecast);

    CalcElementValue calcElementValueTimeEffectByArea(TimeEffectParameter timeEffect);
}

package com.pingchuan.api.service.impl;

import com.pingchuan.api.dao.OtherDao;
import com.pingchuan.api.dto.other.ElementTime;
import com.pingchuan.api.dto.other.Trapezoid;
import com.pingchuan.api.parameter.other.ForecastTimeParameter;
import com.pingchuan.api.parameter.other.NewestParameter;
import com.pingchuan.api.parameter.other.TrapezoidParameter;
import com.pingchuan.api.service.OtherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional
public class OtherServiceImpl implements OtherService {

    @Autowired
    private OtherDao otherDao;

    @Override
    public List<ElementTime> findNewestTime(NewestParameter newest) {

        if(StringUtils.isEmpty(newest.getStartTime()) || StringUtils.isEmpty(newest.getStartTime())){
            return otherDao.findNewestTime(newest.getForecastModel());
        }

        return otherDao.findNewestTime(newest.getForecastModel(), newest.getStartTime(), newest.getEndTime());
    }

    @Override
    public List<ElementTime> findNewestTimeByForecastTime(ForecastTimeParameter forecastTime) {
        return otherDao.findNewestTimeByForecastTime(forecastTime.getForecastModel(), forecastTime.getForecastDate());
    }

    @Override
    public List<Trapezoid> findAllTrapezoid(TrapezoidParameter trapezoid) {
        return otherDao.findAllTrapezoid(trapezoid.getAreaCode());
    }
}

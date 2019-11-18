package com.pingchuan.api.service.impl;

import com.pingchuan.api.dao.ForecastValueDao;
import com.pingchuan.api.dto.base.Element;
import com.pingchuan.api.dto.base.LineInfo;
import com.pingchuan.api.dto.base.PointInfo;
import com.pingchuan.api.dto.base.ThresholdInfo;
import com.pingchuan.api.parameter.base.*;
import com.pingchuan.api.service.ForecastValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 预报数据实现类
 * @author: XW
 * @create: 2019-11-07 15:32
 **/

@Service
public class ForecastValueServiceImpl implements ForecastValueService {

    @Autowired
    private ForecastValueDao forecastValueDao;

    @Override
    public List<Element> findNJGridsByArea(AreaParameter pointByArea) {
        return forecastValueDao.findNJGridsByArea(pointByArea.getUpdateDate(), pointByArea.getStartDate(), pointByArea.getForecastDate(), pointByArea.getAreaCode(), pointByArea.getElementCode().toUpperCase(), pointByArea.getForecastModel().toUpperCase(), null, pointByArea.isNeedElementCode());
    }

    @Override
    public List<Element> findNJGridsByAreaAllElement(AreaParameter pointByArea) {
        return forecastValueDao.findNJGridsByArea(pointByArea.getUpdateDate(), pointByArea.getStartDate(), pointByArea.getForecastDate(), pointByArea.getAreaCode(), "", pointByArea.getForecastModel().toUpperCase(),null, pointByArea.isNeedElementCode());
    }

    @Override
    public List<Element> findNJGridsByLocation(LocationParameter location) {
        return forecastValueDao.findNJGridsByLocation(location.getElementCode(), location.getLocations(), location.getStartDate(), location.getUpdateDate(), location.getForecastDate(), location.getForecastModel(), null, location.isNeedElementCode());
    }

    @Override
    public List<Element> findNJGridsByForecastTimeRange(LineParameter line) {
        return forecastValueDao.findNJGridsByForecastTimeRange(line);
    }

    @Override
    public List<Element> findNJGridsByTimeEffect(TimeEffectParameter area) {
        return forecastValueDao.findNJGridsByArea(area.getUpdateDate(), area.getStartDate(), area.getForecastDate(), area.getAreaCode(), area.getElementCode().toUpperCase(), area.getForecastModel().toUpperCase(),null, area.isNeedElementCode());
    }

    @Override
    public List<Element> findNJGridsByTimeEffectAllElement(TimeEffectParameter area) {
        return forecastValueDao.findNJGridsByArea(area.getUpdateDate(), area.getStartDate(), area.getForecastDate(), area.getAreaCode(), null, area.getForecastModel().toUpperCase(),null, area.isNeedElementCode());
    }

    @Override
    public List<Element> findNJGridsByElementThresholdArea(ThresholdAreaParameter thresholdArea) {
        List<Element> elementList = new ArrayList<>();
        List<ThresholdInfo> thresholdInfos = new ArrayList<>();
        for(double[] threshold : thresholdArea.getThresholdValues()) {
            if (StringUtils.isEmpty(threshold)) {
                continue;
            }

            List<Element> elements = forecastValueDao.findNJGridsByArea(thresholdArea.getUpdateDate(), thresholdArea.getStartDate(), thresholdArea.getForecastDate(), thresholdArea.getAreaCode(), thresholdArea.getElementCode(), thresholdArea.getForecastModel().toUpperCase(), null, thresholdArea.isNeedElementCode());
            if (elements.size() > 0) {
                elements.get(0).setThreshold(threshold);
                elementList.add(elements.get(0));
            }
        }

        return elementList;
    }

    @Override
    public List<Element> findNJGridsByElementThresholdLocation(ThresholdLocationParameter thresholdLocation) {
        List<Element> elementList = new ArrayList<>();
        for(double[] threshold : thresholdLocation.getThresholdValues()) {
            if (StringUtils.isEmpty(threshold)) {
            }

            List<Element> elements = forecastValueDao.findNJGridsByLocation(thresholdLocation.getElementCode(), thresholdLocation.getLocations(), thresholdLocation.getStartDate(), thresholdLocation.getUpdateDate(), thresholdLocation.getForecastDate(), thresholdLocation.getForecastModel(), threshold, thresholdLocation.isNeedElementCode());
            if (elements.size() > 0) {
                elements.get(0).setThreshold(threshold);
                elementList.add(elements.get(0));
            }
        }

        return elementList;
    }

    private ThresholdInfo createThresholdInfo(PointInfo pointInfo, double[] threshold){
        ThresholdInfo thresholdInfo = new ThresholdInfo();
        thresholdInfo.setElements(pointInfo);
        thresholdInfo.setThreshold(threshold);
        return thresholdInfo;
    }
}

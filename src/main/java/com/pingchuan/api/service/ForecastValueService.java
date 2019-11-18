package com.pingchuan.api.service;

import com.pingchuan.api.dto.base.Element;
import com.pingchuan.api.dto.base.LineInfo;
import com.pingchuan.api.dto.base.PointInfo;
import com.pingchuan.api.dto.base.ThresholdInfo;
import com.pingchuan.api.parameter.base.*;

import java.util.List;

public interface ForecastValueService {

    //根据更新时间、起报时间查询指定地区指定要素指定预报时间的预报数据
    List<Element> findNJGridsByArea(AreaParameter pointByArea);

    List<Element> findNJGridsByAreaAllElement(AreaParameter pointByArea);

    List<Element> findNJGridsByLocation(LocationParameter location);

    List<Element> findNJGridsByForecastTimeRange(LineParameter line);

    List<Element> findNJGridsByTimeEffect(TimeEffectParameter area);

    List<Element> findNJGridsByTimeEffectAllElement(TimeEffectParameter area);

    List<Element> findNJGridsByElementThresholdArea(ThresholdAreaParameter thresholdArea);

    List<Element> findNJGridsByElementThresholdLocation(ThresholdLocationParameter thresholdLocation);
}

package com.pingchuan.api.service;

import com.pingchuan.api.dto.base.LineInfo;
import com.pingchuan.api.dto.base.PointInfo;
import com.pingchuan.api.dto.base.ThresholdInfo;
import com.pingchuan.api.parameter.base.*;

import java.util.List;

public interface ForecastValueService {

    //根据更新时间、起报时间查询指定地区指定要素指定预报时间的预报数据
    PointInfo findNJGridsByArea(AreaParameter pointByArea);

    PointInfo findNJGridsByAreaAllElement(AreaParameter pointByArea);

    PointInfo findNJGridsByLocation(LocationParameter location);

    LineInfo findNJGridsByForecastTimeRange(LineParameter line);

    PointInfo findNJGridsByTimeEffect(TimeEffectParameter area);

    PointInfo findNJGridsByTimeEffectAllElement(TimeEffectParameter area);

    List<ThresholdInfo> findNJGridsByElementThresholdArea(ThresholdAreaParameter thresholdArea);

    List<ThresholdInfo> findNJGridsByElementThresholdLocation(ThresholdLocationParameter thresholdLocation);
}

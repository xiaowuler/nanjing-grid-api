package com.pingchuan.api.service;

import com.pingchuan.api.dto.real.Element;
import com.pingchuan.api.parameter.real.AreaParameter;
import com.pingchuan.api.parameter.real.LocationParameter;

import java.util.List;

public interface RealService {
    List<Element> findRealNJGridsByArea(AreaParameter area);

    List<Element> findRealNJGridsByLocation(LocationParameter location);
}

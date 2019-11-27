package com.pingchuan.api.service.impl;

import com.pingchuan.api.dao.BaseSearchDao;
import com.pingchuan.api.parameter.base.AreaParameter;
import com.pingchuan.api.service.BaseSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BaseSearchServiceImpl implements BaseSearchService {

    @Autowired
    private BaseSearchDao baseSearchDao;

    @Override
    public void findNJGridsByArea(AreaParameter areaParameter) {
        baseSearchDao.findNJGridsByArea(areaParameter.getStartDate(), areaParameter.getUpdateDate(), areaParameter.getElementCode());
    }
}

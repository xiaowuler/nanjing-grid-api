package com.pingchuan.api.service.impl;

import com.pingchuan.api.contants.TimeFormat;
import com.pingchuan.api.dao.RealDao;
import com.pingchuan.api.parameter.real.AreaParameter;
import com.pingchuan.api.service.RealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class RealServiceImpl implements RealService {

    @Autowired
    private RealDao realDao;

    @Override
    public void findRealNJGridsByArea(AreaParameter area) {
        List<String> collections = getCollectionByTimeRange(area.getStartRealDate(), area.getEndRealDate());
        for(String collection : collections){
            realDao.findRealNJGridsByArea(collection, area.getAreaCode(), area.getStartRealDate(), area.getEndRealDate(), area.getElementCode());
        }
    }

    private List<String> getCollectionByTimeRange(Date startTime, Date endTime){
        List<String> collections = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TimeFormat.REAL_COLLECTION_NAME);
        startTime = addMonth(startTime, -1);
        while (startTime.compareTo(endTime) == -1){
            startTime = addMonth(startTime, 1);
            collections.add(simpleDateFormat.format(startTime));
        }
        return collections;
    }

    private Date addMonth(Date date, int month){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, month);
        return calendar.getTime();
    }
}

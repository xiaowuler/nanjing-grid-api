package com.pingchuan.api.service.impl;

import com.pingchuan.api.contants.TimeFormat;
import com.pingchuan.api.dao.RealDao;
import com.pingchuan.api.dto.real.Element;
import com.pingchuan.api.dto.real.Location;
import com.pingchuan.api.dto.real.Real;
import com.pingchuan.api.parameter.real.AreaParameter;
import com.pingchuan.api.parameter.real.LocationParameter;
import com.pingchuan.api.service.RealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RealServiceImpl implements RealService {

    @Autowired
    private RealDao realDao;

    @Override
    public List<Element> findRealNJGridsByArea(AreaParameter area) {
        List<Element> areaElements = new ArrayList<>();
        List<String> collections = getCollectionByTimeRange(area.getStartRealDate(), area.getEndRealDate());
        for(String collection : collections){
            List<Element> realNJGridsByArea = realDao.findRealNJGridsByArea(collection, area.getAreaCode(), area.getStartRealDate(), area.getEndRealDate(), area.getElementCode());
            areaElements = mergeAreaElements(areaElements, realNJGridsByArea);
        }

        return areaElements;
    }

    @Override
    public List<Element> findRealNJGridsByLocation(LocationParameter location) {
        List<Element> elements = new ArrayList<>();
        for(String collection : getCollectionByTimeRange(location.getStartRealDate(), location.getEndRealDate())){
            List<Element> realNJGridsByLocation = realDao.findRealNJGridsByLocation(collection, location.getLocations(), location.getStartRealDate(), location.getEndRealDate(), location.getElementCode());
            elements = mergeAreaElements(elements, realNJGridsByLocation);
        }

        return elements;
    }

    private List<Element> mergeLocationElements(List<Element> areaElements, List<Element> searchList){
        if (areaElements.size() == 0){
            areaElements.addAll(searchList);
            return areaElements;
        }

        for (Element areaElement : areaElements){
            List<Element> elementCodes = searchList.stream().filter(a -> a.getElementCode() == areaElement.getElementCode()).collect(Collectors.toList());
            if (elementCodes.size() == 0){
                continue;
            }

            List<Real> reals = elementCodes.get(0).getReals();
            for(Real real : areaElement.getReals()){

                List<Real> realList = reals.stream().filter(r -> r.getRealTime() == real.getRealTime()).collect(Collectors.toList());
                if (realList.size() == 0){
                    continue;
                }

                real.getLocations().addAll(realList.get(0).getLocations());
            }
        }

        return areaElements;
    }

    private List<Element> mergeAreaElements(List<Element> areaElements, List<Element> searchList){
        if (areaElements.size() == 0){
            areaElements.addAll(searchList);
            return areaElements;
        }

        for (Element areaElement : areaElements){
            List<Element> elementCodes = searchList.stream().filter(a -> a.getElementCode() == areaElement.getElementCode()).collect(Collectors.toList());
            if (elementCodes.size() == 0){
                continue;
            }

            List<Location> locations = elementCodes.get(0).getLocations();
            for(Location location : areaElement.getLocations()){

                List<Location> locs = locations.stream().filter(l -> l.getLoc() == location.getLoc()).collect(Collectors.toList());
                if (locs.size() == 0){
                    continue;
                }

                location.getReals().addAll(locs.get(0).getReals());
            }
        }

        return areaElements;
    }

    private List<String> getCollectionByTimeRange(Date startTime, Date endTime){
        List<String> collections = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TimeFormat.REAL_COLLECTION_NAME);
        while (compareMonth(startTime, endTime)){
            collections.add(simpleDateFormat.format(startTime));
            startTime = addMonth(startTime, 1);
        }
        return collections;
    }

    private boolean compareMonth(Date startTime, Date endTime){
        if (startTime.getYear() > endTime.getYear()){
            return false;
        }else if (startTime.getYear() < endTime.getYear()){
            return true;
        }

        if (startTime.getMonth() > endTime.getMonth()){
            return false;
        }

        return true;
    }

    private Date addMonth(Date date, int month){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, month);
        return calendar.getTime();
    }
}

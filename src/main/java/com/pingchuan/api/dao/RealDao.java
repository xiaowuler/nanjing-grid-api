package com.pingchuan.api.dao;

import com.pingchuan.api.dto.real.Element;

import java.util.Date;
import java.util.List;

public interface RealDao {

    List<Element> findRealNJGridsByArea(String collection, String areaCode, Date startRealDate, Date endRealDate, String elementCode);

    List<Element> findRealNJGridsByLocation(String collection, List<double[]> locations, Date startRealDate, Date endRealDate, String elementCode);
}

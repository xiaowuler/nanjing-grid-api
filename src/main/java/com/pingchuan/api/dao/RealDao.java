package com.pingchuan.api.dao;

import java.util.Date;

public interface RealDao {

    void findRealNJGridsByArea(String collection, String areaCode, Date startRealDate, Date endRealDate, String elementCode);
}

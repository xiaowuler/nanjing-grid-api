package com.pingchuan.api.dao;

import java.util.Date;

public interface BaseSearchDao {
    void findNJGridsByArea(Date startDate, Date updateDate, String elementCode);
}

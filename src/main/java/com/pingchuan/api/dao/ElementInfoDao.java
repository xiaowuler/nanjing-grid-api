package com.pingchuan.api.dao;

import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

import java.util.Date;
import java.util.List;

public interface ElementInfoDao {

    List<AggregationOperation> findByStartTimeAndUpdateTimeAndElementCode(Date startTime, Date updateTime, String elementCode);

}

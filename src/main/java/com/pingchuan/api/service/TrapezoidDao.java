package com.pingchuan.api.service;

import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

import java.util.List;

public interface TrapezoidDao {

    List<AggregationOperation> findByArea(String areaCode);

}

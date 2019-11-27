package com.pingchuan.api.dao.impl;

import com.pingchuan.api.dao.ElementInfoDao;
import com.pingchuan.api.util.AggregationUtil;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class ElementInfoDaoImpl implements ElementInfoDao {

    @Override
    public List<AggregationOperation> findByStartTimeAndUpdateTimeAndElementCode(Date startTime, Date updateTime, String elementCode) {

        LookupOperation lookupOperation = Aggregation.lookup("element_info", "_id", "element_code", "element_info");
        UnwindOperation unwindOperation = Aggregation.unwind("element_info");
        MatchOperation matchOperation = Aggregation.match(Criteria.where("element_info.update_time").is(updateTime).and("element_info.start_time").is(startTime).and("element_info.element_code").is(elementCode));
        ProjectionOperation projectionOperation = Aggregation.project("element_info.start_time", "element_info.update_time", "element_info.element_code","element_info.forecast_model", "element_info.trapezoid_info_id").and("_id").as("element_info_id").andExclude("_id");

        return AggregationUtil.SetAggregationOperation(lookupOperation, unwindOperation, matchOperation, projectionOperation);
    }
}

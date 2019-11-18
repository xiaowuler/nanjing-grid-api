package com.pingchuan.api.dao.impl;

import com.pingchuan.api.dao.RealDao;
import com.pingchuan.api.util.AggregationUtil;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class RealDaoImpl implements RealDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void findRealNJGridsByArea(String collection, String areaCode, Date startRealDate, Date endRealDate, String elementCode) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();

        MatchOperation trapezoidMatch = Aggregation.match(Criteria.where("area_code").is(areaCode));
        aggregationOperations.add(trapezoidMatch);

        LookupOperation realInfoLookup = Aggregation.lookup("real_infos", "trapezoid_info_id", "trapezoid_info_id", "real_infos");
        UnwindOperation realInfoUnwind = Aggregation.unwind("real_infos");
        MatchOperation realInfoMatch = Aggregation.match(Criteria.where("real_infos.is_finished").is(true).andOperator(Criteria.where("real_infos.real_time").lt(endRealDate), Criteria.where("real_infos.real_time").gte(startRealDate)));
        ProjectionOperation realInfoProject = Aggregation.project("real_infos.real_time", "real_infos.element_code", "loc", "grid_code", "area_code", "area_name").and("real_infos._id").concat("$grid_code").as("element_value_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(realInfoLookup, realInfoUnwind, realInfoMatch, realInfoProject));

        LookupOperation elementLookup = Aggregation.lookup("elements", "element_code", "_id", "elements");
        UnwindOperation elementUnwind = Aggregation.unwind("elements");
        ProjectionOperation elementProject = Aggregation.project("real_time", "element_code", "loc", "grid_code", "area_code", "area_name", "element_value_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(elementLookup, elementUnwind, getElementMatch(elementCode), elementProject));

        LookupOperation elementValueLookup = Aggregation.lookup(collection, "element_value_id", "_id", "element_values");
        UnwindOperation elementValueUnwind = Aggregation.unwind("element_values");
        ProjectionOperation elementValueProject = Aggregation.project("real_time", "element_code", "loc", "grid_code", "area_code", "area_name", "element_values.value").andExclude("_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(elementValueLookup, elementValueUnwind, null, elementValueProject));

        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
        List<Document> documents = mongoTemplate.aggregate(aggregation, "trapezoids", Document.class).getMappedResults();
        return;

    }

    private MatchOperation getElementMatch(String elementCode){
        if (StringUtils.isEmpty(elementCode)){
            return null;
        }

        return Aggregation.match(Criteria.where("elements.element_code").is(elementCode));
    }
}

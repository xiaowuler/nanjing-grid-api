package com.pingchuan.api.dao.impl;

import com.pingchuan.api.dao.RealDao;
import com.pingchuan.api.domain.Trapezoid;
import com.pingchuan.api.dto.real.Element;
import com.pingchuan.api.util.AggregationUtil;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.bind;

@Component
public class RealDaoImpl implements RealDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Element> findRealNJGridsByArea(String collection, String areaCode, Date startRealDate, Date endRealDate, String elementCode) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();

        MatchOperation trapezoidMatch = Aggregation.match(Criteria.where("area_code").is(areaCode));
        aggregationOperations.add(trapezoidMatch);

        LookupOperation realInfoLookup = Aggregation.lookup("real_infos", "trapezoid_info_id", "trapezoid_info_id", "real_infos");
        UnwindOperation realInfoUnwind = Aggregation.unwind("real_infos");
        MatchOperation realInfoMatch = Aggregation.match(Criteria.where("real_infos.is_finished").is(true).and("real_infos.real_time").gte(startRealDate).lte(endRealDate));
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

        Fields realFields = Fields.fields("element_code", "loc", "grid_code", "area_code", "area_name");
        aggregationOperations.add(Aggregation.project(realFields).and("real").nested(bind("real_time", "real_time").and("value")));
        aggregationOperations.add(Aggregation.group(realFields).push("real").as("reals"));

        Fields locationFields = Fields.fields("element_code");
        aggregationOperations.add(Aggregation.project(locationFields).and("location").nested(bind("loc", "loc").and("grid_code").and("area_code").and("area_name").and("reals")));
        aggregationOperations.add(Aggregation.group("element_code").push("location").as("locations"));

        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
        List<Element> areaElements = mongoTemplate.aggregate(aggregation, "trapezoids", Element.class).getMappedResults();
        return areaElements;

    }

    @Override
    public List<Element> findRealNJGridsByLocation(String collection, List<double[]> locations, Date startRealDate, Date endRealDate, String elementCode) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();

        MatchOperation trapezoidMatch = Aggregation.match(Criteria.where("_id").in(findAllTrapezoidIdByLocation(locations)));
        aggregationOperations.add(trapezoidMatch);

        LookupOperation realInfoLookup = Aggregation.lookup("real_infos", "trapezoid_info_id", "trapezoid_info_id", "real_infos");
        UnwindOperation realInfoUnwind = Aggregation.unwind("real_infos");
        MatchOperation realInfoMatch = Aggregation.match(Criteria.where("real_infos.is_finished").is(true).and("real_infos.real_time").gte(startRealDate).lte(endRealDate));
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

        Fields locationFields = Fields.fields("element_code", "real_time");
        aggregationOperations.add(Aggregation.project(locationFields).and("location").nested(bind("loc", "loc").and("grid_code").and("area_code").and("area_name").and("value")));
        aggregationOperations.add(Aggregation.group(locationFields).push("location").as("locations"));

        Fields realFields = Fields.fields("element_code");
        aggregationOperations.add(Aggregation.project(realFields).and("real").nested(bind("real_time", "real_time").and("locations")));
        aggregationOperations.add(Aggregation.group(realFields).push("real").as("reals"));

        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
        List<Element> areaElements = mongoTemplate.aggregate(aggregation, "trapezoids", Element.class).getMappedResults();

        return areaElements;
    }

    private List<ObjectId> findAllTrapezoidIdByLocation(List<double[]> locations){
        List<ObjectId> trapezoidIds = new ArrayList<>();
        for (double[] loc : locations) {
            if (StringUtils.isEmpty(loc)) {
                continue;
            }

            List<AggregationOperation> aggregationOperations = new ArrayList<>();
            aggregationOperations.add(Aggregation.geoNear(NearQuery.near(loc[0], loc[1]).spherical(true), "distance"));
            aggregationOperations.add(Aggregation.limit(1));
            aggregationOperations.add(Aggregation.project("_id"));

            Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
            List<Trapezoid> trapezoids = mongoTemplate.aggregate(aggregation, "trapezoids", Trapezoid.class).getMappedResults();
            trapezoids.forEach(t -> trapezoidIds.add(t.getId()));
        }

        return trapezoidIds;
    }

    private MatchOperation getElementMatch(String elementCode){
        if (StringUtils.isEmpty(elementCode)){
            return null;
        }

        return Aggregation.match(Criteria.where("elements.element_code").is(elementCode));
    }
}

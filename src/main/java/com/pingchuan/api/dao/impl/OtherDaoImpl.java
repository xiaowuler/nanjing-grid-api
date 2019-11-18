package com.pingchuan.api.dao.impl;

import com.pingchuan.api.dao.OtherDao;
import com.pingchuan.api.dto.other.ElementTime;
import com.pingchuan.api.dto.other.Trapezoid;
import com.pingchuan.api.util.AggregationUtil;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Field;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.bind;

@Component
public class OtherDaoImpl implements OtherDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<ElementTime> findNewestTime(String forecastModel) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        LookupOperation elementInfoLookup = Aggregation.lookup("element_infos", "_id", "element_code", "element_infos");
        UnwindOperation elementInfoUnwind = Aggregation.unwind("element_infos");
        MatchOperation elementInfoMatch = Aggregation.match(Criteria.where("element_infos.forecast_model").is(forecastModel));
        ProjectionOperation elementInfoProject = Aggregation.project("element_infos.start_time", "element_infos.update_time", "element_infos.element_code", "element_infos.forecast_model", "element_infos._id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(elementInfoLookup, elementInfoUnwind, elementInfoMatch, elementInfoProject));

        LookupOperation forecastInfoLookup = Aggregation.lookup("forecast_infos", "_id", "element_info_id", "forecast_infos");
        UnwindOperation forecastInfosUnwind = Aggregation.unwind("forecast_infos");
        ProjectionOperation forecastInfoProject = Aggregation.project("start_time", "update_time", "element_code", "forecast_model", "forecast_infos.forecast_time", "forecast_infos.time_effect").andExclude("_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(forecastInfoLookup, forecastInfosUnwind, null, forecastInfoProject));

        Fields forecastFields = Fields.fields("start_time", "update_time", "element_code", "forecast_model");
        aggregationOperations.add(Aggregation.project(forecastFields).and("forecast").nested(bind("forecast_time", "forecast_time").and("time_effect")));
        aggregationOperations.add(Aggregation.group(forecastFields).push("forecast").as("forecasts"));

        aggregationOperations.add(Aggregation.sort(Sort.Direction.DESC, "update_time"));
        Fields updateTimeFields = Fields.fields("start_time", "element_code", "forecast_model");
        aggregationOperations.add(Aggregation.project(updateTimeFields).and("update_time").nested(bind("forecasts", "forecasts").and("update_time")));
        aggregationOperations.add(Aggregation.group(updateTimeFields).first("update_time").as("update_time"));aggregationOperations.add(Aggregation.sort(Sort.Direction.DESC, "update_time"));

        Fields startTimeFields = Fields.fields("element_code", "forecast_model");
        aggregationOperations.add(Aggregation.project(startTimeFields).and("start_time").nested(bind("start_time", "start_time").and("update_time")));
        aggregationOperations.add(Aggregation.group(startTimeFields).first("start_time").as("start_time"));

        Fields elementCodeFields = Fields.fields("forecast_model");
        aggregationOperations.add(Aggregation.project(elementCodeFields).and("element_code").nested(bind("element_code", "element_code").and("start_time")));
        aggregationOperations.add(Aggregation.group(elementCodeFields).push("element_code").as("element_codes"));

        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
        List<ElementTime> elementTimes = mongoTemplate.aggregate(aggregation, "elements", ElementTime.class).getMappedResults();
        return elementTimes;
    }

    @Override
    public List<ElementTime> findNewestTime(String forecastModel, Date startTime, Date endTime) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        LookupOperation elementInfoLookup = Aggregation.lookup("element_infos", "_id", "element_code", "element_infos");
        UnwindOperation elementInfoUnwind = Aggregation.unwind("element_infos");
        MatchOperation elementInfoMatch = Aggregation.match(Criteria.where("element_infos.forecast_model").is(forecastModel).and("element_infos.start_time").gte(startTime).lte(endTime));
        ProjectionOperation elementInfoProject = Aggregation.project("element_infos.start_time", "element_infos.update_time", "element_infos.element_code", "element_infos.forecast_model", "element_infos._id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(elementInfoLookup, elementInfoUnwind, elementInfoMatch, elementInfoProject));

        LookupOperation forecastInfoLookup = Aggregation.lookup("forecast_infos", "_id", "element_info_id", "forecast_infos");
        UnwindOperation forecastInfosUnwind = Aggregation.unwind("forecast_infos");
        ProjectionOperation forecastInfoProject = Aggregation.project("start_time", "update_time", "element_code", "forecast_model", "forecast_infos.forecast_time", "forecast_infos.time_effect").andExclude("_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(forecastInfoLookup, forecastInfosUnwind, null, forecastInfoProject));

        Fields forecastFields = Fields.fields("start_time", "update_time", "element_code", "forecast_model");
        aggregationOperations.add(Aggregation.project(forecastFields).and("forecast").nested(bind("forecast_time", "forecast_time").and("time_effect")));
        aggregationOperations.add(Aggregation.group(forecastFields).push("forecast").as("forecasts"));

        Fields updateTimeFields = Fields.fields("start_time", "element_code", "forecast_model");
        aggregationOperations.add(Aggregation.project(updateTimeFields).and("update_time").nested(bind("forecasts", "forecasts").and("update_time")));
        aggregationOperations.add(Aggregation.group(updateTimeFields).push("update_time").as("update_times"));

        Fields startTimeFields = Fields.fields("element_code", "forecast_model");
        aggregationOperations.add(Aggregation.project(startTimeFields).and("start_time").nested(bind("start_time", "start_time").and("update_times")));
        aggregationOperations.add(Aggregation.group(startTimeFields).push("start_time").as("start_times"));

        Fields elementCodeFields = Fields.fields("forecast_model");
        aggregationOperations.add(Aggregation.project(elementCodeFields).and("element_code").nested(bind("element_code", "element_code").and("start_times")));
        aggregationOperations.add(Aggregation.group(elementCodeFields).push("element_code").as("element_codes"));

        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
        List<ElementTime> elementTimes = mongoTemplate.aggregate(aggregation, "elements", ElementTime.class).getMappedResults();
        return elementTimes;
    }

    @Override
    public List<ElementTime> findNewestTimeByForecastTime(String forecastModel, Date forecastDate) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        LookupOperation elementInfoLookup = Aggregation.lookup("element_infos", "_id", "element_code", "element_infos");
        UnwindOperation elementInfoUnwind = Aggregation.unwind("element_infos");
        MatchOperation elementInfoMatch = Aggregation.match(Criteria.where("element_infos.forecast_model").is(forecastModel));
        ProjectionOperation elementInfoProject = Aggregation.project("element_infos.start_time", "element_infos.update_time", "element_infos.element_code", "element_infos.forecast_model", "element_infos._id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(elementInfoLookup, elementInfoUnwind, elementInfoMatch, elementInfoProject));

        LookupOperation forecastInfoLookup = Aggregation.lookup("forecast_infos", "_id", "element_info_id", "forecast_infos");
        UnwindOperation forecastInfosUnwind = Aggregation.unwind("forecast_infos");
        MatchOperation forecastInfoMatch = Aggregation.match(Criteria.where("forecast_infos.forecast_time").is(forecastDate));
        ProjectionOperation forecastInfoProject = Aggregation.project("start_time", "update_time", "element_code", "forecast_model", "forecast_infos.forecast_time", "forecast_infos.time_effect").andExclude("_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(forecastInfoLookup, forecastInfosUnwind, forecastInfoMatch, forecastInfoProject));

//        Fields forecastFields = Fields.fields("start_time", "update_time", "element_code", "forecast_model");
//        aggregationOperations.add(Aggregation.project(forecastFields).and("forecast").nested(bind("forecast_time", "forecast_time").and("time_effect")));
//        aggregationOperations.add(Aggregation.group(forecastFields).push("forecast").as("forecasts"));

        Fields updateTimeFields = Fields.fields("start_time", "element_code", "forecast_model", "forecast_time");
        //aggregationOperations.add(Aggregation.project(updateTimeFields).and("update_time").nested(bind("forecast_time", "forecast_time").and("time_effect").and("update_time")));
        aggregationOperations.add(Aggregation.group(updateTimeFields).push("update_time").as("update_dates"));

        Fields startTimeFields = Fields.fields("element_code", "forecast_model", "forecast_time");
        aggregationOperations.add(Aggregation.project(startTimeFields).and("start_time").nested(bind("start_time", "start_time").and("update_dates")));
        aggregationOperations.add(Aggregation.group(startTimeFields).push("start_time").as("start_times"));

        Fields elementCodeFields = Fields.fields("forecast_model", "forecast_time");
        aggregationOperations.add(Aggregation.project(elementCodeFields).and("element_code").nested(bind("element_code", "element_code").and("start_times")));
        aggregationOperations.add(Aggregation.group(elementCodeFields).push("element_code").as("element_codes"));

        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
        List<ElementTime> elementTimes = mongoTemplate.aggregate(aggregation, "elements", ElementTime.class).getMappedResults();
        return elementTimes;
    }

    @Override
    public List<Trapezoid> findAllTrapezoid(String areaCode) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();

        ProjectionOperation projectionOperation = Aggregation.project("grid_code", "area_code", "area_name", "loc").andExclude("_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(null, null, getTrapezoidMatch(areaCode), projectionOperation));

        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
        List<Trapezoid> trapezoids = mongoTemplate.aggregate(aggregation, "trapezoids", Trapezoid.class).getMappedResults();
        return trapezoids;
    }

    private MatchOperation getTrapezoidMatch(String areaCode){
        if (StringUtils.isEmpty(areaCode)){
            return null;
        }

        return Aggregation.match(Criteria.where("area_code").is(areaCode));
    }
}

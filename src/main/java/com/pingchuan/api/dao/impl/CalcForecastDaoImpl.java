package com.pingchuan.api.dao.impl;

import com.pingchuan.api.contants.CalcType;
import com.pingchuan.api.contants.TimeFormat;
import com.pingchuan.api.dao.CalcForecastDao;
import com.pingchuan.api.domain.Trapezoid;
import com.pingchuan.api.dto.calc.CalcElementValue;
import com.pingchuan.api.dto.calc.TotalCalc;
import com.pingchuan.api.util.AggregationUtil;
import com.pingchuan.api.util.TimeFormatUtil;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Field;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SimpleTimeZone;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.bind;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

/**
 * @description: 持久层
 * @author: XW
 * @create: 2019-11-13 16:12
 **/

@Component
public class CalcForecastDaoImpl implements CalcForecastDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<TotalCalc> calcElementValueByArea(Date startDate, Date updateDate, Date forecastDate, String calcType, String areaCode, String forecastModel, String elementCode) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        MatchOperation elementMatch = Aggregation.match(Criteria.where("_id").is(elementCode));
        aggregationOperations.add(elementMatch);

        LookupOperation elementInfoLookup = Aggregation.lookup("element_infos", "_id", "element_code", "element_infos");
        UnwindOperation elementInfoUnwind = Aggregation.unwind("$element_infos");
        MatchOperation elementInfoMatch = Aggregation.match(Criteria.where("element_infos.start_time").is(startDate).and("element_infos.update_time").is(updateDate).and("element_infos.forecast_model").is(forecastModel));
        ProjectionOperation elementInfoProject = Aggregation.project("$element_infos.trapezoid_info_id", "$element_infos.start_time", "$element_infos.update_time", "$element_infos.forecast_model", "$element_infos.element_code").andExclude("_id").and("$element_infos._id").as("element_info_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(elementInfoLookup, elementInfoUnwind, elementInfoMatch, elementInfoProject));

        LookupOperation trapezoidLookup = Aggregation.lookup("trapezoids", "trapezoid_info_id", "trapezoid_info_id", "trapezoids");
        UnwindOperation trapezoidUnwind = Aggregation.unwind("trapezoids");
        MatchOperation trapezoidMatch = Aggregation.match(Criteria.where("trapezoids.area_code").is(areaCode));
        ProjectionOperation trapezoidProject = Aggregation.project("trapezoids.area_code", "trapezoids.area_name", "trapezoids.grid_code", "trapezoids.loc", "trapezoid_info_id", "start_time", "update_time", "forecast_model", "element_code", "element_info_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(trapezoidLookup, trapezoidUnwind, trapezoidMatch, trapezoidProject));

        LookupOperation forecastInfoLookup = Aggregation.lookup("forecast_infos", "element_info_id", "element_info_id", "forecast_infos");
        UnwindOperation forecastInfoUnwind = Aggregation.unwind("forecast_infos");
        MatchOperation forecastInfoMatch = Aggregation.match(Criteria.where("forecast_infos.forecast_time").is(forecastDate).and("forecast_infos.is_finished").is(true));
        ProjectionOperation forecastInfoProject = Aggregation.project("loc", "area_code", "grid_code", "area_name", "start_time", "update_time", "forecast_model", "element_code", "forecast_infos.forecast_time", "forecast_infos.time_effect").and("forecast_infos._id").concat("$grid_code").as("element_value_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(forecastInfoLookup, forecastInfoUnwind, forecastInfoMatch, forecastInfoProject));

        LookupOperation elementValueLookup = Aggregation.lookup(TimeFormatUtil.CovertDateToString(TimeFormat.ELEMENT_VALUES_NAME, startDate), "element_value_id", "_id", "element_values");
        UnwindOperation elementValueUnwind = Aggregation.unwind("element_values");
        ProjectionOperation elementValueProject = Aggregation.project("loc", "area_code", "grid_code", "area_name", "start_time", "update_time", "forecast_model", "element_code", "forecast_time", "time_effect", "element_values.value").andExclude("_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(elementValueLookup, elementValueUnwind, null, elementValueProject));

        aggregationOperations.add(Aggregation.sort("max".equals(calcType) ? Sort.Direction.DESC : Sort.Direction.ASC, "value"));

        Fields locationFields = Fields.fields("start_time", "update_time", "forecast_model", "element_code", "forecast_time", "time_effect", "value");
        aggregationOperations.add(Aggregation.project(locationFields).and("location").nested(bind("loc", "loc").and("area_code").and("grid_code").and("area_name")));
        aggregationOperations.add(Aggregation.group(locationFields).push("location").as("locations"));

        Fields valueFields = Fields.fields("start_time", "update_time", "forecast_model", "element_code", "forecast_time", "time_effect");
        aggregationOperations.add(Aggregation.project(valueFields).and("value").nested(bind("value", "value").and("locations")));
        aggregationOperations.add(Aggregation.group(valueFields).first("value").as(calcType));

        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
        List<TotalCalc> totalCalcs = mongoTemplate.aggregate(aggregation, "elements", TotalCalc.class).getMappedResults();
        return totalCalcs;
    }

    @Override
    public List<TotalCalc> calcAvgElementValueByArea(Date startDate, Date updateDate, Date forecastDate, String calcType, String areaCode, String forecastModel, String elementCode) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        MatchOperation elementMatch = Aggregation.match(Criteria.where("_id").is(elementCode));
        aggregationOperations.add(elementMatch);

        LookupOperation elementInfoLookup = Aggregation.lookup("element_infos", "_id", "element_code", "element_infos");
        UnwindOperation elementInfoUnwind = Aggregation.unwind("$element_infos");
        MatchOperation elementInfoMatch = Aggregation.match(Criteria.where("element_infos.start_time").is(startDate).and("element_infos.update_time").is(updateDate).and("element_infos.forecast_model").is(forecastModel));
        ProjectionOperation elementInfoProject = Aggregation.project("$element_infos.trapezoid_info_id", "$element_infos.start_time", "$element_infos.update_time", "$element_infos.forecast_model", "$element_infos.element_code").andExclude("_id").and("$element_infos._id").as("element_info_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(elementInfoLookup, elementInfoUnwind, elementInfoMatch, elementInfoProject));

        LookupOperation trapezoidLookup = Aggregation.lookup("trapezoids", "trapezoid_info_id", "trapezoid_info_id", "trapezoids");
        UnwindOperation trapezoidUnwind = Aggregation.unwind("trapezoids");
        MatchOperation trapezoidMatch = Aggregation.match(Criteria.where("trapezoids.area_code").is(areaCode));
        ProjectionOperation trapezoidProject = Aggregation.project("trapezoids.area_code", "trapezoids.area_name", "trapezoids.grid_code", "trapezoids.loc", "trapezoid_info_id", "start_time", "update_time", "forecast_model", "element_code", "element_info_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(trapezoidLookup, trapezoidUnwind, trapezoidMatch, trapezoidProject));

        LookupOperation forecastInfoLookup = Aggregation.lookup("forecast_infos", "element_info_id", "element_info_id", "forecast_infos");
        UnwindOperation forecastInfoUnwind = Aggregation.unwind("forecast_infos");
        MatchOperation forecastInfoMatch = Aggregation.match(Criteria.where("forecast_infos.forecast_time").is(forecastDate).and("forecast_infos.is_finished").is(true));
        ProjectionOperation forecastInfoProject = Aggregation.project("loc", "area_code", "grid_code", "area_name", "start_time", "update_time", "forecast_model", "element_code", "forecast_infos.forecast_time", "forecast_infos.time_effect").and("forecast_infos._id").concat("$grid_code").as("element_value_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(forecastInfoLookup, forecastInfoUnwind, forecastInfoMatch, forecastInfoProject));

        LookupOperation elementValueLookup = Aggregation.lookup(TimeFormatUtil.CovertDateToString(TimeFormat.ELEMENT_VALUES_NAME, startDate), "element_value_id", "_id", "element_values");
        UnwindOperation elementValueUnwind = Aggregation.unwind("element_values");
        ProjectionOperation elementValueProject = Aggregation.project("start_time", "update_time", "forecast_model", "element_code", "forecast_time", "time_effect", "element_values.value").andExclude("_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(elementValueLookup, elementValueUnwind, null, elementValueProject));

        aggregationOperations.add(Aggregation.group("start_time", "update_time", "forecast_model", "element_code", "forecast_time", "time_effect").avg("value").as("avg"));
        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);

        List<TotalCalc> totalCalcs = mongoTemplate.aggregate(aggregation, "elements", TotalCalc.class).getMappedResults();
        return totalCalcs;
    }

    @Override
    public List<TotalCalc> calcAvgElementValueByLocation(String calcType, Date startDate, Date updateDate, Date startForecastDate, Date endForecastDate, List<double[]> locations, String forecastModel, String elementCode) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        MatchOperation trapezoidMatch = Aggregation.match(Criteria.where("_id").in(findAllTrapezoidIdByLocation(locations)));
        aggregationOperations.add(trapezoidMatch);
        ProjectionOperation trapezoidProject = project("loc", "trapezoid_info_id", "area_code", "area_name", "grid_code").andExclude("_id");
        aggregationOperations.add(trapezoidProject);

        LookupOperation elementInfoLookup = Aggregation.lookup("element_infos", "trapezoid_info_id", "trapezoid_info_id", "element_infos");
        UnwindOperation elementInfoUnwind = Aggregation.unwind("element_infos");
        MatchOperation elementInfoMatch = Aggregation.match(Criteria.where("element_infos.start_time").is(startDate).and("element_infos.update_time").is(updateDate).and("element_infos.forecast_model").is(forecastModel));
        ProjectionOperation elementInfoProject = project("loc", "area_code", "area_name", "grid_code", "element_infos.start_time", "element_infos.update_time", "element_infos.element_code", "element_infos.forecast_model").and("element_infos._id").as("element_info_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(elementInfoLookup, elementInfoUnwind, elementInfoMatch, elementInfoProject));

        LookupOperation elementLookup = Aggregation.lookup("elements", "element_code", "_id", "elements");
        UnwindOperation elementUnwind = Aggregation.unwind("elements");
        MatchOperation elementMatch = Aggregation.match(Criteria.where("elements._id").is(elementCode));
        ProjectionOperation elementProjection = project("loc", "area_code", "area_name", "grid_code", "start_time", "update_time", "element_code", "forecast_model", "element_info_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(elementLookup, elementUnwind, elementMatch, elementProjection));

        LookupOperation forecastInfoLookup = Aggregation.lookup("forecast_infos", "element_info_id", "element_info_id", "forecast_infos");
        UnwindOperation forecastInfoUnwind = Aggregation.unwind("forecast_infos");
        MatchOperation forecastInfoMatch = Aggregation.match(Criteria.where("forecast_infos.forecast_time").gte(startForecastDate).lt(endForecastDate).and("forecast_infos.is_finished").is(true));
        ProjectionOperation forecastInfoProject = project("loc", "area_code", "grid_code", "area_name", "start_time", "update_time", "forecast_model", "element_code", "forecast_infos.forecast_time", "forecast_infos.time_effect").and("forecast_infos._id").concat("$grid_code").as("element_value_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(forecastInfoLookup, forecastInfoUnwind, forecastInfoMatch, forecastInfoProject));

        LookupOperation elementValueLookup = Aggregation.lookup(TimeFormatUtil.CovertDateToString(TimeFormat.ELEMENT_VALUES_NAME, startDate), "element_value_id", "_id", "element_values");
        UnwindOperation elementValueUnwind = Aggregation.unwind("element_values");
        ProjectionOperation elementValueProject = project("loc", "area_code", "grid_code", "area_name", "start_time", "update_time", "forecast_model", "element_code", "forecast_time", "time_effect", "element_values.value").andExclude("_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(elementValueLookup, elementValueUnwind, null, elementValueProject));

        aggregationOperations.add(Aggregation.group("loc", "area_code","grid_code", "area_name", "start_time", "update_time", "forecast_model", "element_code").first("forecast_time").as("start_forecast_time").last("forecast_time").as("end_forecast_time").avg("value").as("avg"));
        Fields locationFields = Fields.fields("start_time", "update_time", "forecast_model", "element_code");
        aggregationOperations.add(Aggregation.project(locationFields).and("location").nested(bind("loc", "loc").and("area_code").and("area_name").and("grid_code").and("start_forecast_time").and("end_forecast_time").and("avg")));
        aggregationOperations.add(Aggregation.group(locationFields).push("location").as("locations"));

        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
        List<TotalCalc> totalCalcs = mongoTemplate.aggregate(aggregation, "trapezoids", TotalCalc.class).getMappedResults();
        return totalCalcs;
    }

    @Override
    public List<TotalCalc> calcElementValueByLocation(String calcType, Date startDate, Date updateDate, Date startForecastDate, Date endForecastDate, List<double[]> locations, String forecastModel, String elementCode) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        MatchOperation trapezoidMatch = Aggregation.match(Criteria.where("_id").in(findAllTrapezoidIdByLocation(locations)));
        aggregationOperations.add(trapezoidMatch);
        ProjectionOperation trapezoidProject = project("loc", "trapezoid_info_id", "area_code", "area_name", "grid_code").andExclude("_id");
        aggregationOperations.add(trapezoidProject);

        LookupOperation elementInfoLookup = Aggregation.lookup("element_infos", "trapezoid_info_id", "trapezoid_info_id", "element_infos");
        UnwindOperation elementInfoUnwind = Aggregation.unwind("element_infos");
        MatchOperation elementInfoMatch = Aggregation.match(Criteria.where("element_infos.start_time").is(startDate).and("element_infos.update_time").is(updateDate).and("element_infos.forecast_model").is(forecastModel));
        ProjectionOperation elementInfoProject = project("loc", "area_code", "area_name", "grid_code", "element_infos.start_time", "element_infos.update_time", "element_infos.element_code", "element_infos.forecast_model").and("element_infos._id").as("element_info_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(elementInfoLookup, elementInfoUnwind, elementInfoMatch, elementInfoProject));

        LookupOperation elementLookup = Aggregation.lookup("elements", "element_code", "_id", "elements");
        UnwindOperation elementUnwind = Aggregation.unwind("elements");
        MatchOperation elementMatch = Aggregation.match(Criteria.where("elements._id").is(elementCode));
        ProjectionOperation elementProjection = project("loc", "area_code", "area_name", "grid_code", "start_time", "update_time", "element_code", "forecast_model", "element_info_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(elementLookup, elementUnwind, elementMatch, elementProjection));

        LookupOperation forecastInfoLookup = Aggregation.lookup("forecast_infos", "element_info_id", "element_info_id", "forecast_infos");
        UnwindOperation forecastInfoUnwind = Aggregation.unwind("forecast_infos");
        MatchOperation forecastInfoMatch = Aggregation.match(Criteria.where("forecast_infos.forecast_time").gte(startForecastDate).lt(endForecastDate).and("forecast_infos.is_finished").is(true));
        ProjectionOperation forecastInfoProject = project("loc", "area_code", "grid_code", "area_name", "start_time", "update_time", "forecast_model", "element_code", "forecast_infos.forecast_time", "forecast_infos.time_effect").and("forecast_infos._id").concat("$grid_code").as("element_value_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(forecastInfoLookup, forecastInfoUnwind, forecastInfoMatch, forecastInfoProject));

        LookupOperation elementValueLookup = Aggregation.lookup(TimeFormatUtil.CovertDateToString(TimeFormat.ELEMENT_VALUES_NAME, startDate), "element_value_id", "_id", "element_values");
        UnwindOperation elementValueUnwind = Aggregation.unwind("element_values");
        ProjectionOperation elementValueProject = project("loc", "area_code", "grid_code", "area_name", "start_time", "update_time", "forecast_model", "element_code", "forecast_time", "time_effect", "element_values.value").andExclude("_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(elementValueLookup, elementValueUnwind, null, elementValueProject));

        aggregationOperations.add(Aggregation.sort("max".equals(calcType) ? Sort.Direction.DESC : Sort.Direction.ASC, "value"));

        Fields forecastFields = Fields.fields("loc", "area_code", "grid_code", "area_name", "start_time", "update_time", "forecast_model", "element_code", "value");
        aggregationOperations.add(Aggregation.project(forecastFields).and("forecast").nested(bind("forecast_time", "forecast_time").and("time_effect")));
        aggregationOperations.add(Aggregation.group(forecastFields).push("forecast").as("forecasts"));

        Fields valueFields = Fields.fields("loc", "area_code", "grid_code", "area_name", "start_time", "update_time", "forecast_model", "element_code");
        aggregationOperations.add(Aggregation.project(valueFields).and("value").nested(bind("value", "value").and("forecasts")));
        aggregationOperations.add(Aggregation.group(valueFields).first("value").as(calcType));

        Fields locationFields = Fields.fields("start_time", "update_time", "forecast_model", "element_code");
        aggregationOperations.add(Aggregation.project(locationFields).and("location").nested(bind("loc", "loc").and("grid_code").and("area_name").and("area_code").and(calcType)));
        aggregationOperations.add(Aggregation.group(locationFields).push("location").as("locations"));

        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
        List<TotalCalc> totalCalcs = mongoTemplate.aggregate(aggregation, "trapezoids", TotalCalc.class).getMappedResults();
        return totalCalcs;
    }

    @Override
    public List<TotalCalc> findSumOrAvgByTimeInterval(int timeInterval, String elementCode, Date startDate, Date updateDate, String forecastModel, List<double[]> locations, String calcType, int totalHour) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        aggregationOperations.add(Aggregation.match(Criteria.where("_id").in(findAllTrapezoidIdByLocation(locations))));
        ProjectionOperation trapezoidProject = project("loc", "trapezoid_info_id", "area_code", "area_name", "grid_code").andExclude("_id");
        aggregationOperations.add(trapezoidProject);

        LookupOperation elementInfoLookup = Aggregation.lookup("element_infos", "trapezoid_info_id", "trapezoid_info_id", "element_infos");
        UnwindOperation elementInfoUnwind = Aggregation.unwind("element_infos");
        MatchOperation elementInfoMatch = Aggregation.match(Criteria.where("element_infos.start_time").is(startDate).and("element_infos.update_time").is(updateDate).and("element_infos.forecast_model").is(forecastModel).and("element_infos.element_code").is(elementCode));
        ProjectionOperation elementInfoProject = project("loc", "area_code", "area_name", "grid_code", "element_infos.start_time", "element_infos.update_time", "element_infos.element_code", "element_infos.forecast_model").and("element_infos._id").as("element_info_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(elementInfoLookup, elementInfoUnwind, elementInfoMatch, elementInfoProject));

        LookupOperation forecastInfoLookup = Aggregation.lookup("forecast_infos", "element_info_id", "element_info_id", "forecast_infos");
        UnwindOperation forecastInfoUnwind = Aggregation.unwind("forecast_infos");
        ProjectionOperation forecastInfoProject = project("loc", "area_code", "grid_code", "area_name", "start_time", "update_time", "forecast_model", "element_code", "forecast_infos.forecast_time", "forecast_infos.time_effect").and("forecast_infos._id").concat("$grid_code").as("element_value_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(forecastInfoLookup, forecastInfoUnwind, null, forecastInfoProject));

        LookupOperation elementValueLookup = Aggregation.lookup(TimeFormatUtil.CovertDateToString(TimeFormat.ELEMENT_VALUES_NAME, startDate), "element_value_id", "_id", "element_values");
        UnwindOperation elementValueUnwind = Aggregation.unwind("element_values");
        ProjectionOperation elementValueProject = project("loc", "area_code", "grid_code", "area_name", "start_time", "update_time", "forecast_model", "element_code", "forecast_time", "time_effect", "element_values.value").and(DateOperators.dateOf("forecast_time").withTimezone(DateOperators.Timezone.valueOf("+08:00")).hour()).as("hour").and(DateOperators.dateOf("forecast_time").withTimezone(DateOperators.Timezone.valueOf("+08:00")).toString("%Y-%m-%d")).as("day").andExclude("_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(elementValueLookup, elementValueUnwind, null, elementValueProject));

        aggregationOperations.add(Aggregation.sort(Sort.Direction.ASC, "forecast_time"));
        ProjectionOperation project = Aggregation.project("loc", "area_code", "grid_code", "area_name", "start_time", "update_time", "forecast_model", "element_code", "forecast_time", "time_effect", "value", "hour", "day").and("hour").lt(timeInterval).as("is_midnight");
        aggregationOperations.add(project);
        if (CalcType.avg.equals(calcType)){
            aggregationOperations.add(Aggregation.group("area_name", "loc", "grid_code", "start_time", "update_time", "forecast_model", "area_code", "element_code", "day", "is_midnight").avg("value").as("avg").max("forecast_time").as("end_forecast_time").min("forecast_time").as("start_forecast_time"));
            aggregationOperations.add(Aggregation.project("start_time", "update_time", "forecast_model", "element_code", "day", "loc", "area_code", "area_name", "grid_code", "is_midnight").and("value").nested(bind("avg", "avg").and("start_forecast_time").and("end_forecast_time")));

        }else {
            aggregationOperations.add(Aggregation.group("area_name", "loc", "grid_code", "start_time", "update_time", "forecast_model", "area_code", "element_code", "day", "is_midnight").sum("value").as("sum").max("forecast_time").as("end_forecast_time").min("forecast_time").as("start_forecast_time"));
            aggregationOperations.add(Aggregation.project("start_time", "update_time", "forecast_model", "element_code", "day", "loc", "area_code", "area_name", "grid_code", "is_midnight").and("value").nested(bind("sum", "sum").and("start_forecast_time").and("end_forecast_time")));
        }
        aggregationOperations.add(Aggregation.group("start_time", "update_time", "forecast_model", "element_code", "day", "loc", "area_code", "area_name", "grid_code").push("value").as("values"));

        Fields dayFields = Fields.fields("start_time", "update_time", "forecast_model", "element_code", "loc", "area_code", "area_name", "grid_code");
        aggregationOperations.add(Aggregation.project(dayFields).and("day").nested(bind("day", "day").and("values")));
        aggregationOperations.add(Aggregation.group(dayFields).push("day").as("days"));

        aggregationOperations.add(Aggregation.project("start_time", "update_time", "forecast_model", "element_code").and("location").nested(bind("days", "days").and("loc").and("area_code").and("area_name").and("grid_code")));
        aggregationOperations.add(Aggregation.group("start_time", "update_time", "forecast_model", "element_code").push("location").as("locations"));

        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
        List<TotalCalc> totalCalcs = mongoTemplate.aggregate(aggregation, "trapezoids", TotalCalc.class).getMappedResults();
        return totalCalcs;
    }

    @Override
    public List<TotalCalc> findMaxOrMinByTimeInterval(int timeInterval, String elementCode, Date startDate, Date updateDate, String forecastModel, List<double[]> locations, String calcType, int totalHour) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        aggregationOperations.add(Aggregation.match(Criteria.where("_id").in(findAllTrapezoidIdByLocation(locations))));
        ProjectionOperation trapezoidProject = project("loc", "trapezoid_info_id", "area_code", "area_name", "grid_code").andExclude("_id");
        aggregationOperations.add(trapezoidProject);

        LookupOperation elementInfoLookup = Aggregation.lookup("element_infos", "trapezoid_info_id", "trapezoid_info_id", "element_infos");
        UnwindOperation elementInfoUnwind = Aggregation.unwind("element_infos");
        MatchOperation elementInfoMatch = Aggregation.match(Criteria.where("element_infos.start_time").is(startDate).and("element_infos.update_time").is(updateDate).and("element_infos.forecast_model").is(forecastModel).and("element_infos.element_code").is(elementCode));
        ProjectionOperation elementInfoProject = project("loc", "area_code", "area_name", "grid_code", "element_infos.start_time", "element_infos.update_time", "element_infos.element_code", "element_infos.forecast_model").and("element_infos._id").as("element_info_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(elementInfoLookup, elementInfoUnwind, elementInfoMatch, elementInfoProject));

        LookupOperation forecastInfoLookup = Aggregation.lookup("forecast_infos", "element_info_id", "element_info_id", "forecast_infos");
        UnwindOperation forecastInfoUnwind = Aggregation.unwind("forecast_infos");
        ProjectionOperation forecastInfoProject = project("loc", "area_code", "grid_code", "area_name", "start_time", "update_time", "forecast_model", "element_code", "forecast_infos.forecast_time", "forecast_infos.time_effect").and("forecast_infos._id").concat("$grid_code").as("element_value_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(forecastInfoLookup, forecastInfoUnwind, null, forecastInfoProject));

        LookupOperation elementValueLookup = Aggregation.lookup(TimeFormatUtil.CovertDateToString(TimeFormat.ELEMENT_VALUES_NAME, startDate), "element_value_id", "_id", "element_values");
        UnwindOperation elementValueUnwind = Aggregation.unwind("element_values");
        ProjectionOperation elementValueProject = project("loc", "area_code", "grid_code", "area_name", "start_time", "update_time", "forecast_model", "element_code", "forecast_time", "time_effect", "element_values.value").and(DateOperators.dateOf("forecast_time").withTimezone(DateOperators.Timezone.valueOf("+08:00")).hour()).as("hour").and(DateOperators.dateOf("forecast_time").withTimezone(DateOperators.Timezone.valueOf("+08:00")).toString("%Y-%m-%d")).as("day").andExclude("_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(elementValueLookup, elementValueUnwind, null, elementValueProject));

        Fields midnightFields = Fields.fields("loc", "area_code", "grid_code", "area_name", "start_time", "update_time", "forecast_model", "element_code", "forecast_time", "time_effect", "value", "day");
        aggregationOperations.add(Aggregation.project(midnightFields).and("hour").lt(12).as("is_midnight"));

        Fields forecastFields = Fields.fields("loc", "area_code", "grid_code", "area_name", "start_time", "update_time", "forecast_model", "element_code","value", "day", "is_midnight");
        aggregationOperations.add(Aggregation.project(forecastFields).and("forecast").nested(bind("forecast_time", "forecast_time").and("time_effect")));
        aggregationOperations.add(Aggregation.group("loc", "area_code", "grid_code", "area_name", "start_time", "update_time", "forecast_model", "element_code", "value", "day", "is_midnight").min("forecast.forecast_time").as("start_forecast_time").max("forecast.forecast_time").as("end_forecast_time").push("forecast").as("forecasts"));

        aggregationOperations.add(Aggregation.sort(CalcType.max.endsWith(calcType) ? Sort.Direction.DESC : Sort.Direction.ASC, "value"));
        Fields valueFields = Fields.fields("loc", "area_code", "grid_code", "area_name", "start_time", "update_time", "forecast_model", "element_code", "day", "is_midnight");
        aggregationOperations.add((Aggregation.project(valueFields).and("value").nested(bind("value", "value").and("forecasts").and("start_forecast_time").and("end_forecast_time"))));
        aggregationOperations.add(Aggregation.group(valueFields).first("value").as(calcType).min("value.start_forecast_time").as("start_forecast_time").max("value.start_forecast_time").as("end_forecast_time"));

        Fields fields = Fields.fields("start_time", "update_time", "forecast_model", "element_code", "loc","day", "area_code", "grid_code", "area_name");
        aggregationOperations.add(Aggregation.project(fields).and("value").nested(bind("start_forecast_time", "start_forecast_time").and("start_forecast_time").and("end_forecast_time").and(calcType)));
        aggregationOperations.add(Aggregation.group(fields).push("value").as("values"));

        Fields dayFields = Fields.fields("start_time", "update_time", "forecast_model", "element_code", "loc", "area_code", "grid_code", "area_name");
        aggregationOperations.add(Aggregation.project(dayFields).and("day").nested(bind("values", "values").and("day")));
        aggregationOperations.add(Aggregation.group(dayFields).push("day").as("days"));

        Fields locationFields = Fields.fields("start_time", "update_time", "forecast_model", "element_code");
        aggregationOperations.add(Aggregation.project(locationFields).and("location").nested(bind("loc", "loc").and("grid_code").and("area_code").and("area_name").and("days")));
        aggregationOperations.add(Aggregation.group(locationFields).push("location").as("locations"));

        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
        List<TotalCalc> totalCalcs = mongoTemplate.aggregate(aggregation, "trapezoids", TotalCalc.class).getMappedResults();
        return totalCalcs;
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
}

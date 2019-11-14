package com.pingchuan.api.dao.impl;

import com.pingchuan.api.contants.TimeFormat;
import com.pingchuan.api.dao.CalcForecastDao;
import com.pingchuan.api.dto.calc.CalcElementValue;
import com.pingchuan.api.util.AggregationUtil;
import com.pingchuan.api.util.TimeFormatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.geo.GeoJsonMultiPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public CalcElementValue calcElementValueByArea(Date startDate, Date updateDate, Date forecastDate, String calcType, String areaCode, String forecastModel, String elementCode) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();

        //GeoJsonMultiPoint geoJsonMultiPoint = new GeoJsonMultiPoint();

        //Aggregation.match(Criteria.where("loc").intersects(geoJsonMultiPoint));
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

        //aggregationOperations.add(Aggregation.group("value").push("loc", "area_code", "grid_code", "area_name", "start_time", "update_time", "forecast_model", "element_code", "forecast_time", "time_effect", "value").as("element"));
        aggregationOperations.add(Aggregation.sort("max".equals(calcType) ? Sort.Direction.DESC : Sort.Direction.ASC, "value"));
        aggregationOperations.add(Aggregation.limit(1));
        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
        List<CalcElementValue> calcElementValues = mongoTemplate.aggregate(aggregation, "elements", CalcElementValue.class).getMappedResults();



        if (calcElementValues.size() > 0)
            return calcElementValues.get(0);

        return null;
    }

    @Override
    public CalcElementValue calcAvgElementValueByArea(Date startDate, Date updateDate, Date forecastDate, String calcType, String areaCode, String forecastModel, String elementCode) {
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

        GroupOperation elementValueGroup = Aggregation.group("grid_code", "area_name", "start_time", "update_time", "forecast_model", "element_code", "forecast_time", "time_effect").avg("value").as("value");
        aggregationOperations.add(elementValueGroup);
        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
        List<CalcElementValue> calcElementValues = mongoTemplate.aggregate(aggregation, "elements", CalcElementValue.class).getMappedResults();

        if (calcElementValues.size() > 0)
            return calcElementValues.get(0);

        return null;
    }

}

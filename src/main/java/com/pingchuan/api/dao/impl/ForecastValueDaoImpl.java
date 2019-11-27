package com.pingchuan.api.dao.impl;

import com.pingchuan.api.contants.TimeFormat;
import com.pingchuan.api.dao.ForecastValueDao;
import com.pingchuan.api.domain.Trapezoid;
import com.pingchuan.api.dto.base.*;
import com.pingchuan.api.parameter.base.LineParameter;
import com.pingchuan.api.util.AggregationUtil;
import com.pingchuan.api.util.TimeFormatUtil;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.geo.GeoJsonLineString;
import org.springframework.data.mongodb.core.geo.GeoJsonMultiPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Field;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.bind;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

/**
 * @description: 预报数据 dao 实现
 * @author: XW
 * @create: 2019-11-07 15:18
 **/

@Component
public class ForecastValueDaoImpl implements ForecastValueDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final Fields trapezoidFields = Fields.fields("loc", "trapezoid_info_id", "area_code", "area_name", "grid_code");

    @Override
    public List<Element> findNJGridsByArea(Date updateTime, Date startTime, Date forecastTime, String areaCode, String elementCode, String forecastModel, double[] threshold, boolean isNeedElementCode) {

        List<AggregationOperation> aggregationOperations = new ArrayList<>();

        if (isNeedElementCode) {
            MatchOperation elementMatch = Aggregation.match(Criteria.where("_id").is(elementCode));
            aggregationOperations.add(elementMatch);
        }

        LookupOperation elementInfoLookup = Aggregation.lookup("element_infos", "_id", "element_code", "element_infos");
        UnwindOperation elementInfoUnwind = Aggregation.unwind("$element_infos");
        MatchOperation elementInfoMatch = Aggregation.match(Criteria.where("element_infos.start_time").is(startTime).and("element_infos.update_time").is(updateTime).and("element_infos.forecast_model").is(forecastModel));
        ProjectionOperation elementInfoProject = project("$element_infos.trapezoid_info_id", "$element_infos.start_time", "$element_infos.update_time", "$element_infos.forecast_model", "$element_infos.element_code").andExclude("_id").and("$element_infos._id").as("element_info_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(elementInfoLookup, elementInfoUnwind, elementInfoMatch, elementInfoProject));

        LookupOperation trapezoidLookup = Aggregation.lookup("trapezoids", "trapezoid_info_id", "trapezoid_info_id", "trapezoids");
        UnwindOperation trapezoidUnwind = Aggregation.unwind("trapezoids");
        MatchOperation trapezoidMatch = Aggregation.match(Criteria.where("trapezoids.area_code").is(areaCode));
        ProjectionOperation trapezoidProject = project("trapezoids.area_code", "trapezoids.area_name", "trapezoids.grid_code", "trapezoids.loc", "trapezoid_info_id", "start_time", "update_time", "forecast_model", "element_code", "element_info_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(trapezoidLookup, trapezoidUnwind, trapezoidMatch, trapezoidProject));

        LookupOperation forecastInfoLookup = Aggregation.lookup("forecast_infos", "element_info_id", "element_info_id", "forecast_infos");
        UnwindOperation forecastInfoUnwind = Aggregation.unwind("forecast_infos", "index", true);
        MatchOperation forecastInfoMatch = Aggregation.match(Criteria.where("forecast_infos.forecast_time").is(forecastTime).and("forecast_infos.is_finished").is(true));
        ProjectionOperation forecastInfoProject = project("loc", "area_code", "grid_code", "area_name", "start_time", "update_time", "forecast_model", "element_code", "forecast_infos.forecast_time", "forecast_infos.time_effect").and("forecast_infos._id").concat("$grid_code").as("element_value_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(forecastInfoLookup, forecastInfoUnwind, forecastInfoMatch, forecastInfoProject));

        LookupOperation elementValueLookup = Aggregation.lookup(TimeFormatUtil.CovertDateToString(TimeFormat.ELEMENT_VALUES_NAME, startTime), "element_value_id", "_id", "element_values");
        UnwindOperation elementValueUnwind = Aggregation.unwind("element_values");
        ProjectionOperation elementValueProject = project("loc", "grid_code", "area_name", "start_time", "update_time", "forecast_model", "element_code", "forecast_time", "time_effect", "area_code", "element_values.value").andExclude("_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(elementValueLookup, elementValueUnwind, null, elementValueProject));
        if (!StringUtils.isEmpty(threshold)) {
            aggregationOperations.add(Aggregation.match(Criteria.where("value").gte(threshold[0]).lte(threshold[1])));
        }

        Fields locationFields = Fields.fields("start_time", "update_time", "forecast_model", "element_code", "forecast_time", "time_effect", "area_name", "area_code");
        aggregationOperations.add(Aggregation.project(locationFields).and("location").nested(bind("loc", "loc").and("grid_code").and("value")));
        aggregationOperations.add(Aggregation.group(locationFields).push("location").as("locations"));

        Fields forecastFields = Fields.fields("start_time", "update_time", "forecast_model", "element_code", "area_name", "area_code");
        aggregationOperations.add(Aggregation.project(forecastFields).and("forecast").nested(bind("forecast_time", "forecast_time").and("time_effect").and("locations")));
        aggregationOperations.add(Aggregation.group(forecastFields).push("forecast").as("forecasts"));

        Fields elementCodeFields = Fields.fields("start_time", "update_time", "forecast_model", "area_name", "area_code");
        aggregationOperations.add(Aggregation.project(elementCodeFields).and("element_code").nested(bind("element_code", "element_code").and("forecasts")));
        aggregationOperations.add(Aggregation.group(elementCodeFields).push("element_code").as("element_codes"));

        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);

        List<com.pingchuan.api.dto.bases.Element> elements = mongoTemplate.aggregate(aggregation, "elements", com.pingchuan.api.dto.bases.Element.class).getMappedResults();
        return null;
        //return elements;
    }

    @Override
    public List<Element> findNJGridsByLocation(String elementCode, List<double[]> locations, Date startDate, Date updateDate, Date forecastDate, String forecastModel, double[] threshold, boolean isNeedElementCode) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        aggregationOperations.add(Aggregation.match(Criteria.where("_id").in(findAllTrapezoidIdByLocation(locations))));
        aggregationOperations.add(project(trapezoidFields).andExclude("_id"));

        LookupOperation elementInfoLookup = Aggregation.lookup("element_infos", "trapezoid_info_id", "trapezoid_info_id", "element_infos");
        UnwindOperation elementInfoUnwind = Aggregation.unwind("element_infos");
        MatchOperation elementInfoMatch = Aggregation.match(Criteria.where("element_infos.start_time").is(startDate).and("element_infos.update_time").is(updateDate).and("element_infos.forecast_model").is(forecastModel));
        ProjectionOperation elementInfoProject = project("loc", "area_code", "area_name", "grid_code", "element_infos.start_time", "element_infos.update_time", "element_infos.element_code", "element_infos.forecast_model").and("element_infos._id").as("element_info_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(elementInfoLookup, elementInfoUnwind, elementInfoMatch, elementInfoProject));

        LookupOperation elementLookup = Aggregation.lookup("elements", "element_code", "_id", "elements");
        UnwindOperation elementUnwind = Aggregation.unwind("elements");
        ProjectionOperation elementProjection = project("loc", "area_code", "area_name", "grid_code", "start_time", "update_time", "element_code", "forecast_model", "element_info_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(elementLookup, elementUnwind, null, elementProjection));
        if (isNeedElementCode){
            aggregationOperations.add(Aggregation.match(Criteria.where("element_code").is(elementCode)));
        }

        LookupOperation forecastInfoLookup = Aggregation.lookup("forecast_infos", "element_info_id", "element_info_id", "forecast_infos");
        UnwindOperation forecastInfoUnwind = Aggregation.unwind("forecast_infos");
        MatchOperation forecastInfoMatch = Aggregation.match(Criteria.where("forecast_infos.forecast_time").is(forecastDate).and("forecast_infos.is_finished").is(true));
        ProjectionOperation forecastInfoProject = project("loc", "area_code", "grid_code", "area_name", "start_time", "update_time", "forecast_model", "element_code", "forecast_infos.forecast_time", "forecast_infos.time_effect").and("forecast_infos._id").concat("$grid_code").as("element_value_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(forecastInfoLookup, forecastInfoUnwind, forecastInfoMatch, forecastInfoProject));

        LookupOperation elementValueLookup = Aggregation.lookup(TimeFormatUtil.CovertDateToString(TimeFormat.ELEMENT_VALUES_NAME, startDate), "element_value_id", "_id", "element_values");
        UnwindOperation elementValueUnwind = Aggregation.unwind("element_values");
        ProjectionOperation elementValueProject = project("loc", "area_code", "grid_code", "area_name", "start_time", "update_time", "forecast_model", "element_code", "forecast_time", "time_effect", "element_values.value").andExclude("_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(elementValueLookup, elementValueUnwind, null, elementValueProject));
        if (!StringUtils.isEmpty(threshold)) {
            aggregationOperations.add(Aggregation.match(Criteria.where("value").gte(threshold[0]).lte(threshold[1])));
        }

        aggregationOperations.addAll(getLocationGroup());

        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
        List<Element> elements = mongoTemplate.aggregate(aggregation, "trapezoids", Element.class).getMappedResults();
        return elements;
    }

    private PointInfo mergePointInfo(List<PointInfo> pointInfos){
        if (pointInfos.size() == 0)
            return null;

        List<PointLocation> pointLocations = pointInfos.stream().map(p -> p.getLocations().get(0)).collect(Collectors.toList());
        PointInfo pointInfo = pointInfos.get(0);
        pointInfo.setLocations(pointLocations);
        return pointInfo;
    }

    @Override
    public List<Element> findNJGridsByForecastTimeRange(LineParameter line) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();

        aggregationOperations.add(Aggregation.match(Criteria.where("_id").in(findAllTrapezoidIdByLocation(line.getLocations()))));
        aggregationOperations.add(project(trapezoidFields).andExclude("_id"));

        LookupOperation elementInfoLookup = Aggregation.lookup("element_infos", "trapezoid_info_id", "trapezoid_info_id", "element_infos");
        UnwindOperation elementInfoUnwind = Aggregation.unwind("element_infos");
        MatchOperation elementInfoMatch = Aggregation.match(Criteria.where("element_infos.start_time").is(line.getStartDate()).and("element_infos.update_time").is(line.getUpdateDate()).and("element_infos.forecast_model").is(line.getForecastModel()));
        ProjectionOperation elementInfoProject = project("loc", "area_code", "area_name", "grid_code", "element_infos.start_time", "element_infos.update_time", "element_infos.element_code", "element_infos.forecast_model").and("element_infos._id").as("element_info_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(elementInfoLookup, elementInfoUnwind, elementInfoMatch, elementInfoProject));

        LookupOperation elementLookup = Aggregation.lookup("elements", "element_code", "_id", "elements");
        UnwindOperation elementUnwind = Aggregation.unwind("elements");
        ProjectionOperation elementProjection = project("loc", "area_code", "area_name", "grid_code", "start_time", "update_time", "element_code", "forecast_model", "element_info_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(elementLookup, elementUnwind, getElementCodeMath(line.getElementCode()), elementProjection));

        LookupOperation forecastInfoLookup = Aggregation.lookup("forecast_infos", "element_info_id", "element_info_id", "forecast_infos");
        UnwindOperation forecastInfoUnwind = Aggregation.unwind("forecast_infos");
        MatchOperation forecastInfoMatch = Aggregation.match(Criteria.where("forecast_infos.forecast_time").gte(line.getStartForecastDate()).lt(line.getEndForecastDate()).and("forecast_infos.is_finished").is(true));
        ProjectionOperation forecastInfoProject = project("loc", "area_code", "grid_code", "area_name", "start_time", "update_time", "forecast_model", "element_code", "forecast_infos.forecast_time", "forecast_infos.time_effect").and("forecast_infos._id").concat("$grid_code").as("element_value_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(forecastInfoLookup, forecastInfoUnwind, forecastInfoMatch, forecastInfoProject));

        LookupOperation elementValueLookup = Aggregation.lookup(TimeFormatUtil.CovertDateToString(TimeFormat.ELEMENT_VALUES_NAME, line.getStartDate()), "element_value_id", "_id", "element_values");
        UnwindOperation elementValueUnwind = Aggregation.unwind("element_values");
        ProjectionOperation elementValueProject = project("loc", "area_code", "grid_code", "area_name", "start_time", "update_time", "forecast_model", "element_code", "forecast_time", "time_effect", "element_values.value").andExclude("_id");
        aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(elementValueLookup, elementValueUnwind, null, elementValueProject));

        aggregationOperations.addAll(getLocationGroup());

        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
        List<Element> elements = mongoTemplate.aggregate(aggregation, "trapezoids", Element.class).getMappedResults();
        return elements;
    }

    private List<AggregationOperation> getLocationGroup(){
        List<AggregationOperation> aggregationOperations = new ArrayList<>();

        Fields locationFields = Fields.fields("start_time", "update_time", "forecast_model", "element_code", "forecast_time", "time_effect");
        aggregationOperations.add(Aggregation.project(locationFields).and("location").nested(bind("loc", "loc").and("area_code").and("area_name").and("grid_code").and("value")));
        aggregationOperations.add(Aggregation.group(locationFields).push("location").as("locations"));

        Fields forecastFields = Fields.fields("start_time", "update_time", "forecast_model", "element_code");
        aggregationOperations.add(Aggregation.project(forecastFields).and("forecast").nested(bind("forecast_time", "forecast_time").and("time_effect").and("locations")));
        aggregationOperations.add(Aggregation.group(forecastFields).push("forecast").as("forecasts"));

        Fields elementCodeFields = Fields.fields("start_time", "update_time", "forecast_model");
        aggregationOperations.add(Aggregation.project(elementCodeFields).and("element_code").nested(bind("element_code", "element_code").and("forecasts")));
        aggregationOperations.add(Aggregation.group(elementCodeFields).push("element_code").as("element_codes"));

        return aggregationOperations;
    }

    private MatchOperation getElementCodeMath(String elementCode){
        if (StringUtils.isEmpty(elementCode)) {
            return null;
        }

        return Aggregation.match(Criteria.where("element_code").is(elementCode));
    }

    private GeoJsonMultiPoint getGeoJsonMultiPoint(List<double[]> locations){
        List<Point> points = new ArrayList<>();
        locations.forEach(l -> points.add(new Point(l[0], l[1])));
        GeoJsonMultiPoint geoJsonMultiPoint = new GeoJsonMultiPoint(points);
        return geoJsonMultiPoint;
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

    private List<ForecastInfo> getAllForecastInfo(List<LineInfo> lineInfos){
        List<List<ForecastInfo>> lists = lineInfos.stream().map(l -> l.getForecasts()).collect(Collectors.toList());
        List<ForecastInfo> forecastInfos = new ArrayList<>();
        lists.forEach(l -> forecastInfos.addAll(l));
        return forecastInfos;
    }



    private List<AggregationOperation> SetAggregationOperation(ProjectionOperation projectionOperation, GroupOperation groupOperation) {

        List<AggregationOperation> aggregationOperations = new ArrayList<>();

        if (!StringUtils.isEmpty(projectionOperation))
            aggregationOperations.add(projectionOperation);

        if (!StringUtils.isEmpty(groupOperation))
            aggregationOperations.add(groupOperation);

        return aggregationOperations;
    }

}

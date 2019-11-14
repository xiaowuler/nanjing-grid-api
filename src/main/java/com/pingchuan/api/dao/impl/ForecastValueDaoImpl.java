package com.pingchuan.api.dao.impl;

import com.pingchuan.api.contants.TimeFormat;
import com.pingchuan.api.dao.ForecastValueDao;
import com.pingchuan.api.dto.base.*;
import com.pingchuan.api.parameter.base.LineParameter;
import com.pingchuan.api.util.AggregationUtil;
import com.pingchuan.api.util.TimeFormatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.geo.GeoJsonLineString;
import org.springframework.data.mongodb.core.geo.GeoJsonMultiPoint;
import org.springframework.data.mongodb.core.query.Criteria;
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

    @Override
    public PointInfo findNJGridsByArea(Date updateTime, Date startTime, Date forecastTime, String areaCode, String elementCode, String forecastModel, double[] threshold, boolean isNeedElementCode) {

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
        if (!StringUtils.isEmpty(threshold))
            aggregationOperations.add(Aggregation.match(Criteria.where("value").gte(threshold[0]).lte(threshold[1])));

        aggregationOperations.add(Aggregation.project("area_name", "start_time", "update_time", "forecast_model", "forecast_time", "time_effect", "area_code", "loc", "grid_code").and("element_value").nested(bind("$element_code", "element_code").and("value")));
        aggregationOperations.add(Aggregation.group("area_name", "loc", "grid_code", "start_time", "update_time", "forecast_model", "forecast_time", "time_effect", "area_code").push("element_value").as("element_value"));
        aggregationOperations.add(Aggregation.project( "start_time", "update_time", "forecast_model", "forecast_time", "time_effect").and("locations").nested(bind("loc", "loc").and("grid_code").and("element_value").and("area_name").and("area_code")));
        aggregationOperations.add(Aggregation.group("start_time", "update_time", "forecast_model", "forecast_time", "time_effect").push("locations").as("locations"));

        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
        List<PointInfo> pointInfos = mongoTemplate.aggregate(aggregation, "elements", PointInfo.class).getMappedResults();
        if (pointInfos.size() == 0)
            return null;

        return pointInfos.get(0);
    }

    @Override
    public PointInfo findNJGridsByLocation(String elementCode, List<double[]> locations, Date startDate, Date updateDate, Date forecastDate, String forecastModel, double[] threshold, boolean isNeedElementCode) {

        List<PointInfo> pointInfos = new ArrayList<>();

        MatchOperation elementMatch = null;
        if (isNeedElementCode)
            elementMatch = Aggregation.match(Criteria.where("elements._id").is(elementCode));

        for (double[] loc : locations) {
            List<AggregationOperation> aggregationOperations = new ArrayList<>();

            GeoNearOperation trapezoidGeoNear = Aggregation.geoNear(NearQuery.near(loc[0], loc[1]).limit(1).spherical(true), "distance");
            ProjectionOperation trapezoidProject = project("loc", "trapezoid_info_id", "area_code", "area_name", "grid_code").andExclude("_id");
            aggregationOperations.add(trapezoidGeoNear);
            aggregationOperations.add(trapezoidProject);

            LookupOperation elementInfoLookup = Aggregation.lookup("element_infos", "trapezoid_info_id", "trapezoid_info_id", "element_infos");
            UnwindOperation elementInfoUnwind = Aggregation.unwind("element_infos");
            MatchOperation elementInfoMatch = Aggregation.match(Criteria.where("element_infos.start_time").is(startDate).and("element_infos.update_time").is(updateDate).and("element_infos.forecast_model").is(forecastModel));
            ProjectionOperation elementInfoProject = project("loc", "area_code", "area_name", "grid_code", "element_infos.start_time", "element_infos.update_time", "element_infos.element_code", "element_infos.forecast_model").and("element_infos._id").as("element_info_id");
            aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(elementInfoLookup, elementInfoUnwind, elementInfoMatch, elementInfoProject));

            LookupOperation elementLookup = Aggregation.lookup("elements", "element_code", "_id", "elements");
            UnwindOperation elementUnwind = Aggregation.unwind("elements");
            ProjectionOperation elementProjection = project("loc", "area_code", "area_name", "grid_code", "start_time", "update_time", "element_code", "forecast_model", "element_info_id");
            aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(elementLookup, elementUnwind, elementMatch, elementProjection));

            LookupOperation forecastInfoLookup = Aggregation.lookup("forecast_infos", "element_info_id", "element_info_id", "forecast_infos");
            UnwindOperation forecastInfoUnwind = Aggregation.unwind("forecast_infos");
            MatchOperation forecastInfoMatch = Aggregation.match(Criteria.where("forecast_infos.forecast_time").is(forecastDate).and("forecast_infos.is_finished").is(true));
            ProjectionOperation forecastInfoProject = project("loc", "area_code", "grid_code", "area_name", "start_time", "update_time", "forecast_model", "element_code", "forecast_infos.forecast_time", "forecast_infos.time_effect").and("forecast_infos._id").concat("$grid_code").as("element_value_id");
            aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(forecastInfoLookup, forecastInfoUnwind, forecastInfoMatch, forecastInfoProject));

            LookupOperation elementValueLookup = Aggregation.lookup(TimeFormatUtil.CovertDateToString(TimeFormat.ELEMENT_VALUES_NAME, startDate), "element_value_id", "_id", "element_values");
            UnwindOperation elementValueUnwind = Aggregation.unwind("element_values");
            ProjectionOperation elementValueProject = project("loc", "area_code", "grid_code", "area_name", "start_time", "update_time", "forecast_model", "element_code", "forecast_time", "time_effect", "element_values.value").andExclude("_id");
            aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(elementValueLookup, elementValueUnwind, null, elementValueProject));
            if (!StringUtils.isEmpty(threshold))
                aggregationOperations.add(Aggregation.match(Criteria.where("value").gte(threshold[0]).lte(threshold[1])));

            aggregationOperations.add(Aggregation.project("area_name", "start_time", "update_time", "forecast_model", "forecast_time", "time_effect", "area_code", "loc", "grid_code").and("element_value").nested(bind("$element_code", "element_code").and("value")));
            aggregationOperations.add(Aggregation.group("area_name", "loc", "grid_code", "start_time", "update_time", "forecast_model", "forecast_time", "time_effect", "area_code").push("element_value").as("element_value"));
            aggregationOperations.add(Aggregation.project( "start_time", "update_time", "forecast_model", "forecast_time", "time_effect").and("locations").nested(bind("loc", "loc").and("grid_code").and("element_value").and("area_name").and("area_code")));
            aggregationOperations.add(Aggregation.group("start_time", "update_time", "forecast_model", "forecast_time", "time_effect").push("locations").as("locations"));

            Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
            List<PointInfo> values = mongoTemplate.aggregate(aggregation, "trapezoids", PointInfo.class).getMappedResults();
            pointInfos.addAll(values);
        }

        return mergePointInfo(pointInfos);
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
    public LineInfo findNJGridsByForecastTimeRange(LineParameter line) {
        List<LineInfo> lineInfos = new ArrayList<>();

        MatchOperation elementMatch = null;
        if (line.isNeedElementCode())
            elementMatch = Aggregation.match(Criteria.where("elements._id").is(line.getElementCode()));

        for (double[] loc : line.getLocations()) {
            List<AggregationOperation> aggregationOperations = new ArrayList<>();

            //GeoNearOperation trapezoidGeoNear = Aggregation.geoNear(NearQuery.near(loc[0], loc[1]).limit(1).spherical(true), "distance");
            MatchOperation matchOperation = Aggregation.match(Criteria.where("loc").intersects(getGeoJsonMultiPoint(line.getLocations())));
            ProjectionOperation trapezoidProject = project("loc", "trapezoid_info_id", "area_code", "area_name", "grid_code").andExclude("_id");
            aggregationOperations.add(matchOperation);
            aggregationOperations.add(trapezoidProject);

            LookupOperation elementInfoLookup = Aggregation.lookup("element_infos", "trapezoid_info_id", "trapezoid_info_id", "element_infos");
            UnwindOperation elementInfoUnwind = Aggregation.unwind("element_infos");
            MatchOperation elementInfoMatch = Aggregation.match(Criteria.where("element_infos.start_time").is(line.getStartDate()).and("element_infos.update_time").is(line.getUpdateDate()).and("element_infos.forecast_model").is(line.getForecastModel()));
            ProjectionOperation elementInfoProject = project("loc", "area_code", "area_name", "grid_code", "element_infos.start_time", "element_infos.update_time", "element_infos.element_code", "element_infos.forecast_model").and("element_infos._id").as("element_info_id");
            aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(elementInfoLookup, elementInfoUnwind, elementInfoMatch, elementInfoProject));

            LookupOperation elementLookup = Aggregation.lookup("elements", "element_code", "_id", "elements");
            UnwindOperation elementUnwind = Aggregation.unwind("elements");
            ProjectionOperation elementProjection = project("loc", "area_code", "area_name", "grid_code", "start_time", "update_time", "element_code", "forecast_model", "element_info_id");
            aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(elementLookup, elementUnwind, elementMatch, elementProjection));

            LookupOperation forecastInfoLookup = Aggregation.lookup("forecast_infos", "element_info_id", "element_info_id", "forecast_infos");
            UnwindOperation forecastInfoUnwind = Aggregation.unwind("forecast_infos");
            MatchOperation forecastInfoMatch = Aggregation.match(Criteria.where("forecast_infos.forecast_time").gte(line.getStartForecastDate()).lt(line.getEndForecastDate()).and("forecast_infos.is_finished").is(true));
            ProjectionOperation forecastInfoProject = project("loc", "area_code", "grid_code", "area_name", "start_time", "update_time", "forecast_model", "element_code", "forecast_infos.forecast_time", "forecast_infos.time_effect").and("forecast_infos._id").concat("$grid_code").as("element_value_id");
            aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(forecastInfoLookup, forecastInfoUnwind, forecastInfoMatch, forecastInfoProject));

            LookupOperation elementValueLookup = Aggregation.lookup(TimeFormatUtil.CovertDateToString(TimeFormat.ELEMENT_VALUES_NAME, line.getStartDate()), "element_value_id", "_id", "element_values");
            UnwindOperation elementValueUnwind = Aggregation.unwind("element_values");
            ProjectionOperation elementValueProject = project("loc", "area_code", "grid_code", "area_name", "start_time", "update_time", "forecast_model", "element_code", "forecast_time", "time_effect", "element_values.value").andExclude("_id");
            aggregationOperations.addAll(AggregationUtil.SetAggregationOperation(elementValueLookup, elementValueUnwind, null, elementValueProject));

            aggregationOperations.add(Aggregation.project("area_name", "start_time", "update_time", "forecast_model", "forecast_time", "time_effect", "area_code", "loc", "grid_code").and("element_value").nested(bind("$element_code", "element_code").and("value")));
            aggregationOperations.add(Aggregation.group("area_name", "loc", "grid_code", "start_time", "update_time", "forecast_model", "forecast_time", "time_effect", "area_code").push("element_value").as("element_value"));
            aggregationOperations.add(Aggregation.project("start_time", "update_time", "forecast_model", "forecast_time", "time_effect").and("locations").nested(bind("loc", "loc").and("grid_code").and("area_code").and("area_name").and("element_value")));
            aggregationOperations.add(Aggregation.group("start_time", "update_time", "forecast_model", "forecast_time", "time_effect").push("locations").as("locations"));
            aggregationOperations.add(Aggregation.project("start_time", "update_time", "forecast_model").and("forecasts").nested(bind("forecast_time", "forecast_time").and("locations").and("time_effect")));
            aggregationOperations.add(Aggregation.group("start_time", "update_time", "forecast_model").push("forecasts").as("forecasts"));

            Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
            lineInfos.addAll(mongoTemplate.aggregate(aggregation, "trapezoids", LineInfo.class).getMappedResults());
        }
        return setLineInfo(lineInfos);
    }

    private GeoJsonMultiPoint getGeoJsonMultiPoint(List<double[]> locations){
        List<Point> points = new ArrayList<>();
        locations.forEach(l -> new Point(l[0], l[1]));
        GeoJsonMultiPoint geoJsonMultiPoint = new GeoJsonMultiPoint(points);
        return geoJsonMultiPoint;
    }

    private LineInfo setLineInfo(List<LineInfo> lineInfos){

        if (lineInfos.size() == 0)
            return null;

        List<ForecastInfo> forecastInfos = getAllForecastInfo(lineInfos);
        if (forecastInfos.size() == 0)
            return null;

        Map<Date, List<ForecastInfo>> map = forecastInfos.stream().collect(Collectors.groupingBy(l -> l.getForecastTime()));

        forecastInfos = new ArrayList<>();
        ForecastInfo forecastInfo;
        for(Date key : map.keySet()){
            if (map.get(key).size() == 0)
                continue;
            forecastInfo = map.get(key).get(0);
            forecastInfo.setLocations(map.get(key).stream().map(f -> f.getLocations().get(0)).collect(Collectors.toList()));
            forecastInfos.add(forecastInfo);
        }

        LineInfo lineInfo = lineInfos.get(0);
        lineInfo.setForecasts(forecastInfos);
        return lineInfo;
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

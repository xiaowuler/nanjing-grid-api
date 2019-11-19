package com.pingchuan.api.dao.impl;

import com.pingchuan.api.dao.TestDao;
import com.pingchuan.api.domain.Test;
import com.pingchuan.api.util.TimeFormatUtil;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @description: 测试类实现
 * @author: XW
 * @create: 2019-10-31 13:39
 **/

@Component
public class TestDaoImpl implements TestDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void testSearch() {
        
    }

    @Override
    public Document findLineBaseInfo(Date initialTime, double lon, double lat, String modeCode, String elementCode, String orgCode) {

        GeoNearOperation geoNearOperation = new GeoNearOperation(NearQuery.near(lon, lat).num(1).spherical(true), "distance");
        ProjectionOperation trapezProject = Aggregation.project("_id", "trapez_info_id", "index", "loc");

        LookupOperation trapezInfoLookup = Aggregation.lookup("trapez_infos", "trapez_info_id", "_id", "trapez_infos");
        ProjectionOperation trapezInfoProject = Aggregation.project("$trapez_infos.boundary", "$trapez_infos.resolution", "index", "loc", "$trapez_infos._id");

        LookupOperation elementInfoLookup = Aggregation.lookup("element_infos", "_id", "trapez_info_id", "element_infos");
        UnwindOperation elementInfoUnwind = new UnwindOperation(Fields.field("$element_infos"));
        MatchOperation elementInfoMatch = Aggregation.match(Criteria.where("element_infos.element_code").is(elementCode.toUpperCase()).and("element_infos.initial_time").is(initialTime));
        ProjectionOperation elementInfoProject = Aggregation.project("boundary", "resolution", "index", "loc", "$element_infos._id", "$element_infos.initial_time", "$element_infos.element_code", "$element_infos.org_code", "$element_infos.mode_code");

        //Aggregation aggregation = Aggregation.newAggregation(geoNearOperation, trapezProject, trapezInfoLookup, trapezInfoProject, elementInfoLookup, elementInfoUnwind, elementInfoMatch, elementInfoProject);
        Aggregation aggregation = Aggregation.newAggregation(geoNearOperation, trapezProject, trapezInfoLookup, trapezInfoProject, elementInfoLookup, elementInfoUnwind, elementInfoMatch, elementInfoMatch, elementInfoProject);
        List<Document> documents = mongoTemplate.aggregate(aggregation, "trapezes", Document.class).getMappedResults();
        if (documents.size() > 0)
            return documents.get(0);
        else
            return null;
    }

    @Override
    public List<Document> searchLine(Date initialTime, Date startTime, Date endTime, double lon, double lat, String modeCode, String elementCode, String orgCode) {
        GeoNearOperation geoNearOperation = new GeoNearOperation(NearQuery.near(lon, lat).num(1).spherical(true), "distance");
        ProjectionOperation trapezProject = Aggregation.project("_id", "trapez_info_id", "index", "loc");

        LookupOperation trapezInfoLookup = Aggregation.lookup("trapez_infos", "trapez_info_id", "_id", "trapez_infos");
        ProjectionOperation trapezInfoProject = Aggregation.project("$trapez_infos.boundary", "$trapez_infos.resolution", "index", "loc", "$trapez_infos._id");

        LookupOperation elementInfoLookup = Aggregation.lookup("element_infos", "_id", "trapez_info_id", "element_infos");
        UnwindOperation elementInfoUnwind = new UnwindOperation(Fields.field("$element_infos"));
        MatchOperation elementInfoMatch = Aggregation.match(Criteria.where("element_infos.element_code").is(elementCode.toUpperCase()).and("element_infos.initial_time").is(initialTime));
        ProjectionOperation elementInfoProject = Aggregation.project("boundary", "resolution", "index", "loc", "$element_infos._id", "$element_infos.initial_time", "$element_infos.element_code", "$element_infos.org_code", "$element_infos.mode_code");

        LookupOperation forecastInfoLookup = Aggregation.lookup("forecast_infos", "_id", "element_info_id", "forecast_infos");
        MatchOperation forecastInfoMatch = Aggregation.match(Criteria.where("forecast_infos.forecast_time").lt(startTime).gt(endTime));
        UnwindOperation forecastInfoUnwind = new UnwindOperation(Fields.field("$forecast_infos"));
        ProjectionOperation forecastInfoProject = Aggregation.project("boundary", "resolution", "loc", "initial_time", "element_code", "org_code", "mode_code", "$forecast_infos.forecast_time");
        forecastInfoProject = forecastInfoProject
                .and("$forecast_infos._id").concat("$index").as("element_value_id");

        LookupOperation elementValueLookup = Aggregation.lookup(TimeFormatUtil.CovertDateToString("yyyyMMdd", initialTime), "element_value_id", "_id", "element_values");
        ProjectionOperation elementValueProject = Aggregation.project("boundary", "resolution", "loc", "initial_time", "element_code", "org_code", "mode_code", "forecast_time", "$element_values.values").andExclude("_id");
        Aggregation aggregation = Aggregation.newAggregation(geoNearOperation, trapezProject, trapezInfoLookup, trapezInfoProject, elementInfoLookup, elementInfoUnwind, elementInfoMatch, elementInfoProject, forecastInfoLookup, forecastInfoUnwind, forecastInfoMatch, forecastInfoProject, elementValueLookup, elementValueProject);

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "trapezes", Document.class);
        List<Document> mappedResults = results.getMappedResults();
        return mappedResults;
    }

    @Override
    public List<Document> searchLine(Date initialTime, long elementInfoId, String index, Date startTime, Date endTime) {

        MatchOperation forecastInfoMatch = Aggregation.match(Criteria.where("element_info_id").is(elementInfoId).and("forecast_time").gte(startTime).lt(endTime));
        ProjectionOperation forecastInfoProject = Aggregation.project("forecast_time").and("_id").concat(index).as("element_value_id").andExclude("_id");
                ;
        LookupOperation elementValueLookup = Aggregation.lookup(TimeFormatUtil.CovertDateToString("yyyyMMdd", initialTime), "element_value_id", "_id", "element_values");
        ProjectionOperation elementValueProject = Aggregation.project("forecast_time", "$element_values.values").andExclude("_id");

        Aggregation aggregation = Aggregation.newAggregation(forecastInfoMatch, forecastInfoProject, elementValueLookup, elementValueProject);
        return mongoTemplate.aggregate(aggregation, "forecast_infos", Document.class).getMappedResults();
    }

    @Override
    public Document findRegionBaseInfo(Date initialTime, Date forecastTime, String modeCode, String elementCode, String orgCode) {
        MatchOperation elementInfoMatch = Aggregation.match(Criteria.where("mode_code").is(modeCode.toUpperCase()).and("element_code").is(elementCode.toUpperCase()).and("initial_time").is(initialTime));

        LookupOperation forecastInfoLookup = Aggregation.lookup("forecast_infos", "_id", "element_info_id", "forecast_infos");
        UnwindOperation forecastInfoUnwind = Aggregation.unwind("$forecast_infos");
        MatchOperation forecastInfoMatch = Aggregation.match(Criteria.where("forecast_infos.forecast_time").is(forecastTime));
        ProjectionOperation forecastInfoProject = Aggregation.project( "trapez_info_id", "$forecast_infos.forecast_time", "initial_time", "element_code", "org_code", "mode_code")
                .and("$forecast_infos._id").as("forecast_info_id").andExclude("_id");

        LookupOperation trapezInfoLookup = Aggregation.lookup("trapez_infos", "trapez_info_id", "_id", "trapez_infos");
        ProjectionOperation trapezInfoProject = Aggregation.project("forecast_info_id", "trapez_info_id", "forecast_time", "initial_time", "element_code", "org_code", "mode_code", "$trapez_infos.boundary", "$trapez_infos.resolution");

        LookupOperation trapezLookup = Aggregation.lookup("trapezes", "trapez_info_id", "trapez_info_id", "trapezes");
        UnwindOperation trapezUnwind = Aggregation.unwind("$trapezes");

        Aggregation aggregation = Aggregation.newAggregation(elementInfoMatch, forecastInfoLookup, forecastInfoUnwind, forecastInfoMatch, forecastInfoProject, trapezInfoLookup, trapezInfoProject);
        List<Test> documents = mongoTemplate.aggregate(aggregation, "element_infos", Test.class).getMappedResults();

        return null;
    }
}

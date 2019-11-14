package com.pingchuan.api.service.impl;

import com.pingchuan.api.dao.TestDao;
import com.pingchuan.api.domain.ElementValue;
import com.pingchuan.api.domain.Trapez;
import com.pingchuan.api.service.TestService;
import com.pingchuan.api.util.RemoveDollarOperation;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Polygon;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.geo.GeoJson;
import org.springframework.data.mongodb.core.geo.GeoJsonMultiPolygon;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * @description: 测试 服务处 实现
 * @author: XW
 * @create: 2019-10-31 13:41
 **/
@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private TestDao testDao;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void testSearch(Date startTime, Date endTime) {

        GeoNearOperation geoNearOperation = new GeoNearOperation(NearQuery.near(121, 33).limit(1).spherical(true), "distance");
        ProjectionOperation trapezProject = Aggregation.project("_id", "trapez_info_id", "index", "loc");

        LookupOperation trapezInfoLookup = getLookup("trapez_infos", "trapez_info_id", "_id", "trapez_infos");
        ProjectionOperation trapezInfoProject = Aggregation.project("$trapez_infos.boundary", "$trapez_infos.resolution", "index", "loc", "$trapez_infos._id");

        LookupOperation elementInfoLookup = getLookup("element_infos", "_id", "trapez_info_id", "element_infos");
        UnwindOperation elementInfoUnwind = new UnwindOperation(Fields.field("$element_infos"));
        MatchOperation elementInfoMatch = Aggregation.match(Criteria.where("element_infos.element_code").is("ER03").and("element_infos.initial_time").is(endTime));
        ProjectionOperation elementInfoProject = Aggregation.project("boundary", "resolution", "index", "loc", "$element_infos._id", "$element_infos.initial_time", "$element_infos.element_code", "$element_infos.org_code", "$element_infos.mode_code");

        LookupOperation forecastInfoLookup = getLookup("forecast_infos", "_id", "element_info_id", "forecast_infos");
        MatchOperation forecastInfoMatch = Aggregation.match(Criteria.where("forecast_infos.forecast_time").lt(startTime).gt(endTime));
        UnwindOperation forecastInfoUnwind = new UnwindOperation(Fields.field("$forecast_infos"));
        ProjectionOperation forecastInfoProject = Aggregation.project("boundary", "resolution", "loc", "initial_time", "element_code", "org_code", "mode_code", "$forecast_infos.forecast_time");
        forecastInfoProject = forecastInfoProject
                .and("$forecast_infos._id").concat("$index").as("element_value_id");

        LookupOperation elementValueLookup = getLookup("20191029", "element_value_id", "_id", "element_values");
        ProjectionOperation elementValueProject = Aggregation.project("boundary", "resolution", "loc", "initial_time", "element_code", "org_code", "mode_code", "forecast_time", "$element_values.values").andExclude("_id");
        Aggregation aggregation = Aggregation.newAggregation(geoNearOperation, trapezProject, trapezInfoLookup, trapezInfoProject, elementInfoLookup, elementInfoUnwind, elementInfoMatch, elementInfoProject, forecastInfoLookup, forecastInfoUnwind, forecastInfoMatch, forecastInfoProject, elementValueLookup, elementValueProject);

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "trapezes", Document.class);
        List<Document> mappedResults = results.getMappedResults();
    }

    public void testRegion(Date startTime, Date endTime){
        Point point = new Point(80, 30);
        Point point1 = new Point(130, 30);
        Point point2 = new Point(130, 80);
        //Point point4 = new Point(130, 50);
        Point point3 = new Point(80, 30);
        /*Polygon polygon = new Polygon(point, point1, point2, point3);
        Query query = new Query(Criteria.where("loc").within(polygon));
        GeoNearOperation geoNearOperation = new (query);*/
        /*Near*/

        MatchOperation elementInfoMatch = Aggregation.match(Criteria.where("mode_code").is("spcc".toUpperCase()).and("element_code").is("tmp".toUpperCase()).and("initial_time").is(endTime));

        LookupOperation forecastInfoLookup = getLookup("forecast_infos", "_id", "element_info_id", "forecast_infos");
        UnwindOperation forecastInfoUnwind = Aggregation.unwind("$forecast_infos");
        MatchOperation forecastInfoMatch = Aggregation.match(Criteria.where("forecast_infos.forecast_time").is(startTime));
        ProjectionOperation forecastInfoProject = Aggregation.project( "trapez_info_id", "$forecast_info.forecast_time", "initial_time", "$element_infos.element_code", "org_code", "mode_code")
                .and("$forecast_infos._id").as("forecast_info_id").andExclude("_id");

        LookupOperation trapezInfoLookup = Aggregation.lookup("trapez_infos", "trapez_info_id", "_id", "trapez_infos");
        ProjectionOperation trapezInfoProject = Aggregation.project("trapez_info_id", "$forecast_info.forecast_time", "initial_time", "element_code", "org_code", "mode_code", "forecast_info_id", "trapez_info.boundary");

        LookupOperation trapezLookup = Aggregation.lookup("trapezes", "trapez_info_id", "trapez_info_id", "trapezes");
        UnwindOperation trapezUnwind = Aggregation.unwind("$trapezes");

        //UnwindOperation elementInfoUnwind = new UnwindOperation(Fields.field("$element_infos"));
        //MatchOperation elementInfoMatch = Aggregation.match(Criteria.where("element_infos.element_code").is("ER03").and("element_infos.initial_time").is(endTime));
        //ProjectionOperation elementInfoProject = Aggregation.project("boundary", "resolution", "index", "loc", "$element_infos._id", "$element_infos.initial_time", "$element_infos.element_code", "$element_infos.org_code", "$element_infos.mode_code");

        GeoJsonPolygon geoJsonPolygon = new GeoJsonPolygon(point, point1, point2, point3);
        MatchOperation trapezMatch = Aggregation.match(Criteria.where("trapezes.loc").within(geoJsonPolygon));
        ProjectionOperation trapezProject = Aggregation.project("forecast_time", "initial_time", "element_code", "org_code", "mode_code", "boundary", "$trapezes.loc").and("forecast_info_id").concat("$trapezes.index").as("element_value_id").andExclude("_id");

        LookupOperation elementValueLookup = Aggregation.lookup("element_values", "element_value_id", "_id", "element_values");
        ProjectionOperation elementValueProject = Aggregation.project("loc", "$element_values.values");

        Aggregation aggregation = Aggregation.newAggregation(elementInfoMatch, forecastInfoLookup, forecastInfoUnwind, forecastInfoMatch, forecastInfoProject, trapezInfoLookup, trapezInfoProject, trapezLookup, trapezUnwind, trapezMatch, trapezProject, elementValueLookup, elementValueProject);


        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "element_infos", Document.class);
        List<Document> mappedResults = results.getMappedResults();
    }

    @Override
    public Document searchLine(Date initialTime, Date startTime, Date endTime, double lon, double lat, String modeCode, String elementCode, String orgCode) {
        Document document = testDao.findLineBaseInfo(initialTime, lon, lat, modeCode, elementCode, orgCode);
        if (StringUtils.isEmpty(document))
            return null;

        long elementInfoId = (long)document.get("_id");
        String index = document.get("index").toString();
        List<Document> documents = testDao.searchLine(initialTime, elementInfoId, index, startTime, endTime);
        //document.put("values", document);
        return null;
    }

    @Override
    public Document searchRegion(Date initialTime, Date forecastTime, double startLon, double endLon, double startLat, double endLat, String modeCode, String elementCode, String orgCode) {
        Document document = testDao.findRegionBaseInfo(initialTime, forecastTime, modeCode, elementCode, orgCode);

        return null;
    }

    private LookupOperation getLookup(String from, String localField, String foreignField, String as){
        return LookupOperation.newLookup()
                .from(from)
                .localField(localField)
                .foreignField(foreignField)
                .as(as);
    }
}

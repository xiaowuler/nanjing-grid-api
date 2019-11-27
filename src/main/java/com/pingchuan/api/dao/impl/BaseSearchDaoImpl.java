package com.pingchuan.api.dao.impl;

import com.pingchuan.api.dao.BaseSearchDao;
import com.pingchuan.api.dao.ElementInfoDao;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class BaseSearchDaoImpl implements BaseSearchDao {

    @Autowired
    private ElementInfoDao elementInfoDao;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void findNJGridsByArea(Date startDate, Date updateDate, String elementCode) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        aggregationOperations.addAll(elementInfoDao.findByStartTimeAndUpdateTimeAndElementCode(startDate, updateDate, elementCode));

        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
        List<Document> documents = mongoTemplate.aggregate(aggregation, "elements", Document.class).getMappedResults();
    }
}

package com.pingchuan.api.util;

import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 聚合工具类
 * @author: XW
 * @create: 2019-11-13 16:15
 **/
public class AggregationUtil {

    public static List<AggregationOperation> SetAggregationOperation(LookupOperation lookupOperation, UnwindOperation unwindOperation, MatchOperation matchOperation, ProjectionOperation projectionOperation) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();

        if (!StringUtils.isEmpty(lookupOperation))
            aggregationOperations.add(lookupOperation);

        if (!StringUtils.isEmpty(unwindOperation))
            aggregationOperations.add(unwindOperation);

        if (!StringUtils.isEmpty(matchOperation))
            aggregationOperations.add(matchOperation);

        if (!StringUtils.isEmpty(projectionOperation))
            aggregationOperations.add(projectionOperation);

        return aggregationOperations;
    }

}

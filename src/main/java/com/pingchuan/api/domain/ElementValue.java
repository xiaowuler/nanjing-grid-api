package com.pingchuan.api.domain;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @description: 要素 值
 * @author: XW
 * @create: 2019-10-31 14:12
 **/

@Data
public class ElementValue {

    @Id
    private ObjectId id;

    @Field("forecast_info_id")
    private ObjectId forecastInfoId;

    @Field("trapez_id")
    private ObjectId trapezId;

    private Double value;

    @Field("u_value")
    private Double uValue;

    @Field("v_value")
    private Double vValue;
}

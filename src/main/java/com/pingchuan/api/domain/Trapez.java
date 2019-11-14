package com.pingchuan.api.domain;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * @description: 位置类
 * @author: XW
 * @create: 2019-10-31 14:15
 **/

@Data
public class Trapez {

    @Id
    private ObjectId id;

    @Field("trapez_info_id")
    private ObjectId trapezInfoId;

    private Double[] loc;

    @Field("station_code")
    private String stationCode;

    private String department;

    @Field("station_class")
    private String stationClass;

    private Double altitude;

    private String description;

    @DBRef
    private List<ElementValue> elementValues;

}

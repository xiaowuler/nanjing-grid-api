package com.pingchuan.api.domain;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * @description: 测试实体类
 * @author: XW
 * @create: 2019-11-05 09:36
 **/

@Data
public class Test {

    @Field("initial_time")
    private Date initialTime;

    @Field("element_code")
    private String elementCode;

    @Field("mode_code")
    private String modeCode;

    @Field("org_code")
    private String orgCode;

    @Field("trapezoid_info_id")
    private String trapezoidInfoId;

    @Field("forecast_time")
    private Date forecastTime;

    @Field("forecast_info_id")
    private String forecastInfoId;

}

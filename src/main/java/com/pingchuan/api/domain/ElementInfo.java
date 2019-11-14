package com.pingchuan.api.domain;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * @description: 元素简略信息类
 * @author: XW
 * @create: 2019-10-31 15:54
 **/

@Data
public class ElementInfo {

    @Id
    private long ticks;

    @Field("initial_time")
    private Date initialTime;

    @Field("element_code")
    private String elementCode;

    @Field("createTime")
    private Date createTime;

    @Field("mode_code")
    private String modeCode;

    @Field("org_code")
    private String orgCode;

    @Field("trapezoid_info_id")
    private ObjectId trapezInfoId;

    @Field("forecast_level")
    private Double forecastLevel;

    @Field("forecast_periods")
    private long forecastPeriods;

    @Field("forecast_interval")
    private long forecastInterval;

}

package com.pingchuan.api.domain;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @description: 点数据 实体类
 * @author: XW
 * @create: 2019-11-07 18:03
 **/

@Data
public class PointValue {

    @Field("area_code")
    private String areaCode;

    @Field("area_name")
    private String areaName;

    @Field("element_code")
    private String elementCode;

    @Field("grid_code")
    private String gridCode;

    private double[] loc;

    private double value;

    @Field("forecast_time")
    private Date forecastTime;

    @Field("update_time")
    private Date updateTime;

    @Field("start_time")
    private Date startTime;

    @Field("forecast_model")
    private String forecastModel;

    @Field("time_effect")
    private int timeEffect;



}

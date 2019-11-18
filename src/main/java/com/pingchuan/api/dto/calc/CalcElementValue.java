package com.pingchuan.api.dto.calc;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

/**
 * @description: 最大 最小返回值
 * @author: XW
 * @create: 2019-11-13 17:35
 **/

@Data
public class CalcElementValue {

    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @Field("start_time")
    private Date startTime;

    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @Field("forecast_time")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date forecastTime;

    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @Field("update_time")
    private Date updateTime;

    @Field("forecast_model")
    private String forecastModel;

    @Field("time_effect")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer timeEffect;

    @Field("area_code")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String areaCode;

    @Field("area_name")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String areaName;

    @Field("grid_code")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String gridCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private double[] loc;

    @Field("element_code")
    private String elementCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double value;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Location> locations;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Forecast> forecasts;
}

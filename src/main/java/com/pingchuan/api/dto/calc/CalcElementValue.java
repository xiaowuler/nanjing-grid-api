package com.pingchuan.api.dto.calc;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * @description: 最大 最小返回值
 * @author: XW
 * @create: 2019-11-13 17:35
 **/

@Data
public class CalcElementValue {

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @Field("start_time")
    private Date startTime;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @Field("forecast_time")
    private Date forecastTime;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @Field("update_time")
    private Date updateTime;

    @Field("forecast_model")
    private String forecastModel;

    @Field("time_effect")
    private int timeEffect;

    @Field("area_code")
    private String areaCode;

    @Field("area_name")
    private String areaName;

    @Field("grid_code")
    private String gridCode;

    private double[] loc;

    @Field("element_code")
    private String elementCode;

    private double value;
}

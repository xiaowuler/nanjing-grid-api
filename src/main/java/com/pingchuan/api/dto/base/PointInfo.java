package com.pingchuan.api.dto.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

/**
 * @description: 共有属性
 * @author: XW
 * @create: 2019-11-12 14:49
 **/

@Data
public class PointInfo {

    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @Field("start_time")
    private Date startTime;

    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @Field("forecast_time")
    private Date forecastTime;

    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @Field("update_time")
    private Date updateTime;

    @Field("forecast_model")
    private String forecastModel;

    @Field("time_effect")
    private int timeEffect;

    private List<PointLocation> locations;

}

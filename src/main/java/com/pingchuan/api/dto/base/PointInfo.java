package com.pingchuan.api.dto.base;

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

    private List<PointLocation> locations;

}

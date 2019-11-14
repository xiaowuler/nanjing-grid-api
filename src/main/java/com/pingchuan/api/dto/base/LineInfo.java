package com.pingchuan.api.dto.base;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

/**
 * @description: 线数据实体类
 * @author: XW
 * @create: 2019-11-12 17:54
 **/

@Data
public class LineInfo {
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @Field("start_time")
    private Date startTime;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @Field("update_time")
    private Date updateTime;

    @Field("forecast_model")
    private String forecastModel;

    private List<ForecastInfo> forecasts;
}

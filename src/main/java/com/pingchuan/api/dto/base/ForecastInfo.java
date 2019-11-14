package com.pingchuan.api.dto.base;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

/**
 * @description: 预报实体类
 * @author: XW
 * @create: 2019-11-13 09:57
 **/

@Data
public class ForecastInfo {

    @Field("forecast_time")
    private Date forecastTime;

    @Field("time_effect")
    private int timeEffect;

    List<LineLocation> locations;

}

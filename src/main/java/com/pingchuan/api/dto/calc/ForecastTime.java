package com.pingchuan.api.dto.calc;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
public class ForecastTime {
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @Field("forecast_time")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date forecastTime;

    @Field("time_effect")
    private int timeEffect;
}

package com.pingchuan.api.dto.calc;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class Forecast {

    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @Field("forecast_time")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date forecastTime;

    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @Field("start_forecast_time")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date startForecastTime;

    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @Field("end_forecast_time")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date endForecastTime;

    @Field("time_effect")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer timeEffect;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Location> locations;
}

package com.pingchuan.api.dto.calc;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
public class CalcValue {
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @Field("start_forecast_time")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date startForecastTime;

    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @Field("end_forecast_time")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date endForecastTime;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TotalValue min;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TotalValue max;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double avg;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double sum;

}

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
public class Location {

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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double sum;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double avg;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ElementValue> values;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TotalValue min;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TotalValue max;

    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @Field("start_forecast_time")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date startForecastTime;

    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @Field("end_forecast_time")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date endForecastTime;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Day> days;
}

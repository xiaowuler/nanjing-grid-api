package com.pingchuan.api.dto.calc;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Data
public class TotalCalc {

    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @Field("start_time")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date startTime;

    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @Field("update_time")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date updateTime;

    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @Field("forecast_time")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date forecastTime;

    @Field("time_effect")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int timeEffect;

    @Field("element_code")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String elementCode;

    @Field("forecast_model")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String forecastModel;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Location> locations;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double avg;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TotalValue min;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TotalValue max;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TotalValue sum;
}

package com.pingchuan.api.dto.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
public class Element {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private double[] threshold;

    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @Field("start_time")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date startTime;

    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @Field("update_time")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date updateTime;

    @Field("forecast_model")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String forecastModel;

    @Field("element_codes")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ElementCode> elementCodes;

}

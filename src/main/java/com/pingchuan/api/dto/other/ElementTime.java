package com.pingchuan.api.dto.other;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
public class ElementTime {

    @Field("forecast_model")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String forecastModel;

    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @Field("forecast_time")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date forecastTime;

    @Field("element_codes")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ElementCode> elementCodes;
}

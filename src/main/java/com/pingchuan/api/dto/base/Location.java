package com.pingchuan.api.dto.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
public class Location {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private double[] loc;

    @Field("grid_code")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String gridCode;

    @Field("area_code")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String areaCode;

    @Field("area_name")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String areaName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double value;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<Forecast> forecasts;

}

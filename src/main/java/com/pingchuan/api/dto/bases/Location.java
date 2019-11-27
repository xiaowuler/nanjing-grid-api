package com.pingchuan.api.dto.bases;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
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

    @Field("loc")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private double[] loc;

    @Field("value")
    private Double value;
}

package com.pingchuan.api.dto.other;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
public class Trapezoid {

    @Field("area_code")
    private String areaCode;

    @Field("area_name")
    private String areaName;

    @Field("grid_code")
    private String gridCode;

    private double[] loc;
}

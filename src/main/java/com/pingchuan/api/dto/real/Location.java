package com.pingchuan.api.dto.real;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.pingchuan.api.dto.calc.Day;
import com.pingchuan.api.dto.calc.ElementValue;
import com.pingchuan.api.dto.calc.TotalValue;
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
    private List<Real> reals;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double value;
}

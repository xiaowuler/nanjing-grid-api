package com.pingchuan.api.dto.calc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pingchuan.api.dto.base.Location;
import lombok.Data;

import java.util.List;

@Data
public class TotalValue {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double value;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Location> locations;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Forecast> forecasts;
}

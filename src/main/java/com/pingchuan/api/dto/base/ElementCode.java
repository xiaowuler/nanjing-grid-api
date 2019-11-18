package com.pingchuan.api.dto.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
public class ElementCode {

    @Field("element_code")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String elementCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Location> locations;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<Forecast> forecasts;
}

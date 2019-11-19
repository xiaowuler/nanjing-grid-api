package com.pingchuan.api.dto.calc;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class Day {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String day;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<CalcValue> values;
}

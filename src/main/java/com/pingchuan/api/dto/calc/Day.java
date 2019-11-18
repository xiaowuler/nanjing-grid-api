package com.pingchuan.api.dto.calc;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class Day {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String day;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<CalcValue> values;
}

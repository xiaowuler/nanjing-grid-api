package com.pingchuan.api.dto.other;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
public class ElementCode {

    @Field("element_code")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String elementCode;

    @Field("start_time")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private StartTime startTime;

    @Field("start_times")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<StartTime> startTimes;
}

package com.pingchuan.api.dto.other;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
public class UpdateTime {

    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @Field("update_time")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date updateTime;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<Forecast> forecasts;
}

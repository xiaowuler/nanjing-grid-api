package com.pingchuan.api.dto.other;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
public class StartTime {

    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @Field("start_time")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date startTime;

    @Field("update_time")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UpdateTime updateTime;

    @Field("update_times")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<UpdateTime> updateTimes;

    @Field("update_dates")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private List<Date> updateDates;

}

package com.pingchuan.api.parameter.other;

import com.alibaba.fastjson.annotation.JSONField;
import com.pingchuan.api.parameter.Parameter;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ForecastTimeParameter extends BaseParameter implements Parameter {

    private String forecastTime;

    @JSONField(serialize = false)
    private Date forecastDate;

    @Override
    public String getAreaCode() {
        return null;
    }

    @Override
    public List<String> checkCode(boolean isNeedElementCode) {
        super.checkCode(isNeedElementCode);

        forecastDate = check.checkTime(forecastTime, "forecastTime");

        return check.errors;
    }
}

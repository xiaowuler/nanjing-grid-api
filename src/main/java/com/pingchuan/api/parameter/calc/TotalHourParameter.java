package com.pingchuan.api.parameter.calc;

import com.alibaba.fastjson.annotation.JSONField;
import com.pingchuan.api.parameter.BaseParameter;
import com.pingchuan.api.parameter.CheckParameter;
import com.pingchuan.api.parameter.Parameter;
import lombok.Data;

import java.util.List;

/**
 * @author xiaowuler
 */
@Data
public class TotalHourParameter extends BaseParameter implements Parameter {

    private String calcType;

    private String location;

    @JSONField(serialize = false)
    private int totalHour;

    @JSONField(serialize = false)
    private int timeInterval;

    @JSONField(serialize = false)
    private List<double[]> locations;

    @Override
    public String getAreaCode() {
        return null;
    }

    @Override
    public List<String> checkCode(boolean isNeedElementCode) {
        totalHour = 72;
        forecastModel = "NJGRID-STANDARD";

        super.checkCode(false);
        locations = check.checkLocation(location);
        return check.errors;
    }
}

package com.pingchuan.api.parameter.base;

import com.pingchuan.api.parameter.BaseParameter;
import com.pingchuan.api.parameter.Parameter;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @description: 位置 阈值 查询
 * @author: XW
 * @create: 2019-11-13 14:32
 **/

@Data
public class ThresholdLocationParameter extends BaseParameter implements Parameter {

    private String location;

    private List<double[]> locations;

    private String forecastTime;

    private String threshold;

    private List<double[]> thresholdValues;

    private Date forecastDate;

    @Override
    public List<String> checkCode(boolean isNeedElementCode) {
        super.checkCode(isNeedElementCode);

        forecastDate = check.checkTime(forecastTime, "forecastTime");
        thresholdValues = check.checkThreshold(threshold);
        locations = check.checkLocation(location);

        return check.errors;
    }

    @Override
    public String getAreaCode() {
        return null;
    }
}

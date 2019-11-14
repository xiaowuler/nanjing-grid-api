package com.pingchuan.api.parameter.base;

import com.pingchuan.api.parameter.BaseParameter;
import com.pingchuan.api.parameter.Parameter;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @description: 阈值查询 实体类
 * @author: XW
 * @create: 2019-11-13 11:51
 **/

@Data
public class ThresholdAreaParameter extends BaseParameter implements Parameter {

    private String areaCode;

    private String forecastTime;

    private String threshold;

    private List<double[]> thresholdValues;

    private Date forecastDate;

    @Override
    public List<String> checkCode(boolean isNeedElementCode) {
        super.checkCode(isNeedElementCode);

        areaCode = check.checkString(areaCode, "areaCode");
        forecastDate = check.checkTime(forecastTime, "forecastTime");
        thresholdValues = check.checkThreshold(threshold);

        return check.errors;
    }
}

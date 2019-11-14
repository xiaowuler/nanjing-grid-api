package com.pingchuan.api.parameter.calc;

import com.pingchuan.api.parameter.BaseParameter;
import com.pingchuan.api.parameter.Parameter;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @description: 预报时间 参数类
 * @author: XW
 * @create: 2019-11-13 15:46
 **/

@Data
public class ForecastParameter extends BaseParameter implements Parameter {

    private String calcType;

    private String areaCode;

    private String forecastTime;

    private Date forecastDate;

    @Override
    public List<String> checkCode(boolean isNeedElementCode) {
        super.checkCode(isNeedElementCode);

        areaCode = check.checkString(areaCode, "areaCode");
        forecastDate = check.checkTime(forecastTime, "forecastTime");

        return check.errors;
    }
}

package com.pingchuan.api.parameter.calc;

import com.pingchuan.api.parameter.BaseParameter;
import com.pingchuan.api.parameter.Parameter;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * @description: 预报 时效
 * @author: XW
 * @create: 2019-11-13 17:59
 **/

@Data
public class TimeEffectParameter extends BaseParameter implements Parameter {

    private String calcType;

    private String areaCode;

    private String timeEffect;

    private Date forecastDate;

    private Integer times;

    @Override
    public List<String> checkCode(boolean isNeedElementCode) {
        super.checkCode(isNeedElementCode);

        areaCode = check.checkString(areaCode, "areaCode");
        times = check.checkTimeEffect(timeEffect);

        if (!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(times));
        forecastDate = check.ConvertForecastTime(startDate, times);

        return check.errors;
    }
}

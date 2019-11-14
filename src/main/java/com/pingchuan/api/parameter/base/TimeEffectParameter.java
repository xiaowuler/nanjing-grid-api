package com.pingchuan.api.parameter.base;

import com.pingchuan.api.parameter.BaseParameter;
import com.pingchuan.api.parameter.Parameter;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * @description: 时效 实体参数类
 * @author: XW
 * @create: 2019-11-11 11:11
 **/

@Data
public class TimeEffectParameter extends BaseParameter implements Parameter {

    private String timeEffect;

    private Integer times;

    private String areaCode;

    private Date forecastDate;

    @Override
    public List<String> checkCode(boolean isNeedElementCode) {

        super.checkCode(isNeedElementCode);

        times = check.checkTimeEffect(timeEffect);

        if (!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(times));
            forecastDate = check.ConvertForecastTime(startDate, times);

        areaCode = check.checkString(areaCode, "areaCode");

        return check.errors;
    }
}

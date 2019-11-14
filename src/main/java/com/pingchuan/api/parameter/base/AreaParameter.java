package com.pingchuan.api.parameter.base;

import com.alibaba.fastjson.annotation.JSONField;
import com.pingchuan.api.parameter.BaseParameter;
import com.pingchuan.api.parameter.Parameter;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @description: findPointNJGridsByArea 参数类
 * @author: XW
 * @create: 2019-11-07 15:41
 **/

@Data
public class AreaParameter extends BaseParameter implements Parameter {

    private String areaCode;

    private String forecastTime;

    @JSONField(serialize = false)
    private Date forecastDate;

    @Override
    public List<String> checkCode(boolean isNeedElementCode) {

        super.checkCode(isNeedElementCode);

        forecastDate = check.checkTime(forecastTime, "forecastTime");
        areaCode = check.checkString(areaCode, "areaCode");

        return check.errors;
    }

}

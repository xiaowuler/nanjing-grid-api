package com.pingchuan.api.parameter.base;
import com.pingchuan.api.parameter.BaseParameter;
import com.pingchuan.api.parameter.Parameter;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @description: 位置 参数 实体类
 * @author: XW
 * @create: 2019-11-08 13:22
 **/

@Data
public class LocationParameter extends BaseParameter implements Parameter {

    private String location;

    private String forecastTime;

    private Date forecastDate;

    private List<double[]> locations;

    @Override
    public List<String> checkCode(boolean isNeedElementCode) {

        super.checkCode(isNeedElementCode);

        forecastDate = check.checkTime(forecastTime, "forecastTime");
        locations = check.checkLocation(location);

        return check.errors;
    }

    @Override
    public String getAreaCode() {
        return null;
    }
}

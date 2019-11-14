package com.pingchuan.api.parameter.base;

import com.pingchuan.api.parameter.BaseParameter;
import com.pingchuan.api.parameter.Parameter;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @description: 时间段 内数据
 * @author: XW
 * @create: 2019-11-08 15:57
 **/

@Data
public class LineParameter extends BaseParameter implements Parameter {

    private String startForecastTime;

    private String endForecastTime;

    private Date startForecastDate;

    private Date endForecastDate;

    private String location;

    private List<double[]> locations;

    @Override
    public List<String> checkCode(boolean isNeedElementCode) {

        super.checkCode(isNeedElementCode);

        locations = check.checkLocation(location);
        startForecastDate = check.checkTime(startForecastTime, "startForecastTime");
        endForecastDate = check.checkTime(endForecastTime, "endForecastTime");

        return check.errors;
    }

    @Override
    public String getAreaCode() {
        return null;
    }


}

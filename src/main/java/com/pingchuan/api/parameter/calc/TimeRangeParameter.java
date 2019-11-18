package com.pingchuan.api.parameter.calc;

import com.pingchuan.api.parameter.BaseParameter;
import com.pingchuan.api.parameter.Parameter;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: XW
 * @create: 2019-11-13 18:14
 **/

@Data
public class TimeRangeParameter extends BaseParameter implements Parameter {

    private String calcType;

    private String location;

    private String startForecastTime;

    private String endForecastTime;

    private Date startForecastDate;

    private Date endForecastDate;

    private List<double[]> locations;

    @Override
    public String getAreaCode() {
        return null;
    }

    @Override
    public List<String> checkCode(boolean isNeedElementCode) {
        super.checkCode(isNeedElementCode);

        startForecastDate = check.checkTime(this.startForecastTime, "startForecastTime");
        endForecastDate = check.checkTime(this.endForecastTime, "endForecastTime");
        locations = check.checkLocation(location);

        return check.errors;
    }
}

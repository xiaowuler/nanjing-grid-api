package com.pingchuan.api.parameter.calc;

import lombok.Data;

import java.util.Date;

/**
 * @description:
 * @author: XW
 * @create: 2019-11-13 18:14
 **/

@Data
public class ForecastLocationParameter {

    private String calcType;

    private String location;

    private String startForecastTime;

    private String endForecastTime;

    private Date startForecastDate;

    private Date endForecastDate;

    private double[] locations;
}

package com.pingchuan.api.dto.base;

import lombok.Data;

/**
 * @description: 阈值 dto
 * @author: XW
 * @create: 2019-11-13 14:11
 **/
@Data
public class ThresholdInfo {

    private double[] threshold;

    private PointInfo elements;
}

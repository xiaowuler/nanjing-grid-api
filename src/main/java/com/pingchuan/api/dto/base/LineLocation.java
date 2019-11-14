package com.pingchuan.api.dto.base;

import com.pingchuan.api.dto.ElementValue;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * @description: 线 数据
 * @author: XW
 * @create: 2019-11-13 10:32
 **/

@Data
public class LineLocation {

    @Field("area_code")
    private String areaCode;

    @Field("area_name")
    private String areaName;

    @Field("grid_code")
    private String gridCode;

    private double[] loc;

    @Field("element_value")
    private List<ElementValue> elementValues;
}

package com.pingchuan.api.dto;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @description: 值
 * @author: XW
 * @create: 2019-11-12 14:26
 **/

@Data
public class ElementValue {

    @Field("element_code")
    private String elementCode;

    private Double value;

}

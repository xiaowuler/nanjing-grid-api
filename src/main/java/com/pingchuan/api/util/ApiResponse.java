package com.pingchuan.api.util;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @description: 接口返回类
 * @author: XW
 * @create: 2019-11-11 13:58
 **/

@Data
@AllArgsConstructor
public class ApiResponse {

    private Integer retCode;

    private String retMsg;

    private Object data;

}

package com.pingchuan.api.model;

import lombok.Data;

/**
 * @description: 接口 实体类
 * @author: XW
 * @create: 2019-11-11 18:13
 **/

@Data
public class Interface {

    private Integer id;

    private String name;

    private Integer typeId;

    private String explain;

    private byte enabled;
}

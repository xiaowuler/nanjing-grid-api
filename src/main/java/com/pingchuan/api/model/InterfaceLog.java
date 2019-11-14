package com.pingchuan.api.model;

import lombok.Data;

/**
 * @description: 日志类
 * @author: XW
 * @create: 2019-11-11 11:47
 **/
@Data
public class InterfaceLog {
    private Integer id;

    private Integer interfaceId;

    private String parameters;

    private byte state;

    private String callerCode;

    private String errorMessage;

    private Long startTime;

    private Long endTime;

    private Long createTime;

    private Long stopTime;

    private String requestType;

    private String hostAddress;

    private String regionCode;
}

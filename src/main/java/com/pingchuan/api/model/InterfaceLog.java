package com.pingchuan.api.model;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

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

    private Timestamp requestStartTime;

    private Timestamp executeStartTime;

    private Timestamp executeEndTime;

    private Timestamp requestEndTime;

    private String requestType;

    private String hostAddress;

    private String regionCode;

    private Integer resultCode;
}

package com.pingchuan.api.model;

import lombok.Data;

/**
 * @description: 调用者
 * @author: XW
 * @create: 2019-11-11 13:31
 **/
@Data
public class Caller {

    private String code;

    private String department;

    private String loginName;

    private String loginPassword;

    private String role;

    private String url;

    private byte enabled;

    private String realName;

}

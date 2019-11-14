package com.pingchuan.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @description: 立牌
 * @author: XW
 * @create: 2019-11-11 14:48
 **/

@Data
@AllArgsConstructor
public class Token {

    private String callerCode;

    private String token;
}

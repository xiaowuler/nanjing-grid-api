package com.pingchuan.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @description: 用户返回 DTO
 * @author: XW
 * @create: 2019-11-11 14:10
 **/

@Data
@AllArgsConstructor
public class UserDTO {

    private String username;

    private String realName;

    private String token;
}

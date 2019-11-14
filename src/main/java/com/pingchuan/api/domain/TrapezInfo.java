package com.pingchuan.api.domain;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @description: 位置 信息 类
 * @author: XW
 * @create: 2019-10-31 16:02
 **/

@Data
public class TrapezInfo {

    @Id
    private ObjectId id;

    @DBRef
    private Boundary boundary;

    @DBRef
    private Resolution resolution;

}

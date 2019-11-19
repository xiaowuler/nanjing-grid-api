package com.pingchuan.api.mapper;

import com.pingchuan.api.model.Interface;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface InterfaceMapper {
    Interface findOneById(@Param("id") int id);

    void updateOne(@Param("id") Integer interfaceId, @Param("state") byte state);
}

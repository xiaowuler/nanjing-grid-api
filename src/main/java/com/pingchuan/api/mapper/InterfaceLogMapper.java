package com.pingchuan.api.mapper;

import com.pingchuan.api.model.InterfaceLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InterfaceLogMapper {
    List<InterfaceLog> findAll();

    void insertOne(@Param("interface") InterfaceLog interfaceLog);
}

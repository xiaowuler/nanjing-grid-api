package com.pingchuan.api.dao;

import com.pingchuan.api.model.InterfaceLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InterfaceLogService {
    List<InterfaceLog> findAll();

    void insertOne(@Param("interfaceLog") InterfaceLog interfaceLog);
}

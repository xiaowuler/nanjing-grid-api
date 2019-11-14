package com.pingchuan.api.dao.impl;

import com.pingchuan.api.dao.InterfaceLogService;
import com.pingchuan.api.mapper.InterfaceLogMapper;
import com.pingchuan.api.model.InterfaceLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description: 接口日志 服务层
 * @author: XW
 * @create: 2019-11-11 12:30
 **/

@Service
@Transactional
public class InterfaceLogServiceImpl implements InterfaceLogService {

    @Autowired
    private InterfaceLogMapper interfaceLogMapper;

    @Override
    public List<InterfaceLog> findAll() {
        return interfaceLogMapper.findAll();
    }

    @Override
    public void insertOne(InterfaceLog interfaceLog) {
        interfaceLogMapper.insertOne(interfaceLog);
    }
}

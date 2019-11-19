package com.pingchuan.api.service.impl;

import com.pingchuan.api.mapper.InterfaceMapper;
import com.pingchuan.api.model.Interface;
import com.pingchuan.api.service.InterfaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description: 接口 查询 服务层
 * @author: XW
 * @create: 2019-11-11 20:06
 **/

@Service
@Transactional
public class InterfaceServiceImpl implements InterfaceService {

    @Autowired
    private InterfaceMapper interfaceMapper;

    @Override
    public Interface findOneById(int id) {
        return interfaceMapper.findOneById(id);
    }
}

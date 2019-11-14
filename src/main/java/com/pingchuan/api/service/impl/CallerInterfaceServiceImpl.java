package com.pingchuan.api.service.impl;

import com.pingchuan.api.mapper.CallerInterfaceMapper;
import com.pingchuan.api.model.CallerInterface;
import com.pingchuan.api.service.CallerInterfaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description: 服务层
 * @author: XW
 * @create: 2019-11-11 20:34
 **/

@Service
@Transactional
public class CallerInterfaceServiceImpl implements CallerInterfaceService {

    @Autowired
    private CallerInterfaceMapper callerInterfaceMapper;

    @Override
    public CallerInterface findOneByCallerAndInterface(String callerCode, Integer interfaceId) {
        return callerInterfaceMapper.findOneByCallerAndInterface(callerCode, interfaceId);
    }
}

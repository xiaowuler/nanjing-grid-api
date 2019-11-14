package com.pingchuan.api.service.impl;

import com.pingchuan.api.mapper.CallerMapper;
import com.pingchuan.api.model.Caller;
import com.pingchuan.api.service.CallerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description: 调用者 实现类服务层
 * @author: XW
 * @create: 2019-11-11 13:35
 **/

@Service
@Transactional
public class CallerServiceImpl implements CallerService {

    @Autowired
    private CallerMapper callerMapper;

    @Override
    public Caller findOneByUsernameAndPassword(String username, String password) {
        return callerMapper.findOneByUsernameAndPassword(username, password);
    }

}

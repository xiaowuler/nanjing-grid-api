package com.pingchuan.api.service.impl;

import com.pingchuan.api.mapper.TokenMapper;
import com.pingchuan.api.model.Token;
import com.pingchuan.api.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description: 立牌 服务层
 * @author: XW
 * @create: 2019-11-11 14:50
 **/

@Service
@Transactional
public class TokenServiceImpl implements TokenService {

    @Autowired
    private TokenMapper tokenMapper;

    @Override
    public void InsertOne(Token token) {
        tokenMapper.insertOne(token);
    }

    @Override
    public Token findOneByCallerCode(String code) {
        return tokenMapper.findOneByCallerCode(code);
    }


    @Override
    public void deleteOneByCallerCode(String code) {
        tokenMapper.deleteOneByCallerCode(code);
    }
}

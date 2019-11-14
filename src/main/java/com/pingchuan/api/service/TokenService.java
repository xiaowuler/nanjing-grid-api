package com.pingchuan.api.service;

import com.pingchuan.api.model.Token;

public interface TokenService {
    void InsertOne(Token token);

    Token findOneByCallerCode(String code);

    void deleteOneByCallerCode(String code);
}

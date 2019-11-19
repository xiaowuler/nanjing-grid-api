package com.pingchuan.api.service;

import com.pingchuan.api.model.Interface;

public interface InterfaceService {
    Interface findOneById(int id);

    void updateOne(Integer interfaceId, byte b);
}

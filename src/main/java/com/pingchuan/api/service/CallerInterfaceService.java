package com.pingchuan.api.service;

import com.pingchuan.api.model.CallerInterface;

public interface CallerInterfaceService {
    CallerInterface findOneByCallerAndInterface(String callerCode, Integer interfaceId);
}

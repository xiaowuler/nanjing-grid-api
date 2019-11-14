package com.pingchuan.api.service;

import com.pingchuan.api.model.Caller;

public interface CallerService {
    Caller findOneByUsernameAndPassword(String username, String password);

}

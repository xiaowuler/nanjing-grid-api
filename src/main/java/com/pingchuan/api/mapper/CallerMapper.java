package com.pingchuan.api.mapper;

import com.pingchuan.api.model.Caller;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CallerMapper {

    Caller findOneByUsernameAndPassword(@Param("username") String username, @Param("password") String password);


}

package com.pingchuan.api.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Action {
    boolean isNeedElementCode();
    int apiId();
}

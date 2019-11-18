package com.pingchuan.api.annotation;

import java.lang.annotation.*;

/**
 * @author xiaowuler
 */
@Target(ElementType.METHOD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface OtherAction {
    int apiId();
    boolean isNeedTime();
}

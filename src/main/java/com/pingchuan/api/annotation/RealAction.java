package com.pingchuan.api.annotation;

import java.lang.annotation.*;

/**
 * @author xiaowuelr
 */

@Target(ElementType.METHOD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface RealAction {
    int apiId();
    boolean isNeedElementCode();
}

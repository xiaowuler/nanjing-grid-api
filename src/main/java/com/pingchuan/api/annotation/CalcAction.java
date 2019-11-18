package com.pingchuan.api.annotation;

import java.lang.annotation.*;

/**
 * @author xiaowuler
 */
@Target(ElementType.METHOD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface CalcAction{
    String calcType();
    int apiId();
    boolean isArea();
}

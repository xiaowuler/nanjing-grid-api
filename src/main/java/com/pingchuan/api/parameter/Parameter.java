package com.pingchuan.api.parameter;

import java.util.List;

public interface Parameter {

    List<String> checkCode(boolean isNeedElementCode);

    boolean verifyToken();

    String getCallerCode();

    String getAreaCode();

    default void setCalcType(String calcType){};
}

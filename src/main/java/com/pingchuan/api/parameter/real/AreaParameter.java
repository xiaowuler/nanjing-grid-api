package com.pingchuan.api.parameter.real;

import com.pingchuan.api.parameter.Parameter;
import lombok.Data;

import java.util.List;

@Data
public class AreaParameter extends BaseParameter implements Parameter {

    private String areaCode;

    @Override
    public List<String> checkCode(boolean isNeed) {
        super.checkCode(isNeed);

        areaCode = check.checkString(areaCode, "areaCode");

        return check.errors;
    }
}

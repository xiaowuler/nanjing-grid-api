package com.pingchuan.api.parameter.other;

import com.pingchuan.api.parameter.CheckParameter;
import com.pingchuan.api.parameter.Parameter;
import lombok.Data;

import java.util.List;

@Data
public class TrapezoidParameter extends BaseParameter implements Parameter {

    private String areaCode;

    @Override
    public String getAreaCode() {
        return areaCode;
    }

    @Override
    public List<String> checkCode(boolean isNeedElementCode) {
        check = new CheckParameter();

        if (isNeedElementCode){
            areaCode = check.checkString(areaCode, "areaCode");
        }

        return check.errors;
    }
}

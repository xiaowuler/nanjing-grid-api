package com.pingchuan.api.parameter.other;

import com.alibaba.fastjson.annotation.JSONField;
import com.pingchuan.api.parameter.Parameter;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class NewestParameter extends BaseParameter implements Parameter {

    private String date;

    @JSONField(serialize = false)
    private Date startTime;

    @JSONField(serialize = false)
    private Date endTime;

    @Override
    public List<String> checkCode(boolean isNeedTime) {
        super.checkCode(isNeedTime);

        if (isNeedTime){
            startTime = check.checkDate(date, "date");
        }

        return check.errors;
    }

    @Override
    public String getAreaCode() {
        return null;
    }
}

package com.pingchuan.api.parameter;

import com.alibaba.fastjson.annotation.JSONField;
import com.pingchuan.api.util.SignUtil;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Data
public class BaseParameter{

    protected String token;

    protected String updateTime;

    protected String startTime;

    protected String forecastModel;

    @JSONField(serialize = false)
    protected Date updateDate;

    @JSONField(serialize = false)
    protected Date startDate;

    protected String elementCode;

    @JSONField(serialize = false)
    public String callerCode;

    @JSONField(serialize = false)
    protected boolean isNeedElementCode;

    @JSONField(serialize = false)
    protected CheckParameter check;

    public List<String> checkCode(boolean isNeedElementCode){

        this.isNeedElementCode = isNeedElementCode;

        check = new CheckParameter();
        startDate = check.checkTime(startTime, "startTime");
        updateDate = check.checkTime(updateTime, "updateTime");
        forecastModel = check.checkString(forecastModel, "forecastModel");
        if (isNeedElementCode) {
            elementCode = check.checkString(elementCode, "elementCode");
        }

        return null;
    }

    public boolean verifyToken(){

        if (StringUtils.isEmpty(token)) {
            return false;
        }

        callerCode = SignUtil.getClaim(token, "userCode");
        return SignUtil.verify(token);
    }
}

package com.pingchuan.api.parameter.real;

import com.alibaba.fastjson.annotation.JSONField;
import com.pingchuan.api.parameter.CheckParameter;
import com.pingchuan.api.util.SignUtil;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Data
public class BaseParameter {

    private String startRealTime;

    @JSONField(serialize = false)
    private Date startRealDate;

    private String endRealTime;

    @JSONField(serialize = false)
    private Date endRealDate;

    protected String elementCode;

    protected String token;

    @JSONField(serialize = false)
    public String callerCode;

    @JSONField(serialize = false)
    protected CheckParameter check;

    public List<String> checkCode(boolean isNeed){

        check = new CheckParameter();
        startRealDate = check.checkTime(startRealTime, "startRealTime");
        endRealDate = check.checkTime(endRealTime, "endRealTime");
        if (isNeed){
            elementCode = check.checkString(elementCode,"elementCode");
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

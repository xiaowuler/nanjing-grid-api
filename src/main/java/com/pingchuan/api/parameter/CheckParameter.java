package com.pingchuan.api.parameter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.pingchuan.api.contants.TimeFormat;
import com.pingchuan.api.util.TimeFormatUtil;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @description: 校验参数
 * @author: XW
 * @create: 2019-11-11 10:09
 **/

@Data
public class CheckParameter {

    public List<String> errors = new ArrayList<>();

    public Date checkTime(String timeStr, String parameterName){
        if (StringUtils.isEmpty(timeStr))
        {
            errors.add(String.format("%s不能为空", parameterName));
            return null;
        }

        Date time = convertTimeStr(timeStr);
        if (StringUtils.isEmpty(time))
            errors.add(String.format("%s 格式不正确，日期格式：%s", parameterName, TimeFormat.PARAMETER_TIME));
        else
            return time;

        return null;
    }

    public Date checkDate(String timeStr, String parameterName){
        if (StringUtils.isEmpty(timeStr))
        {
            errors.add(String.format("%s不能为空", parameterName));
            return null;
        }

        Date time = convertDateStr(timeStr);
        if (StringUtils.isEmpty(time))
            errors.add(String.format("%s 格式不正确，日期格式：%s", parameterName, TimeFormat.ELEMENT_VALUES_NAME));
        else
            return time;

        return null;
    }

    public Integer checkTimeEffect(String timeEffect){
        if (StringUtils.isEmpty(timeEffect))
        {
            errors.add("timeEffect不能为空");
            return null;
        }

        return Integer.parseInt(timeEffect);
    }

    public String checkString(String parameter, String parameterName){
        if (StringUtils.isEmpty(parameter))
        {
            errors.add(String.format("%s不能为空", parameterName));
            return null;
        }

        return parameter.toUpperCase();
    }

    public List<double[]> checkLocation(String location){

        if (StringUtils.isEmpty(location))
        {
            errors.add("location不能为空");
            return null;
        }

        try{
            return JSON.parseObject(location, new TypeReference<ArrayList<double[]>>(){});
        }catch (Exception e){
            errors.add("location 格式不正确，格式应为'[[lon,lat],...,[lon,lat]]'");
        }

        return null;
    }

    public double[] checkSingleLocation(String location){

        if (StringUtils.isEmpty(location))
        {
            errors.add("location不能为空");
            return null;
        }

        try{
            return JSON.parseObject(location, new TypeReference<double[]>(){});
        }catch (Exception e){
            errors.add("location 格式不正确，格式应为'[lon,lat]'");
        }

        return null;
    }

    public List<double[]> checkThreshold(String threshold){

        if (StringUtils.isEmpty(threshold))
        {
            errors.add("threshold不能为空");
            return null;
        }

        try{
            return JSON.parseObject(threshold, new TypeReference<ArrayList<double[]>>(){});
        }catch (Exception e){
            errors.add("threshold 格式不正确，格式应为'[[startValue, endValue],...,[startValue, endValue]]'");
        }

        return null;
    }

    private Date convertTimeStr(String timeStr){
        try {
            return TimeFormatUtil.CovertStringToDate(TimeFormat.PARAMETER_TIME, timeStr);
        } catch (ParseException e) {

        }
        return null;
    }

    private Date convertDateStr(String timeStr){
        try {
            return TimeFormatUtil.CovertStringToDate(TimeFormat.ELEMENT_VALUES_NAME, timeStr);
        } catch (ParseException e) {

        }
        return null;
    }

    public Date ConvertForecastTime(Date startTime, int timeEffect){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        calendar.add(Calendar.HOUR_OF_DAY, timeEffect);
        return calendar.getTime();
    }

}

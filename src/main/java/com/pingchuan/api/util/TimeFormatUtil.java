package com.pingchuan.api.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @description: 格式化时间工具类
 * @author: XW
 * @create: 2019-11-04 16:07
 **/
public class TimeFormatUtil {

    public static String CovertDateToString(String timeFormat, Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeFormat);
        return simpleDateFormat.format(date);
    }

    public static Date CovertStringToDate(String timeFormat, String time) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeFormat);
        return simpleDateFormat.parse(time);
    }

}

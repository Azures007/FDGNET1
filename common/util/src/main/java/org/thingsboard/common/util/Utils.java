package org.thingsboard.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {

    private static SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
     *
     * @param nowTime 当前时间
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取两个日期的时间差，单位为天
     * @param endTime
     * @param startTime
     * @return
     */
    public static int getDifferDay(Date endTime, Date startTime){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String newDate = df.format(endTime);
        String sqlDate1 = df.format(startTime);
        try {
            endTime = df.parse(newDate);
            startTime = df.parse(sqlDate1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Long time = endTime.getTime();
        Long time2 = startTime.getTime();
        int day = (int) ((time - time2) / (24*3600*1000));
        return day;
    }

    /**
     * 获取两个日期的时间差，单位为小时
     * @param endTime
     * @param startTime
     * @return
     */
    public static int getDifferHour(Date endTime, Date startTime){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String newDate = df.format(endTime);
        String sqlDate1 = df.format(startTime);
        try {
            endTime = df.parse(newDate);
            startTime = df.parse(sqlDate1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Long time = endTime.getTime();
        Long time2 = startTime.getTime();
        int hour = (int) ((time - time2) / (3600*1000));
        return hour;
    }

    /**
     * 日期格式化文本，格式：yyyy-MM-dd
     *
     * @param date 日期
     * @return
     */
    public static String formatDateToString(Date date){
        if (null == date) return null;
        return df1.format(date);
    }

    /**
     * 日期格式化文本，格式：yyyy-MM-dd HH:mm:ss
     *
     * @param date 日期
     * @return
     */
    public static String formatDateTimeToString(Date date){
        if (null == date) return null;
        return df2.format(date);
    }

    /**
     * 文本转换成日期格式，格式：yyyy-MM-dd
     *
     * @param dateStr 日期（文本）
     * @return
     */
    public static Date parseStringToDate(String dateStr){
        try {
            return df1.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文本转换成日期格式，格式：yyyy-MM-dd HH:mm:ss
     *
     * @param dateStr 日期（文本）
     * @return
     */
    public static Date parseStringToDateTime(String dateStr){
        try {
            return df2.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }



}

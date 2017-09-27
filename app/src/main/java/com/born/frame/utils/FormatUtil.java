package com.born.frame.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by min on 2015/3/7.
 */
public class FormatUtil {

    /**
     * 转换文件大小
     *
     * @return B/KB/MB/GB
     */
    public static String formatFileSize(long fileS) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 判断字符串是否含有数字
     *
     * @param content
     * @return
     */
    public static boolean hasDigit(String content) {
        if (TextUtils.isEmpty(content)) {
            return false;
        }
        boolean result = false;
        Pattern p = Pattern.compile(".*\\d+.*");
        Matcher m = p.matcher(content);
        if (m.matches())
            result = true;
        return result;
    }

    /**
     * 判断email格式是否正确
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))" +
                "([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 判断phone格式是否正确
     *
     * @param mobile
     * @return
     */
    public static boolean isMobileNo(String mobile) {
        Pattern p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号
        Matcher m = p.matcher(mobile);
        return m.matches();
    }

    /**
     * 判断字符串中是否包含中文
     *
     * @param name
     * @return
     */
    public static boolean isChinese(String name) {
        String str = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D]+$";
        boolean isIncludeChinese = false;
        for (int i = 0; i < name.length(); i++) {
            if (Pattern.matches(str, name.substring(i, i + 1))) {
                isIncludeChinese = true;
                break;
            }
        }
        return isIncludeChinese;
    }

    /**
     * 判断字符串是否全为数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        if (null == str) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]+");
        return pattern.matcher(str).matches();
    }

    /**
     * 以date时间转换成format格式输出
     *
     * @param timeInMillis 0 表示返回当前时间
     * @param format       yyyyMMddkkmm、yyyy-MM-dd"、MM月dd日、yyyyMMddHHmmss、yyyyMMddHHmm
     * @return
     */
    public static String getFormatTime(String format, long timeInMillis) {
        if (0 == timeInMillis) {
            return DateFormat.format(format, System.currentTimeMillis()).toString();
        }
        return DateFormat.format(format, timeInMillis).toString();
    }

    /**
     * 时间格式转换
     *
     * @param timeIn
     * @param formatIn
     * @param formatOut
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String transferTime(String timeIn, String formatIn, String formatOut) {
        String timeOut = "";

        if (TextUtils.isEmpty(formatIn) || TextUtils.isEmpty(formatOut)) {
            return timeOut;
        }

        try {
            Date dateIn = new SimpleDateFormat(formatIn).parse(timeIn);
            timeOut = new SimpleDateFormat(formatOut).format(dateIn);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeOut;
    }

    /**
     * 将日期格式的字符串转换为长整型
     *
     * @param date
     * @param format
     * @return
     */
    public static long timeStr2long(String date, String format) {
        try {
            SimpleDateFormat sf = new SimpleDateFormat(format);
            return sf.parse(date).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0l;
    }

}

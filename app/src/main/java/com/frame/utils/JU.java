package com.frame.utils;

import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;

import com.google.gson.internal.LinkedTreeMap;

import java.util.AbstractMap;
import java.util.ArrayList;

/**
 * JsonUtil：解析返回的对象
 */

@SuppressWarnings("unchecked")
public class JU {

    /**
     * 获取指定key的value
     *
     * @param object AbstractMap
     * @param key    查找的key值
     * @return 返回字符串
     */
    public static String s(Object object, String key) {
        if (object == null || TextUtils.isEmpty(key)) return "";
        Object value = null;
        if (object instanceof AbstractMap) {
            value = ((AbstractMap) object).get(key);
        }
        return value == null ? "" : String.valueOf(value);
    }

    public static Spanned sh(Object object, String key) {
        return Html.fromHtml(s(object, key));
    }

    /**
     * 获取指定key的value
     *
     * @return double
     */
    public static double d(Object object, String key) {
        if (object == null || TextUtils.isEmpty(key)) return 0d;
        double value = 0d;
        if (object instanceof AbstractMap) {
            try {
                String dStr = s(object, key);
                if (TextUtils.isEmpty(dStr)) return 0d;
                value = Double.parseDouble(dStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    /**
     * 获取指定key的value
     *
     * @return long
     */
    public static long l(Object object, String key) {
        return (long) d(object, key);
    }

    /**
     * 获取指定key的value
     *
     * @return int
     */
    public static int i(Object object, String key) {
        return (int) d(object, key);
    }

    public static String d2s(Object object, String key) { //先转double，再转String
        return String.valueOf(d(object, key));
    }

    public static String l2s(Object object, String key) { //先转long，再转String
        return String.valueOf(l(object, key));
    }

    public static String i2s(Object object, String key) { //先转int，再转String
        return String.valueOf(i(object, key));
    }

    /**
     * 获取指定key的value
     *
     * @return boolean
     */
    public static boolean b(Object object, String key) {
        return "true".equals(s(object, key));
    }

    /**
     * 获取指定key的value
     *
     * @return T
     */
    public static <T> T m(Object object, String key) {
        if (object == null || TextUtils.isEmpty(key)) return (T) new LinkedTreeMap<>();
        Object objResult = null;
        if (object instanceof AbstractMap) {
            objResult = ((AbstractMap) object).get(key);
        }
        return objResult == null ? (T) new LinkedTreeMap<>() : (T) objResult;
    }

    /**
     * 获取指定key的value
     *
     * @return T
     */
    public static <T> T al(Object object, String key) {
        if (object == null || TextUtils.isEmpty(key)) return (T) new ArrayList<>();
        Object objResult = null;
        if (object instanceof AbstractMap) {
            objResult = ((AbstractMap) object).get(key);
        }
        return objResult == null ? (T) new ArrayList<>() : (T) objResult;
    }

    /**
     * object转换ArrayList
     *
     * @return T
     */
    public static <T> T al(Object object) {
        return object instanceof ArrayList ? (T) object : (T) new ArrayList<>();
    }

    /**
     * 通用转换器
     * 获取指定key的value，缺省返回Object类型
     *
     * @return T
     */
    public static <T> T obj(Object object, String key) {
        if (object == null || TextUtils.isEmpty(key)) return (T) new Object();
        Object objResult = null;
        if (object instanceof AbstractMap) {
            objResult = ((AbstractMap) object).get(key);
        }
        return objResult == null ? (T) new Object() : (T) objResult;
    }

}

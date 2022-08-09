package com.frame.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.frame.application.App;

public class SP {

    private static final String SP_NAME = "sp_info";

    public static final String USER_NAME = "user_name";  // 登录成功的用户名


    /**
     * ========================================================================================================
     * 保存数据
     *
     * @param key  key
     * @param data 要存储的数据
     * @return 是否成功
     */
    public static boolean saveData(String key, Object data) {
        SharedPreferences sp = App.getInstance().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);

        if (data instanceof Integer) return sp.edit().putInt(key, (Integer) data).commit();
        else if (data instanceof Long) return sp.edit().putLong(key, (Long) data).commit();
        else if (data instanceof Float) return sp.edit().putFloat(key, (Float) data).commit();
        else if (data instanceof Boolean) return sp.edit().putBoolean(key, (Boolean) data).commit();
        else if (data instanceof String) return sp.edit().putString(key, (String) data).commit();
        return false;
    }

    /**
     * 获取数据
     *
     * @param key          key
     * @param defaultValue defaultValue
     * @return 返回存储的类型
     */
    public static <T> T getData(String key, T defaultValue) {
        SharedPreferences sp = App.getInstance().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);

        if (defaultValue instanceof Integer) return cast(sp.getInt(key, (Integer) defaultValue));
        else if (defaultValue instanceof Long) return cast(sp.getLong(key, (Long) defaultValue));
        else if (defaultValue instanceof Float) return cast(sp.getFloat(key, (Float) defaultValue));
        else if (defaultValue instanceof Boolean) return cast(sp.getBoolean(key, (Boolean) defaultValue));
        else if (defaultValue instanceof String) return cast(sp.getString(key, (String) defaultValue));
        return defaultValue;
    }

    private static <T> T cast(Object object) {
        return (T) object;
    }
}

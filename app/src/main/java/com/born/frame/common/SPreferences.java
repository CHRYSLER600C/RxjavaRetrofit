package com.born.frame.common;

import android.content.Context;
import android.content.SharedPreferences;

public class SPreferences {

	private static final String SP_NAME = "sp_info";

	// 登录成功后用户的name
	public static final String USER_NAME = "user_name";

	// ========================================================================================================
	/**
	 * 保存数据
	 * 
	 * @param context
	 * @param data
	 * @param key
	 * @return
	 */
	public static boolean saveData(Context context, Object data, String key) {
		return saveData(context, data, key, SP_NAME);
	}

	/**
	 * 保存数据
	 * 
	 * @param context
	 * @param data
	 * @param key
	 * @param spName
	 * @return
	 */
	public static boolean saveData(Context context, Object data, String key, String spName) {
		boolean bResult = false;
		SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
		if (context != null) {
			if (data instanceof Integer) {
				bResult = sp.edit().putInt(key, (Integer) data).commit();
			} else if (data instanceof Long) {
				bResult = sp.edit().putLong(key, (Long) data).commit();
			} else if (data instanceof Float) {
				bResult = sp.edit().putFloat(key, (Float) data).commit();
			} else if (data instanceof Boolean) {
				bResult = sp.edit().putBoolean(key, (Boolean) data).commit();
			} else if (data instanceof String) {
				bResult = sp.edit().putString(key, (String) data).commit();
			}
		}
		return bResult;
	}

	/**
	 * 获取数据
	 * 
	 * @param context
	 * @param defaultValue
	 * @param key
	 * @return 数据类型需要强制转换
	 */
	public static Object getData(Context context, Object defaultValue, String key) {
		return getData(context, defaultValue, key, SP_NAME);
	}

	/**
	 * 获取数据
	 * 
	 * @param context
	 * @param defaultValue
	 * @param key
	 * @param spName
	 * @return 数据类型需要强制转换
	 */
	public static Object getData(Context context, Object defaultValue, String key, String spName) {
		Object bResult = defaultValue;
		SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
		if (context != null) {
			if (defaultValue instanceof Integer) {
				bResult = sp.getInt(key, (Integer) defaultValue);
			} else if (defaultValue instanceof Long) {
				bResult = sp.getLong(key, (Long) defaultValue);
			} else if (defaultValue instanceof Float) {
				bResult = sp.getFloat(key, (Float) defaultValue);
			} else if (defaultValue instanceof Boolean) {
				bResult = sp.getBoolean(key, (Boolean) defaultValue);
			} else if (defaultValue instanceof String) {
				bResult = sp.getString(key, (String) defaultValue);
			}
		}
		return bResult;
	}

}

package com.born.frame.utils;

import android.util.Log;

import com.born.frame.common.CommonData;


public final class Logger {
	/**
	 * 日志打印
	 */
	// public static final boolean DEGUG = true;

	public static void i(String msg) {
		if (CommonData.DEBUG) {
			System.out.println(msg);
		}
	}

	public static void i(String tag, String msg) {
		if (CommonData.DEBUG) {
			Log.i(tag, msg);
		}
	}

	public static void v(String tag, String msg) {
		if (CommonData.DEBUG) {
			Log.v(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (CommonData.DEBUG) {
			Log.d(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (CommonData.DEBUG) {
			Log.w(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (CommonData.DEBUG) {
			Log.e(tag, msg);
		}
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (CommonData.DEBUG) {
			Log.e(tag, msg, tr);
		}
	}

	public static void m(String tag, String msg) { // 带方法名
		Log.v(tag, new Exception().getStackTrace()[1].getMethodName() + ": " + msg);
	}
}

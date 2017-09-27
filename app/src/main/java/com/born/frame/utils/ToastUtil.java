package com.born.frame.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 随时注入即可使用
 */
public class ToastUtil {

    public static Context mContext;

    public ToastUtil(Context context) {
        mContext = context.getApplicationContext();
    }

    public static void showToastShort(int resId) {
        Toast.makeText(mContext, mContext.getString(resId), Toast.LENGTH_SHORT).show();
    }

    public static void showToastShort(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToastLong(int resId) {
        Toast.makeText(mContext, mContext.getString(resId), Toast.LENGTH_LONG).show();
    }

    public static void showToastLong(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }
}

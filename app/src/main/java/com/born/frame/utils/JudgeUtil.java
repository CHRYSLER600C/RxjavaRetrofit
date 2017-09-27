package com.born.frame.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by min on 2016/12/12.
 * <p>
 * 各种判断的工具类
 */

public class JudgeUtil {

    /**
     * 判断是否为空
     *
     * @param obj
     * @return
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }

        if (obj instanceof String || obj instanceof CharSequence) {
            return TextUtils.isEmpty(obj.toString());
        } else if (obj instanceof List) {
            return ((List) obj).size() == 0;
        } else if (obj instanceof Map) {
            return ((Map) obj).size() == 0;
        }
        return false;
    }

    /**
     * 判断是否不为空
     *
     * @param obj
     * @return
     */
    public static boolean isNotEmpty(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof String || obj instanceof CharSequence) {
            String str = obj.toString();
            return !TextUtils.isEmpty(str) && !"null".equals(str);
        } else if (obj instanceof List) {
            return ((List) obj).size() > 0;
        } else if (obj instanceof Map) {
            return ((Map) obj).size() > 0;
        }
        return true;
    }

    public static String checkNull(String src) {
        return TextUtils.isEmpty(src) ? "" : src;
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo =
                    mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

}

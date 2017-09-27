package com.born.frame.httputils;

import android.app.Fragment;

import com.born.frame.activity.BaseActivity;
import com.born.frame.common.CommonData;
import com.born.frame.dataclass.DataClass;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.Reader;
import java.net.FileNameMap;
import java.net.URLConnection;

/**
 * OkHttpUtil2 的工具类
 * Created by dongxie on 2016/11/23.
 */

public class HttpUtil2 {

    /**
     * 将类转换为json格式字符串
     *
     * @param obj 需要转换的类
     * @return
     */
    public static String createJsonString(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    /**
     * 解析json字符串为相应类
     *
     * @param t       字符串转换的类类型
     * @param content json字符串
     * @return
     */
    public static <T> T parsingJson(Class<T> t, String content) {
        Gson gson = new Gson();
        T result = null;
        try {
            result = (T) gson.fromJson(content, t);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 解析json字符串为相应类
     *
     * @param t      字符串转换的类类型
     * @param reader json字符串
     * @return
     */
    public static <T> T parsingJson(Class<T> t, Reader reader) {
        Gson gson = new Gson();
        T result = (T) gson.fromJson(reader, t);
        return result;
    }

    /**
     * 解析json字符串为自定义类, 不需要 @Expose
     *
     * @param t       字符串转换的类类型
     * @param content json字符串
     * @return
     */
    public static <T> T parsingCustomJson(Class<T> t, String content) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        T result = null;
        try {
            result = (T) gson.fromJson(content, t);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 通过文件名猜测MimeType
     *
     * @param path
     * @return
     */
    public static String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        System.out.println("contentTypeFor is " + contentTypeFor);
        return contentTypeFor;
    }

    /**
     * 跳转登录界面，当是activity时，就用activity的跳转来跳转，那样的话返回的时候就会调用activity中的onActivityResult方法。
     *
     * @param context     只能为BaseActivity / Fragment
     * @param isSuccess
     * @param responseObj
     * @return
     */
    public static <T> boolean handleResponse(Object context, boolean isSuccess, T responseObj) {
        BaseActivity baseActivity = (BaseActivity) context;
        if (context instanceof Fragment) {
            baseActivity = (BaseActivity) ((Fragment) context).getActivity();
        }

        boolean needContinue = false;
        if (isSuccess) {
            DataClass response = (DataClass) responseObj;
            if (CommonData.RESULT_UNLOGIN.equals(response.code)) {
//                loginIntent(activity);
            } else if (CommonData.RESULT_FAILED.equals(response.code)) {
                baseActivity.showToast(response.message);
            } else {
                needContinue = true;
            }
        } else {
            baseActivity.showToast(CommonData.NETWORK_ERROR_MSG);
        }
        return needContinue;
    }

}

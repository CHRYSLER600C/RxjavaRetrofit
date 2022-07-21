package com.frame.httputils;

import android.os.Handler;

import com.blankj.utilcode.util.ToastUtils;
import com.frame.common.CommonData;
import com.frame.dataclass.DataClass;
import com.frame.utils.JU;
import com.frame.utils.LU;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.Reader;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * OkHttpUtil2 的工具类
 * Created by dongxie on 2016/11/23.
 */

public class HttpUtil2 {

    private static final String JSON_TYPE = "application/json; charset=utf-8";
    private static final String TEXT_TYPE = "text/x-markdown; charset=utf-8";
    private static final String IMG_TYPE = "image/png";
    private static final MediaType JSON = MediaType.parse(JSON_TYPE);
    private static final MediaType TEXT = MediaType.parse(TEXT_TYPE);
    private static final MediaType IMG = MediaType.parse(IMG_TYPE);

    /**
     * ================================================== Gson =================================================
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
     * ================================================== Request =================================================
     * 创建get方法的地址请求
     *
     * @param url
     * @param tag
     * @return
     */
    public static Request createUrlGetRequest(String url, Object tag) {
        Request.Builder builder = new Request.Builder();
        if (tag != null) {
            builder.tag(tag);
        }
        return builder.url(url).build();
    }

    /**
     * 创建post方法的json请求
     *
     * @param url
     * @param json
     * @param tag
     * @return
     */
    public static Request createGSONPostRequest(String url, String json, Object tag) {
        RequestBody body = RequestBody.create(JSON, json);
        Request.Builder builder = new Request.Builder();
        if (tag != null) {
            builder.tag(tag);
        }
        return builder.url(url).post(body).build();
    }

    /**
     * 创建上传多文件请求
     *
     * @param url
     * @param params   需要上传的文件
     * @param callback
     * @param tag
     * @return
     */
    public static Request createFilePostRequest(Handler handler, String url, Map<String, Object> params,
                                                OkHttpUtil2.IRequestFileCallback callback, Object tag) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);//设置类型
        for (String key : params.keySet()) {//追加参数
            Object object = params.get(key);
            if (object instanceof File) {
                File file = (File) object;
                builder.addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse(HttpUtil2.guessMimeType(file.getName())), file));
            } else {
                builder.addFormDataPart(key, object.toString());
            }
        }

        CountingRequestBody countingRequestBody = new CountingRequestBody(builder.build(),
                (final long bytesWritten, final long contentLength) ->
                        handler.post(() -> callback.ProgressResponse(bytesWritten, contentLength)));

        Request.Builder requestBuilder = new Request.Builder();
        if (tag != null) {
            requestBuilder.tag(tag);
        }
        return requestBuilder.url(url).post(countingRequestBody).build();
    }

    /**
     * 创建post方法请求，用以传递参数
     *
     * @param url
     * @param params 参数
     * @param tag
     * @return
     */
    public static Request createParamsPostRequest(String url, Map<String, String> params, Object tag) {
        RequestBody formBody = null;
        if (params != null) {
            FormBody.Builder builder = new FormBody.Builder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
            formBody = builder.build();
        }

        Request.Builder builder = new Request.Builder();
        if (tag != null) {
            builder.tag(tag);
        }
        if (formBody != null) {
            builder.post(formBody);
        }
        return builder.url(url).build();
    }

    public static void cancelCall(Object tag, List<Call> list) {
        for (Call call : list) {
            if (tag.equals(call.request().tag())) {
                try {
                    call.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 跳转登录界面，Fragment的时候就会调用Fragment中的onActivityResult方法
     *
     * @param context 只能为BaseActivity / Fragment
     */
    public static <T> boolean handleResponse(Object context, boolean isSuccess, T responseObj) {
        if (!isSuccess) {
            ToastUtils.showShort(CommonData.NETWORK_ERROR_MSG);
            return false;
        }

        DataClass dc = (DataClass) responseObj;
        int code = JU.i(dc.obj, CommonData.CODE);
        String message = JU.s(dc.obj, CommonData.MESSAGE);

        if (CommonData.RESULT_UNLOGIN == code) {
            LU.INSTANCE.gotoLogin(context);
            throw new ApiException(message);
        } else if (CommonData.RESULT_SUCCESS != code) {
            throw new ApiException(message);
        }
        return true;
    }

}

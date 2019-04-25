package com.frame.httputils;

import android.text.TextUtils;

import com.frame.utils.Logger;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;


/**
 * Created by zhy on 16/3/1.
 */
public class LoggerInterceptor implements Interceptor {

    public static final String TAG = "okhttp";
    private String tag;
    private boolean showResponse;

    public LoggerInterceptor(String tag, boolean showResponse) {
        this.tag = TextUtils.isEmpty(tag) ? TAG : tag;
        this.showResponse = showResponse;
    }

    public LoggerInterceptor(String tag) {
        this(tag, false);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        logForRequest(request);
        Response response = chain.proceed(request);
        return logForResponse(response);
    }

    /**
     * 请求的日志
     *
     * @param request
     */
    private void logForRequest(Request request) {
        try {
            String url = request.url().toString();
            Headers headers = request.headers();

            Logger.v(tag, "================ Request Log Begin ================");
            Logger.d(tag, "Method : " + request.method());
            Logger.d(tag, "Url : " + url);
            if (headers != null && headers.size() > 0) {
                Logger.d(tag, "Headers : " + headers.toString());
            }
            RequestBody requestBody = request.body();
            if (requestBody != null) {
                MediaType mediaType = requestBody.contentType();
                if (mediaType != null) {
                    Logger.d(tag, "RequestBody ContentType : " + mediaType.toString());
                    if (isText(mediaType)) {
                        Logger.d(tag, "RequestBody Content : " + bodyToString(request));
                    } else {
                        Logger.d(tag, "RequestBody Content : maybe [file part] , too large too print , ignored!");
                    }
                }
            }
            Logger.v(tag, "================= Request Log End =================\n ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回的日志
     *
     * @param response
     * @return
     */
    private Response logForResponse(Response response) {
        try {
            Logger.i(tag, "================ Response Log Begin ================");
            Response.Builder builder = response.newBuilder();
            Response clone = builder.build();
            String url = clone.request().url().toString();
            Logger.d(tag, "Url : " + url);
            Logger.d(tag, "Code : " + clone.code());
            if (showResponse) {
                ResponseBody body = clone.body();
                if (body != null) {
                    MediaType mediaType = body.contentType();
                    if (mediaType != null) {
                        Logger.d(tag, "ResponseBody ContentType : " + mediaType.toString());
                        if (isText(mediaType)) {
                            String responseBody = body.string();
                            Logger.d(tag, "ResponseBody Content : " + responseBody);
                            Logger.i(tag, "================= Response Log End =================\n ");

                            body = ResponseBody.create(mediaType, responseBody);
                            return response.newBuilder().body(body).build();
                        } else {
                            Logger.d(tag, "ResponseBody Content : maybe [file part] , too large too print , ignored!");
                        }
                    }
                }
            }
            Logger.i(tag, "================= Response Log End =================\n ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private boolean isText(MediaType mediaType) {
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        if (mediaType.subtype() != null) {
            if (mediaType.subtype().equals("json") || mediaType.subtype().equals("xml")
                    || mediaType.subtype().equals("html") || mediaType.subtype().equals("webviewhtml"))
                return true;
        }
        return false;
    }

    private String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "Something error when show requestBody.";
        }
    }
}
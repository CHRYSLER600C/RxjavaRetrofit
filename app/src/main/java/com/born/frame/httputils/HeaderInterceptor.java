package com.born.frame.httputils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by dongxie on 2016/11/21.
 * 通过Interceptor来定义静态请求头
 */

public class HeaderInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request original = chain.request();
        Request request = original.newBuilder()
                .header("User-Agent", "Your-App-Name")
                .header("Accept", "application/vnd.yourapi.v1.full+json")
                .method(original.method(), original.body())
                .build();
        return chain.proceed(request);
    }
}

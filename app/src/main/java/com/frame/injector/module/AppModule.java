package com.frame.injector.module;

import android.app.NotificationManager;
import android.content.Context;
import android.view.LayoutInflater;

import com.frame.common.CommonData;
import com.frame.httputils.HeaderInterceptor;
import com.frame.httputils.LoggerInterceptor;
import com.frame.httputils.MemoryCookieStore;
import com.frame.httputils.MyHostnameVerifier;
import com.frame.httputils.MyTrustManager;
import com.frame.httputils.RequestService;
import com.frame.httputils.converter.MyGsonConverterFactory;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;


@Module
public class AppModule {

    private static final long CONNECT_TIMEOUT = 15;
    private static final long READ_TIMEOUT = 20;
    private static final long WRITE_TIMEOUT = 20;

    private final Context context;

    public AppModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    public Context provideApplicationContext() {
        return context.getApplicationContext();
    }

    /**
     * 实际网络请求类
     */
    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(SSLSocketFactory sslSocketFactory) {
        /** 手动创建一个OkHttpClient并设置参数 */
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        /** 设置请求时间 */
        builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
        /** 设置cookie */
        builder.cookieJar(new MemoryCookieStore());
        /** 设置忽略https安全证书验证 */
        builder.hostnameVerifier(new MyHostnameVerifier());
        builder.sslSocketFactory(sslSocketFactory, new MyTrustManager());
        /** 通过Interceptor实现url参数的添加 */
//        builder.addInterceptor(new UrlParamsInterceptor()); //使用请开启
        /** 通过Interceptor来定义静态请求头 */
        builder.addInterceptor(new HeaderInterceptor()); //使用请开启
        /** 添加日志拦截器 */
        builder.addInterceptor(new LoggerInterceptor(null, CommonData.DEBUG));
        return builder.build();
    }

    /**
     * 网络请求格式解析器
     */
    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .client(okHttpClient)
//                .addConverterFactory(GsonConverterFactory.create()) // Gson解析
                .addConverterFactory(MyGsonConverterFactory.create()) // 自定义Gson解析
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 支持RxJava
                .baseUrl(CommonData.SEVER_URL)
                .build();
    }

    /**
     * 信任https
     */
    @Provides
    @Singleton
    SSLSocketFactory provideSSLSocketFactory(Context context) {
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return sc.getSocketFactory();
    }

    /**
     * 网络请求格式定义函数
     */
    @Provides
    @Singleton
    RequestService provideRequestService(Retrofit retrofit) {
        return retrofit.create(RequestService.class);
    }


    @Provides
    @Singleton
    LayoutInflater provideLayoutInflater(Context context) {
        return (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Provides
    @Singleton
    NotificationManager provideNotificationManager(Context mContext) {
        return (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }



}

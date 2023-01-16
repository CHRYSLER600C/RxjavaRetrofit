package com.frame.httputils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import com.blankj.utilcode.util.ObjectUtils;
import com.frame.injector.component.ComponentHolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.Lazy;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络请求工具
 * <p>
 * 使用Call.cancel()可以立即停止掉一个正在执行的call。如果一个线程正在写请求或者读响应，将会引发IOException。
 * 当call没有必要的时候，使用这个api可以节约网络资源。例如当用户离开一个应用时。不管同步还是异步的call都可以取消。
 * 你可以通过tags来同时取消多个请求。当你构建一请求时，使用RequestBuilder.tag(tag)来分配一个标签。之后你就可以用
 * OkHttpClient.cancel(tag)来取消所有带有这个tag的call。
 */

public class OkHttpUtil2 {

    private Handler mHandler;

    @Inject
    public Lazy<OkHttpClient> mOkHttpClient2;

    //构造方法私有
    private OkHttpUtil2() {
        mHandler = new Handler(Looper.getMainLooper());
        ComponentHolder.getAppComponent().inject(this);
    }

    /**
     * 在访问HttpMethods时创建单例
     */
    private static class SingletonHolder {
        private static final OkHttpUtil2 INSTANCE = new OkHttpUtil2();
    }

    /**
     * 获取单例
     */
    public static OkHttpUtil2 getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 清空cookie
     */
    public void cleanCookie() {
        CookieJar cookieJar = mOkHttpClient2.get().cookieJar();
        if (cookieJar instanceof MemoryCookieStore) {
            MemoryCookieStore cookieStore = (MemoryCookieStore) cookieJar;
            cookieStore.removeAll();
        }
    }

    /**
     * 获取指定url的Cookies
     *
     * @param url 传空时返回所有Cookies
     * @return
     */
    public List<Cookie> getCookies(HttpUrl url) {
        CookieJar cookieJar = mOkHttpClient2.get().cookieJar();
        if (cookieJar instanceof MemoryCookieStore) {
            MemoryCookieStore cookieStore = (MemoryCookieStore) cookieJar;
            return null == url ? cookieStore.getCookies() : cookieStore.get(url);
        }
        return null;
    }

    /**
     * 使用get方法获取请求后返回的json数据
     *
     * @param url
     * @param t        返回的json数据转换类型
     * @param callback
     * @param tag
     */
    public <T> void getGson(String url, final Class<T> t, final IRequestCallback callback, Object tag) {
        if (callback == null) return;

        Call call = mOkHttpClient2.get().newCall(HttpUtil2.createUrlGetRequest(url, tag));
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = ObjectUtils.getOrDefault(response.body().string(), "");
                final T resultObj = HttpUtil2.parsingCustomJson(t, "{\"json\":" + result + "}");
                mHandler.post(() -> callback.ObjResponse(true, resultObj, null));
            }

            @Override
            public void onFailure(Call call, final IOException ioexception) {
                mHandler.post(() -> callback.ObjResponse(false, null, ioexception));
            }
        });
    }

    private <T> void postGsonImpl(Request request, final Class<T> t, final IRequestCallback callback) {
        if (callback == null) return;

        Call call = mOkHttpClient2.get().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() != 200) return;
                String result = ObjectUtils.getOrDefault(response.body().string(), "");
                final T resultObj = HttpUtil2.parsingJson(t, "{\"json\":" + result + "}");
                mHandler.post(() -> callback.ObjResponse(true, resultObj, null));
            }

            @Override
            public void onFailure(Call call, final IOException ioexception) {
                mHandler.post(() -> callback.ObjResponse(false, null, ioexception));
            }
        });
    }

    /**
     * 使用post方法上传json格式数据并获取请求返回的json数据
     *
     * @param obj 上传的数据类型（用以转换成json数据） eg. Map<String, Object>
     */
    public <T> void postGson(String url, Object obj, Class<T> t, IRequestCallback callback, Object tag) {
        Request request = HttpUtil2.createGSONPostRequest(url, HttpUtil2.createJsonString(obj), tag);
        postGsonImpl(request, t, callback);
    }

    /**
     * 使用post方法上传类的map格式数据并获取请求返回的json数据
     */
    public <T> void postGson(String url, Map<String, String> params, Class<T> t, IRequestCallback callback, Object
            tag) {
        Request request = HttpUtil2.createParamsPostRequest(url, params, tag);
        postGsonImpl(request, t, callback);
    }

    /**
     * 上传文件
     *
     * @param url      上传文件地址
     * @param params   上传文件本地地址
     * @param t        上传文件后服务器返回的json数据类
     * @param callback
     * @param tag
     */
    public <T> void uploadFile(String url, Map<String, Object> params, final Class<T> t, final IRequestFileCallback
            callback, Object tag) {
        if (callback == null) return;

        Request request = HttpUtil2.createFilePostRequest(mHandler, url, params, callback, tag);
        Call call = mOkHttpClient2.get().newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException ioexception) {
                mHandler.post(() -> callback.ObjResponse(false, null, ioexception));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseStr = "{\"json\":" + ObjectUtils.getOrDefault(response.body().string(), "") + "}";
                final T resultObj = HttpUtil2.parsingCustomJson(t, responseStr);
                mHandler.post(() -> callback.ObjResponse(true, resultObj, null));
            }
        });
    }

    /**
     * 下载图片请求（含Cookie）
     *
     * @param url      请求图片地址
     * @param callback 请求返回调用函数
     * @param tag      请求标签
     */
    public void downLoadImage(String url, final IRequestCallback callback, Object tag) {
        if (callback == null) return;

        Request request = HttpUtil2.createUrlGetRequest(url, tag);
        Call call = mOkHttpClient2.get().newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException ioexception) {
                mHandler.post(() -> callback.ObjResponse(false, null, ioexception));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                mHandler.post(() -> callback.ObjResponse(true, bitmap, null));
            }
        });
    }

    /**
     * 下载文件
     *
     * @param url      下载文件的地址
     * @param savePath 文件下载后保存目录
     * @param filename 下载后保存的文件名
     * @param callback 下载后调用的函数
     * @param tag      请求标签
     */
    public void downLoadFile(String url, String savePath, String filename, final IRequestFileCallback callback,
                             Object tag) {
        if (callback == null) return;

        Request request = HttpUtil2.createUrlGetRequest(url, tag);
        Call call = mOkHttpClient2.get().newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException ioexception) {
                mHandler.post(() -> callback.ObjResponse(false, "", ioexception));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                File dir = new File(savePath);
                if (!dir.exists()) dir.mkdirs();
                File file = new File(dir, filename);

                final long total = response.body().contentLength();
                InputStream inputStream = response.body().byteStream();
                FileOutputStream outputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len;
                long sum = 0;
                while ((len = inputStream.read(buffer)) != -1) {
                    sum += len;
                    final long finalSum = sum;
                    outputStream.write(buffer, 0, len);
                    mHandler.post(() -> callback.ProgressResponse(finalSum, total));
                }
                outputStream.flush();
                try {
                    inputStream.close();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mHandler.post(() -> callback.ObjResponse(true, file.getAbsolutePath(), null));
            }
        });
    }

    /**
     * 取消请求
     *
     * @param tag 根据此标签取消请求
     */
    public void cancelRequest(Object tag) {
        HttpUtil2.cancelCall(tag, mOkHttpClient2.get().dispatcher().queuedCalls());
        HttpUtil2.cancelCall(tag, mOkHttpClient2.get().dispatcher().runningCalls());
    }

    /**
     * Request Callback Interface
     */
    public interface IRequestCallback {
        /**
         * 通用数据返回
         *
         * @param <T> 根据具体类型自行转换
         */
        <T> void ObjResponse(Boolean isSuccess, T responseObj, IOException e);
    }

    public interface IRequestFileCallback extends IRequestCallback {
        void ProgressResponse(long progress, long total);
    }

}


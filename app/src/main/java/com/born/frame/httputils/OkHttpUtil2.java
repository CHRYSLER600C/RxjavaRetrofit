package com.born.frame.httputils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import com.born.frame.injector.component.ComponentHolder;
import com.born.frame.utils.JudgeUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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

    private static final String JSON_TYPE = "application/json; charset=utf-8";
    private static final String TEXT_TYPE = "text/x-markdown; charset=utf-8";
    private static final String IMG_TYPE = "image/png";
    private static final MediaType JSON = MediaType.parse(JSON_TYPE);
    private static final MediaType TEXT = MediaType.parse(TEXT_TYPE);
    private static final MediaType IMG = MediaType.parse(IMG_TYPE);

    private Handler mHandler;

    @Inject
    public OkHttpClient mOkHttpClient2;

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
        CookieJar cookieJar = mOkHttpClient2.cookieJar();
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
        CookieJar cookieJar = mOkHttpClient2.cookieJar();
        if (cookieJar instanceof MemoryCookieStore) {
            MemoryCookieStore cookieStore = (MemoryCookieStore) cookieJar;
            return null == url ? cookieStore.getCookies() : cookieStore.get(url);
        }
        return null;
    }

    /**
     * 获取请求地址返回的字符串
     *
     * @param url             请求地址
     * @param requestCallback 请求返回调用函数
     * @param tag             请求标签
     * @throws IOException 请求报错信息
     */
    public void getString(String url, final IRequestStringCallback requestCallback, String tag) {
        Request request = createUrlGetRequest(url, tag);
        Call call = mOkHttpClient2.newCall(request);// new call

        call.enqueue(new Callback() { // 请求加入调度
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String resultStr = response.body().string();
                if (requestCallback != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            requestCallback.StringResponse(true, resultStr, null);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, final IOException ioexception) {
                if (requestCallback != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            requestCallback.StringResponse(false, "", ioexception);
                        }
                    });
                }
            }
        });
    }

    /**
     * 使用get方法获取请求后返回的json数据
     *
     * @param url
     * @param t        返回的json数据转换类型
     * @param callback
     * @param tag
     */
    public <T> void getGson(String url, final Class<T> t, final IRequestGsonCallback callback, Object tag) {
        Request request = createUrlGetRequest(url, tag);
        Call call = mOkHttpClient2.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                final T resultObj = HttpUtil2.parsingCustomJson(t, result);
                if (callback != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (JudgeUtil.isNotEmpty(resultObj)) {
                                callback.JsonResponse(true, resultObj, null);
                            } else {
                                try {
                                    callback.JsonResponse(true, t.newInstance(), null);
                                } catch (InstantiationException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, final IOException ioexception) {
                if (callback != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.JsonResponse(false, null, ioexception);
                        }
                    });
                }

            }
        });
    }

    private <T> void postGsonImpl(Request request, final Class<T> t, final IRequestGsonCallback callback) {
        Call call = mOkHttpClient2.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() != 200) {
                    return;
                }
                String result = response.body().string();
                final T resultObj = HttpUtil2.parsingJson(t, result);
                if (callback != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.JsonResponse(true, resultObj, null);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, final IOException ioexception) {
                if (callback != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.JsonResponse(false, null, ioexception);
                        }
                    });
                }
            }
        });
    }

    /**
     * 使用post方法上传json格式数据并获取请求返回的json数据
     *
     * @param url
     * @param obj      上传的数据类型（用以转换成json数据） eg. Map<String, Object>
     * @param t        返回的数据类型（用以转换成json数据）
     * @param callback
     * @param tag
     */
    public <T> void postGson(String url, Object obj, Class<T> t, IRequestGsonCallback callback, Object tag) {
        Request request = createGSONPostRequest(url, HttpUtil2.createJsonString(obj), tag);
        postGsonImpl(request, t, callback);
    }

    /**
     * 使用post方法上传类的map格式数据并获取请求返回的json数据
     */
    public <T> void postGson(String url, Map<String, String> params, Class<T> t, IRequestGsonCallback callback, Object
            tag) {
        Request request = createParamsPostRequest(url, params, tag);
        postGsonImpl(request, t, callback);
    }

    /**
     * 上传文件
     *
     * @param url           上传文件地址
     * @param localFilePath 上传文件本地地址
     * @param callback
     * @param tag
     */
    public void uploadFile(String url, final String localFilePath, final IRequestFileCallback callback, Object tag) {
        Request request = createFilePostRequest(url, new File(localFilePath), callback, tag);
        Call call = mOkHttpClient2.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException ioexception) {
                if (callback != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.FileResponse(false, ioexception, localFilePath);
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (callback != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.FileResponse(true, null, localFilePath);
                        }
                    });
                }
            }
        });
    }

    /**
     * 上传文件
     *
     * @param url           上传文件地址
     * @param localFilePath 上传文件本地地址
     * @param t             上传文件后服务器返回的json数据类
     * @param callback
     * @param tag
     */
    public <T> void uploadFile(String url, final String localFilePath, final Class<T> t, final IRequestGsonCallback
            callback, Object tag) {
        Request request = createFilePostRequest(url, new File(localFilePath), tag);
        Call call = mOkHttpClient2.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException ioexception) {
                if (callback != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.JsonResponse(false, null, ioexception);
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (callback != null) {
                    String result = response.body().string();
                    final T resultObj = HttpUtil2.parsingCustomJson(t, result);

                    if (callback != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (JudgeUtil.isNotEmpty(resultObj)) {
                                    callback.JsonResponse(true, resultObj, null);
                                } else {
                                    try {
                                        callback.JsonResponse(true, t.newInstance(), null);
                                    } catch (InstantiationException e) {
                                        e.printStackTrace();
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }
                }
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
    public void downLoadImage(String url, final IRequestBitmapCallback callback, Object tag) {
        Request request = createUrlGetRequest(url, tag);
        Call call = mOkHttpClient2.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException ioexception) {
                if (callback != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.BitmapResponse(false, null, ioexception);
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.BitmapResponse(true, bitmap, null);
                        }
                    }
                });
            }
        });
    }

    /**
     * 下载文件
     *
     * @param url              下载文件的地址
     * @param downLoadSavePath 文件下载后保存目录
     * @param filename         下载后保存的文件名
     * @param callback         下载后调用的函数
     * @param tag              请求标签
     */
    public void downLoadFile(String url, String downLoadSavePath, String filename, final IRequestFileCallback callback,
                             Object tag) {
        Request request = createUrlGetRequest(url, tag);
        File dir = new File(downLoadSavePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        final File file = new File(dir, filename);
        Call call = mOkHttpClient2.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException ioexception) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.FileResponse(false, ioexception, "");
                        }
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
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
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.ProgressResponse(finalSum, total);
                        }

                    });
                }
                outputStream.flush();
                try {
                    inputStream.close();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.FileResponse(true, null, file.getAbsolutePath());
                        }
                    }
                });
            }
        });
    }

    /**
     * 取消请求
     *
     * @param tag 根据此标签取消请求
     */
    public void cancelRequest(Object tag) {
        for (Call call : mOkHttpClient2.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                try {
                    call.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        for (Call call : mOkHttpClient2.dispatcher().runningCalls()) {
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
     * 创建get方法的地址请求
     *
     * @param url
     * @param tag
     * @return
     */
    private Request createUrlGetRequest(String url, Object tag) {
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
    private Request createGSONPostRequest(String url, String json, Object tag) {
        RequestBody body = RequestBody.create(JSON, json);
        Request.Builder builder = new Request.Builder();
        if (tag != null) {
            builder.tag(tag);
        }
        return builder.url(url).post(body).build();
    }

    /**
     * 创建post方法请求，用以传递参数
     *
     * @param url
     * @param params 参数
     * @param tag
     * @return
     */
    private Request createParamsPostRequest(String url, Map<String, String> params, Object tag) {
        RequestBody formBody = null;
        if (params != null) {
            FormBody.Builder builder = new FormBody.Builder();
            for (Entry<String, String> entry : params.entrySet()) {
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

    /**
     * 创建上传文件请求
     *
     * @param url
     * @param file     需要上传的文件
     * @param callback
     * @param tag
     * @return
     */
    private Request createFilePostRequest(String url, File file, final IRequestFileCallback callback, Object tag) {
        String fileName = file.getName();
        RequestBody fileBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName, RequestBody.create(MediaType.parse(HttpUtil2.guessMimeType
                        (fileName)), file))
                .build();

        CountingRequestBody body = new CountingRequestBody(fileBody, new CountingRequestBody.Listener() {
            @Override
            public void onRequestProgress(final long bytesWritten, final long contentLength) {
                if (callback != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.ProgressResponse(bytesWritten, contentLength);
                        }
                    });
                }
            }
        });
        Request.Builder builder = new Request.Builder();
        if (tag != null) {
            builder.tag(tag);
        }
        return builder.url(url).post(body).build();
    }

    private Request createFilePostRequest(String url, File file, Object tag) {
        String fileName = file.getName();
        RequestBody fileBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName, RequestBody.create(MediaType.parse(HttpUtil2.guessMimeType
                        (fileName)), file))
                .build();
        Request.Builder builder = new Request.Builder();
        if (tag != null) {
            builder.tag(tag);
        }
        return builder.url(url).post(fileBody).build();
    }

    /**
     * Request Callback Interface
     */
    public interface IRequestGsonCallback {
        <T> void JsonResponse(Boolean isSuccess, T responseObj, IOException e);
    }

    public interface IRequestFileCallback {
        void FileResponse(Boolean isSuccess, IOException e, String path);

        void ProgressResponse(long progress, long total);
    }

    public interface IRequestStringCallback {
        void StringResponse(Boolean isSuccess, String responseStr, IOException e);
    }

    public interface IRequestBitmapCallback {
        void BitmapResponse(Boolean isSuccess, Bitmap responseBmp, IOException e);
    }

}


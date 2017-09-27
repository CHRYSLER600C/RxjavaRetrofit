package com.born.frame.httputils;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.born.frame.activity.BaseActivity;
import com.born.frame.common.CommonData;
import com.born.frame.dataclass.DataClass;
import com.born.frame.injector.component.ComponentHolder;
import com.born.frame.subscribers.ProgressSubscriber;
import com.born.frame.utils.LogicUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * 使用Retrofit方式获取数据，文件上传请使用OkHttpUtil2
 * Created by dongxie on 16/11/9.
 */
public class OkHttpUtil {

    /**
     * 网络请求格式定义函数
     */
    @Inject
    public RequestService mRequestService;

    /**
     * 实际网络请求类
     */
    @Inject
    public OkHttpClient mOkHttpClient;


    //构造方法私有
    private OkHttpUtil() {
        ComponentHolder.getAppComponent().inject(this);
    }

    /**
     * 在访问HttpMethods时创建单例
     */
    private static class SingletonHolder {
        private static final OkHttpUtil INSTANCE = new OkHttpUtil();
    }

    /**
     * 获取单例
     */
    public static OkHttpUtil getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 清空cookie
     */
    public void cleanCookie() {
        CookieJar cookieJar = mOkHttpClient.cookieJar();
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
        CookieJar cookieJar = mOkHttpClient.cookieJar();
        if (cookieJar instanceof MemoryCookieStore) {
            MemoryCookieStore cookieStore = (MemoryCookieStore) cookieJar;
            return null == url ? cookieStore.getCookies() : cookieStore.get(url);
        }
        return null;
    }

    /**
     * 用来统一处理Http的Code和Message
     */
    private class HttpResultFunc<T> implements Func1<T, T> {
        Object mObject; // Activity or Fragment

        public HttpResultFunc(Object object) {
            this.mObject = object;
        }

        @Override
        public T call(T data) {
            if (data != null && data instanceof DataClass) {
                DataClass dc = (DataClass) data;
                if (CommonData.RESULT_FAILED.equals(dc.code)) {
                    throw new ApiException(dc.message);
                } else if (CommonData.RESULT_UNLOGIN.equals(dc.code)) {
                    LogicUtil.loginIntent(mObject);
                    throw new ApiException(dc.message);
                }
            }
            return data;
        }
    }

    /**
     * ================================== Common Request Method As Below ==================================
     */

    /**
     * 通用网络请求方法
     *
     * @param methodName 方法名
     * @param params     url/path/header等参数集合，注意和RequestService里的定义函数保存顺序一致
     * @param map        ( GET:@QueryMap / POST:@FieldMap )
     * @param subscriber 订阅者
     * @param <T>        DataClass子类
     */
    public <T extends DataClass> void doRequestImpl(String methodName, List<String> params, Map<String, Object> map,
                                                    final Subscriber<T> subscriber) {
        Method method;
        Observable<T> requestResult;
        try {
            // 反射获取具体方法
            if (params == null && map == null) {
                method = mRequestService.getClass().getDeclaredMethod(methodName);
            } else {
                List<Class> classList = new ArrayList<>();
                if (params != null && params.size() > 0) {
                    for (int i = 0; i < params.size(); i++) {
                        classList.add(String.class);
                    }
                }
                if (map != null) {
                    classList.add(Map.class);
                }
                method = mRequestService.getClass().getDeclaredMethod(methodName, (Class[]) classList.toArray(new
                        Class[classList.size()]));
            }

            if (method != null) {
                method.setAccessible(true);
                if (params == null && map == null) {
                    requestResult = (Observable<T>) method.invoke(mRequestService);
                } else {
                    List<Object> objectList = new ArrayList<Object>();
                    if (params != null && params.size() > 0) {
                        objectList.addAll(params);
                    }
                    if (map != null) {
                        objectList.add(map);
                    }
                    requestResult = (Observable<T>) method.invoke(mRequestService, (Object[]) objectList.toArray(new
                            Object[objectList.size()]));
                }

                Object object = ((ProgressSubscriber) subscriber).getObject();
                BaseActivity baseActivity;
                if (object instanceof Activity) {
                    baseActivity = (BaseActivity) object;
                } else if (object instanceof Fragment) {
                    baseActivity = (BaseActivity) ((Fragment) object).getActivity();
                } else {
                    return; // 传入类型错误
                }
                requestResult
                        .compose(baseActivity.bindToLifecycle()) // Rxlifecycle自动取消订阅，防止内存泄露
                        .map(new HttpResultFunc(object))
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(new Action0() {
                            @Override
                            public void call() {
                                ((ProgressSubscriber) subscriber).showProgressDialog(); // 需要在主线程执行
                            }
                        })
                        .subscribeOn(AndroidSchedulers.mainThread()) // 指定doOnSubscribe运行在主线程
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(subscriber);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通用网络请求方法
     *
     * @param methodName 方法名
     * @param subscriber 订阅者
     * @param <T>        DataClass子类
     */
    public <T extends DataClass> void doRequestImpl(String methodName, Subscriber<T> subscriber) {
        doRequestImpl(methodName, null, null, subscriber);
    }

    /**
     * 通用网络请求方法
     *
     * @param methodName 方法名
     * @param params     url/path/header等参数集合，注意顺序
     * @param subscriber 订阅者
     * @param <T>        DataClass子类
     */
    public <T extends DataClass> void doRequestImpl(String methodName, List<String> params, Subscriber<T>
            subscriber) {
        doRequestImpl(methodName, params, null, subscriber);
    }

    /**
     * 通用网络请求方法
     *
     * @param methodName 方法名
     * @param map        ( GET:@QueryMap / POST:@FieldMap )
     * @param subscriber 订阅者
     * @param <T>        DataClass子类
     */
    public <T extends DataClass> void doRequestImpl(String methodName, Map<String, Object> map, Subscriber<T>
            subscriber) {
        doRequestImpl(methodName, null, map, subscriber);
    }

}

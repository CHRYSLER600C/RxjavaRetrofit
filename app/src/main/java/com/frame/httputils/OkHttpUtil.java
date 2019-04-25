package com.frame.httputils;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.blankj.utilcode.util.ObjectUtils;
import com.frame.activity.BaseActivity;
import com.frame.common.CommonData;
import com.frame.dataclass.DataClass;
import com.frame.injector.component.ComponentHolder;
import com.frame.observers.ProgressObserver;
import com.frame.utils.JU;
import com.frame.utils.LogicUtil;
import com.google.gson.Gson;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;


/**
 * 使用Retrofit方式获取数据，文件上传请使用OkHttpUtil2
 * Created by dongxie on 16/11/9.
 */
public class OkHttpUtil {

    private static final String JSON_TYPE = "application/json; charset=utf-8";

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
     * 通用网络请求方法
     *
     * @param methodName       方法名
     * @param params           url/path/header等参数集合，注意和RequestService里的定义函数保存顺序一致
     * @param map              ( GET:@QueryMap / POST:@FieldMap )
     * @param progressObserver 订阅者
     * @param <T>              DataClass子类
     */
    @SuppressWarnings("unchecked")
    public <T extends DataClass> void doRequestImpl(String methodName, List<String> params, Map<String, Object> map,
                                                    final ProgressObserver<T> progressObserver) {
        Method method;
        Observable<T> observable;
        try {
            // 反射获取具体方法
            if (params == null && map == null) {
                method = mRequestService.getClass().getDeclaredMethod(methodName);
            } else {
                List<Class> classList = new ArrayList<>();
                if (params != null && params.size() > 0) {
                    for (int i = 0; i < params.size(); i++) classList.add(String.class);
                }
                if (map != null) {
                    classList.add(map.get("POST_TYPE") == "JSON_STRING" ? RequestBody.class : Map.class);
                }
                method = mRequestService.getClass().getDeclaredMethod(methodName, (Class[]) classList.toArray(new
                        Class[classList.size()]));
            }

            method.setAccessible(true);
            if (params == null && map == null) {
                observable = (Observable<T>) method.invoke(mRequestService);
            } else {
                List<Object> objectList = new ArrayList<>();
                if (params != null && params.size() > 0) objectList.addAll(params);
                if (map != null) {
                    if (map.get("POST_TYPE") == "JSON_STRING") {
                        map.remove("POST_TYPE");
                        objectList.add(RequestBody.create(MediaType.parse(JSON_TYPE), new Gson().toJson(map)));
                    } else {
                        objectList.add(map);
                    }
                }
                observable = (Observable<T>) method.invoke(mRequestService, (Object[]) objectList.toArray(new
                        Object[objectList.size()]));
            }

            Object object = progressObserver.getObject();
            BaseActivity baseActivity;
            if (object instanceof Activity) {
                baseActivity = (BaseActivity) object;
            } else if (object instanceof Fragment) {
                baseActivity = (BaseActivity) ((Fragment) object).getActivity();
            } else {
                return; // 传入类型错误
            }
            if(ObjectUtils.isEmpty(baseActivity)) return;

            baseActivity.add2Disposable(observable
                    .map(new HttpResultFunc(object))
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe((c) -> progressObserver.showProgressDialogObserver())
                    .subscribeOn(AndroidSchedulers.mainThread()) // 指定doOnSubscribe运行在主线程
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(progressObserver));
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
     * 用来统一处理Http的Code和Message
     */
    private class HttpResultFunc<T> implements Function<T, T> {
        Object mObject; // Activity or Fragment

        private HttpResultFunc(Object object) {
            this.mObject = object;
        }

        @Override
        public T apply(T data) {
            if (data instanceof DataClass) {
                DataClass dc = (DataClass) data;
                int code = JU.i(dc.object, CommonData.CODE);
                String message = JU.s(dc.object, CommonData.MESSAGE);

                if (CommonData.RESULT_UNLOGIN == code) {
                    LogicUtil.gotoLogin(mObject);
                    throw new ApiException(message);
                } else if (CommonData.RESULT_SUCCESS != code) {
                    throw new ApiException(message);
                }
            }
            return data;
        }
    }

}

package com.frame.activity;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;

import com.blankj.utilcode.util.ToastUtils;
import com.frame.R;
import com.frame.common.CommonData;
import com.frame.dataclass.DataClass;
import com.frame.dataclass.bean.Event;
import com.frame.httputils.OkHttpUtil;
import com.frame.httputils.OkHttpUtil2;
import com.frame.httputils.OkHttpUtil2.IRequestFileCallback;
import com.frame.httputils.OkHttpUtil2.IRequestCallback;
import com.frame.httputils.RequestBuilder;
import com.frame.httputils.RequestBuilder.RequestObject;
import com.frame.observers.ProgressObserver;
import com.frame.observers.progress.ProgressDialogHandler;
import com.githang.statusbar.StatusBarCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * activity基类，继承此类可以使用butterknife来bind(view),还封装了数据请求，请求提示框和toast显示方法。
 */
public class BaseActivity extends AppCompatActivity {

    public BaseActivity mContext;
    private Unbinder mUnbinder;
    private ProgressDialogHandler mProgressDialogHandler;
    private CompositeDisposable mCompositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager
                .LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mContext = this;
        if (regEvent()) {
            EventBus.getDefault().register(this);
        }
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.title_bg_color));
    }

    public void setContentView(int layoutResId) {
        super.setContentView(layoutResId);
        mUnbinder = ButterKnife.bind(this);
    }

    public void setContentView(View view) {
        super.setContentView(view);
        mUnbinder = ButterKnife.bind(this);
    }

    public void setContentView(View view, LayoutParams params) {
        super.setContentView(view, params);
        mUnbinder = ButterKnife.bind(this);
    }

    public void showToast(String text) {
        ToastUtils.showShort(text);
    }


    public void showProgressDialog() {
        if (mProgressDialogHandler == null) {
            mProgressDialogHandler = new ProgressDialogHandler(mContext, null, true);
        }
        mProgressDialogHandler.obtainMessage(ProgressDialogHandler.SHOW_PROGRESS_DIALOG).sendToTarget();
    }

    public void dismissProgressDialog() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.DISMISS_PROGRESS_DIALOG).sendToTarget();
        }
    }

    public void add2Disposable(Object object) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        if (object instanceof Disposable) {
            mCompositeDisposable.add((Disposable) object);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
        if (regEvent()) {
            EventBus.getDefault().unregister(this);
        }
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
        mProgressDialogHandler = null;
        try {
            OkHttpUtil2.getInstance().cancelRequest(this.getLocalClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ========================================= EventBus =========================================
     */
    /**
     * 需要接收事件 重写该方法 并返回true
     */
    protected boolean regEvent() {
        return false;
    }

    /**
     * 子类接受事件 重写该方法
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommonEventBus(Event event) {
    }

    /**
     * 子类接受事件 重写该方法
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onCommonEventBusSticky(Event event) {
    }


    /**
     * ========================================= Request Method =========================================
     */
    public <T> void getRequest(RequestObject request, Class<T> t, IRequestCallback callback) {
        // 经测试，getLocalClassName方法拿的是对应子activity的
        OkHttpUtil2.getInstance().getGson(RequestBuilder.build(request), t, callback, this.getLocalClassName());
    }

    /**
     * 使用post方法上传json格式数据并获取请求返回的json数据
     *
     * @param obj Map<String, Object>
     */
    public <T> void postRequest(RequestObject request, Object obj, Class<T> t, IRequestCallback callback) {
        OkHttpUtil2.getInstance().postGson(RequestBuilder.build(request), obj, t, callback, this.getLocalClassName());
    }

    /**
     * 使用post方法上传map格式数据并获取请求返回的json数据
     */
    public <T> void postRequest(RequestObject request, Map<String, String> params, Class<T> t, IRequestCallback
            callback) {
        OkHttpUtil2.getInstance().postGson(RequestBuilder.build(request), params, t, callback, this.getLocalClassName
                ());
    }

    public <T> void uploadFile(RequestObject request, Map<String, Object> params, Class<T> t, IRequestFileCallback
            callback) {
        OkHttpUtil2.getInstance().uploadFile(RequestBuilder.build(request), params, t, callback, this
                .getLocalClassName());
    }

    public void downLoadImage(String url, IRequestCallback callback) {
        OkHttpUtil2.getInstance().downLoadImage(url, callback, this.getLocalClassName());
    }

    public void downLoadImage(String url, final ImageView iv) {
        OkHttpUtil2.getInstance().downLoadImage(url, new IRequestCallback() {
            @Override
            public <T> void ObjResponse(Boolean isSuccess, T responseObj, IOException e) {
                iv.setImageBitmap((Bitmap) responseObj);
            }
        }, this.getLocalClassName());
    }

    public void downLoadFile(String url, String saveDir, String saveFileName, IRequestFileCallback callback) {
        OkHttpUtil2.getInstance().downLoadFile(url, saveDir, saveFileName, callback, this.getLocalClassName());
    }


    /**
     * 通用网络请求方法 ============================================================================================
     *
     * @param methodName       方法名
     * @param params           url/path/header等参数集合，必须和RequestService里的定义函数保存顺序一致, 没有传null
     * @param map              ( GET:@QueryMap / POST:@FieldMap ), 没有传null
     * @param progressObserver 订阅者
     * @param <T>              DataClass子类
     */
    public static <T extends DataClass> void doRequestImpl(String methodName, List<String> params, Map<String,
            Object> map, final ProgressObserver<T> progressObserver) {
        OkHttpUtil.getInstance().doRequestImpl(methodName, params, map, progressObserver);
    }

    /**
     * Common Retrofit Method (Get)
     *
     * @param url 如果只有方法名，会采用默认的BaseURL
     * @param map 没有参数传null
     */

    public static <T extends DataClass> void doCommonGetImpl(String url, Map<String, Object> map, ProgressObserver<T>
            progressObserver) {
        List<String> params = new ArrayList<>();
        params.add((url.startsWith("http") ? "" : CommonData.SEVER_URL) + url);
        doRequestImpl("commonGet", params, map, progressObserver);
    }

    /**
     * Common Retrofit Method (Post)
     *
     * @param url 如果只有方法名，会采用默认的BaseURL
     * @param map 没有参数传null
     */
    public static <T extends DataClass> void doCommonPostImpl(String url, Map<String, Object> map, ProgressObserver<T>
            progressObserver) {
        List<String> params = new ArrayList<>();
        params.add((url.startsWith("http") ? "" : CommonData.SEVER_URL) + url);
        doRequestImpl("commonPost", params, map, progressObserver);
    }

    /**
     * Common Retrofit Method (Post),以json字符串传送
     *
     * @param url 如果只有方法名，会采用默认的BaseURL
     * @param map 没有参数传null
     */
    public static <T extends DataClass> void doCommonPostJsonImpl(String url, Map<String, Object> map,
                                                                  ProgressObserver<T> progressObserver) {
        List<String> params = new ArrayList<>();
        params.add((url.startsWith("http") ? "" : CommonData.SEVER_URL) + url);
        if (map != null) map.put("POST_TYPE", "JSON_STRING");
        doRequestImpl("commonPostJson", params, map, progressObserver);
    }
}
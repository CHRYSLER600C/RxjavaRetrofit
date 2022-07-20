package com.frame.activity;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.frame.R;
import com.frame.common.CommonData;
import com.frame.dataclass.bean.Event;
import com.frame.httputils.OkHttpUtil;
import com.frame.httputils.OkHttpUtil2;
import com.frame.httputils.OkHttpUtil2.IRequestCallback;
import com.frame.httputils.OkHttpUtil2.IRequestFileCallback;
import com.frame.httputils.RequestBuilder;
import com.frame.httputils.RequestBuilder.RequestObject;
import com.frame.observers.ProgressObserver;
import com.frame.observers.RecycleObserver;
import com.frame.observers.progress.ProgressDialogHandler;
import com.frame.other.ICallBack;
import com.frame.utils.CU;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zouxianbin.android.slide.SlideBackAppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
public class BaseActivity extends SlideBackAppCompatActivity {

    public BaseActivity mBActivity;
    private Unbinder mUnBinder;
    private ProgressDialogHandler mProgressDialogHandler;
    private CompositeDisposable mCompositeDisposable;
    protected View vStatusBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setSlideable(isActivityCanSlideBack());  //设置是否可以左滑返回，必须在super.onCreate（）之前

        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mBActivity = this;
        if (regEvent()) EventBus.getDefault().register(this);
        initImmersionBar();
    }

    public void setContentView(int layoutResId) {
        setContentView(LayoutInflater.from(this).inflate(layoutResId, null, false)); //滑动返回要求必须是setContentView(view)
    }

    public void setContentView(View view) {
        LinearLayout root = (LinearLayout) View.inflate(this, R.layout.activity_base_title, null);
        vStatusBar = root.findViewById(R.id.vStatusBar);
        if (getStatusBarBgColor() > 0) {
            vStatusBar.getLayoutParams().height = BarUtils.getStatusBarHeight();
            vStatusBar.setBackgroundColor(getResources().getColor(getStatusBarBgColor()));
        } else {
            vStatusBar.setVisibility(View.GONE); //返回0时隐藏
        }
        root.addView(view, new ViewGroup.LayoutParams(-1, -1));
        super.setContentView(root);
        mUnBinder = ButterKnife.bind(this);
        setShadowResource(R.drawable.shape_sliding_back_shadow); //设置Slide Back的阴影
        initSmartRefreshLayout();
    }

    /**
     * 初始化沉浸式状态栏，个性化请重载
     */
    protected void initImmersionBar() {
        CU.setImmersionBar(this, false);
    }

    public boolean isActivityCanSlideBack() { return true; }

    public int getStatusBarBgColor() {return R.color.title_bg_color;}

    public void rxPermissionsRequest(ICallBack icb, final String... permissions) {
        if (PermissionUtils.isGranted(permissions)) {
            if (icb != null) icb.dataCallback(true);
            return;
        }
        add2Disposable(new RxPermissions(this).request(permissions).subscribeWith(new RecycleObserver<Boolean>() {
            @Override
            public void onNext(@NotNull Boolean isGranted) {// 权限申请结果回调
                if (icb != null) icb.dataCallback(isGranted);
            }
        }));
    }

    public void showShort(int resText) {
        ToastUtils.showShort(getString(resText));
    }

    public void showShort(String text) {
        ToastUtils.showShort(text);
    }

    public void showLong(int resText) {
        ToastUtils.showLong(getString(resText));
    }

    public void showLong(String text) {
        ToastUtils.showLong(text);
    }

    public void showProgressDialog() {
        if (mProgressDialogHandler == null) {
            mProgressDialogHandler = new ProgressDialogHandler(mBActivity, null, true);
        }
        mProgressDialogHandler.obtainMessage(ProgressDialogHandler.SHOW_PROGRESS_DIALOG).sendToTarget();
    }

    public void dismissProgressDialog() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.DISMISS_PROGRESS_DIALOG).sendToTarget();
        }
    }

    protected void initSmartRefreshLayout() {
        SmartRefreshLayout srl = findViewById(R.id.smartRefreshLayout);
        if (srl != null) {
            if (getOnRefreshListener() != null) {
                srl.setRefreshHeader(new ClassicsHeader(mBActivity)).setOnRefreshListener(getOnRefreshListener());
            }
            if (getOnLoadMoreListener() != null) {
                srl.setRefreshFooter(new ClassicsFooter(mBActivity)).setOnLoadMoreListener(getOnLoadMoreListener());
            }
            srl.setEnableRefresh(getOnRefreshListener() != null);
            srl.setEnableLoadMore(getOnLoadMoreListener() != null);
        }
    }

    protected OnRefreshListener getOnRefreshListener() { return null;}

    protected OnLoadMoreListener getOnLoadMoreListener() { return null;}

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
        if (mUnBinder != null) {
            mUnBinder.unbind();
            mUnBinder = null;
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
        super.onDestroy();
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
     * @param <T>              如果传入String则返回原始数据，传DataClass则解析成json返回
     */
    public static <T> void doCommonRequest(String methodName, List<String> params, Map<String, Object> map,
                                           final ProgressObserver<T> progressObserver) {
        OkHttpUtil.getInstance().doRequestImpl(methodName, params, map, progressObserver);
    }

    /**
     * 如果T传入String则返回原始数据，传DataClass则解析成json返回
     *
     * @param url 如果只有方法名，会采用默认的BaseURL
     * @param map 没有参数传null
     */

    public static <T> void doCommonGet(String url, Map<String, Object> map, ProgressObserver<T> progressObserver) {
        List<String> params = new ArrayList<>();
        params.add((url.startsWith("http") ? "" : CommonData.SEVER_URL) + url);
        doCommonRequest(getMethodName(progressObserver, "commonGet"), params, map, progressObserver);
    }

    /**
     * 如果T传入String则返回原始数据，传DataClass则解析成json返回
     *
     * @param url 如果只有方法名，会采用默认的BaseURL
     * @param map 没有参数传null
     */
    public static <T> void doCommonPost(String url, Map<String, Object> map, ProgressObserver<T> progressObserver) {
        List<String> params = new ArrayList<>();
        params.add((url.startsWith("http") ? "" : CommonData.SEVER_URL) + url);
        doCommonRequest(getMethodName(progressObserver, "commonPost"), params, map, progressObserver);
    }

    /**
     * 以json字符串传送，如果T传入String则返回原始数据，传DataClass则解析成json返回
     *
     * @param url 如果只有方法名，会采用默认的BaseURL
     * @param map 没有参数传null
     */
    public static <T> void doCommonPostJson(String url, Map<String, Object> map, ProgressObserver<T> progressObserver) {
        List<String> params = new ArrayList<>();
        params.add((url.startsWith("http") ? "" : CommonData.SEVER_URL) + url);
        if (map != null) map.put("POST_TYPE", "JSON_STRING");
        doCommonRequest(getMethodName(progressObserver, "commonPostJson"), params, map, progressObserver);
    }

    /**
     * 根据T的类型（String/DataClass）来生成RequestServices的方法名
     */
    private static <T> String getMethodName(ProgressObserver<T> progressObserver, String name) {
        Type genType = progressObserver.getClass().getGenericSuperclass();
        Type[] types = ((ParameterizedType) genType).getActualTypeArguments();
        return name + (types[0].equals(String.class) ? "Raw" : "");
    }

}
package com.born.frame.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.born.frame.R;
import com.born.frame.dataclass.DataClass;
import com.born.frame.httputils.OkHttpUtil;
import com.born.frame.httputils.OkHttpUtil2;
import com.born.frame.httputils.OkHttpUtil2.IRequestBitmapCallback;
import com.born.frame.httputils.OkHttpUtil2.IRequestFileCallback;
import com.born.frame.httputils.OkHttpUtil2.IRequestGsonCallback;
import com.born.frame.httputils.RequestBuilder;
import com.born.frame.httputils.RequestBuilder.RequestObject;
import com.born.frame.utils.DeviceUtil;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import rx.Subscriber;

/**
 * activity基类，继承此类可以使用butterknife来bind(view),还封装了数据请求，请求提示框和toast显示方法。
 */
public class BaseActivity extends RxAppCompatActivity {

    protected ProgressDialog mProgressDialog;
    private boolean mIsDestroy;
    BaseActivity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager
                .LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mIsDestroy = false;
        mContext = this;
    }

    public void setContentView(int layoutResId) {
        super.setContentView(layoutResId);
        ButterKnife.bind(this);
    }

    public void setContentView(View view) {
        super.setContentView(view);
        ButterKnife.bind(this);
    }

    public void setContentView(View view, LayoutParams params) {
        super.setContentView(view, params);
        ButterKnife.bind(this);
    }

    public void showToast(String text) {
        if (!TextUtils.isEmpty(text)) {
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        }
    }

    public void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    public void gotoActivity(Class<?> cls) {
        startActivity(new Intent(mContext, cls));
    }

    public void gotoActivityForResult(Class<?> cls, int requestCode) {
        startActivityForResult(new Intent(mContext, cls), requestCode);
    }

    public void showProgressDialog() {
        if (mIsDestroy) {
            return;
        }
        if (mProgressDialog == null) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.dialog_progress, null);
            ImageView ivProgress = (ImageView) view.findViewById(R.id.progress_dialog_img);
            TextView tvMsg = (TextView) view.findViewById(R.id.progress_dialog_txt);
            ivProgress.setAnimation(AnimationUtils.loadAnimation(this, R.anim.dialog_loading_progressbar));
            tvMsg.setText(getResources().getString(R.string.loading));
            mProgressDialog = ProgressDialog.show(this, "", "");
            mProgressDialog.setContentView(view);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(true);
        } else {
            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        }
    }

    public void dismissProgressDialog() {
        if (mIsDestroy) {
            return;
        }
        if (null != mProgressDialog && mProgressDialog.isShowing()) {
            try {
                mProgressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        mIsDestroy = true;
        try {
            OkHttpUtil2.getInstance().cancelRequest(this.getLocalClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        DeviceUtil.releaseThreadPool();
        super.onDestroy();
    }


    /**
     * ================================== Request Method Add Below ==================================
     */

    public <T> void getRequest(RequestObject request, Class<T> t, IRequestGsonCallback callback) {
        // 经测试，getLocalClassName方法拿的是对应子activity的
        OkHttpUtil2.getInstance().getGson(RequestBuilder.build(request), t, callback, this.getLocalClassName());
    }

    /**
     * 使用post方法上传json格式数据并获取请求返回的json数据
     *
     * @param obj Map<String, Object>
     */
    public <T> void postRequest(RequestObject request, Object obj, Class<T> t, IRequestGsonCallback callback) {
        OkHttpUtil2.getInstance().postGson(RequestBuilder.build(request), obj, t, callback, this.getLocalClassName());
    }

    /**
     * 使用post方法上传map格式数据并获取请求返回的json数据
     */
    public <T> void postRequest(RequestObject request, Map<String, String> params, Class<T> t, IRequestGsonCallback
            callback) {
        OkHttpUtil2.getInstance().postGson(RequestBuilder.build(request), params, t, callback, this.getLocalClassName
                ());
    }

    public void uploadFile(RequestObject request, String filePath, IRequestFileCallback callback) {
        OkHttpUtil2.getInstance().uploadFile(RequestBuilder.build(request), filePath, callback, this
                .getLocalClassName());
    }

    public <T> void uploadFile(RequestObject request, String filePath, Class<T> t, IRequestGsonCallback callback) {
        OkHttpUtil2.getInstance().uploadFile(RequestBuilder.build(request), filePath, t, callback, this
                .getLocalClassName());
    }

    public void downLoadImage(RequestObject request, IRequestBitmapCallback callback) {
        OkHttpUtil2.getInstance().downLoadImage(RequestBuilder.build(request), callback, this.getLocalClassName());
    }

    public void downLoadFile(String url, String saveDir, String saveFileName, IRequestFileCallback callback) {
        OkHttpUtil2.getInstance().downLoadFile(url, saveDir, saveFileName, callback, this.getLocalClassName());
    }

    public void downLoadFile(RequestObject request, String saveDir, String saveFileName, IRequestFileCallback
            callback) {
        OkHttpUtil2.getInstance().downLoadFile(RequestBuilder.build(request), saveDir, saveFileName, callback, this
                .getLocalClassName());
    }

    // Retrofit Method ============================================================================================
    public <T extends DataClass> void doRequestImpl(String methodName, Subscriber<T> subscriber) {
        doRequestImpl(methodName, null, null, subscriber);
    }

    public <T extends DataClass> void doRequestImpl(String methodName, List<String> params, Subscriber<T> subscriber) {
        doRequestImpl(methodName, params, null, subscriber);
    }

    public <T extends DataClass> void doRequestImpl(String methodName, Map<String, Object> map, Subscriber<T>
            subscriber) {
        doRequestImpl(methodName, null, map, subscriber);
    }

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
        OkHttpUtil.getInstance().doRequestImpl(methodName, params, map, subscriber);
    }

    /**
     * Common Retrofit Method (Get/Post)
     */
    public <T extends DataClass> void doCommonGetImpl(String methodName, Map<String, Object> map, Subscriber<T>
            subscriber) {
        List<String> params = new ArrayList<>();
        params.add(methodName); // 方法名
        doRequestImpl("commonGet", params, map, subscriber);
    }

    public <T extends DataClass> void doCommonPostImpl(String methodName, Map<String, Object> map, Subscriber<T>
            subscriber) {
        List<String> params = new ArrayList<>();
        params.add(methodName); // 方法名
        doRequestImpl("commonPost", params, map, subscriber);
    }

}
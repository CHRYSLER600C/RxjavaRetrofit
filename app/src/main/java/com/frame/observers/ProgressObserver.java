package com.frame.observers;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.frame.activity.BaseActivity;
import com.frame.common.CommonData;
import com.frame.observers.progress.ProgressCancelListener;
import com.frame.observers.progress.ProgressDialogHandler;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import io.reactivex.observers.ResourceObserver;

/**
 * 用于在Http请求开始时，自动显示一个ProgressDialog
 * 在Http请求结束是，关闭ProgressDialog
 * 调用者自己对请求数据进行处理
 * Created by dongxie on 16/3/10.
 */
public abstract class ProgressObserver<T> extends ResourceObserver<T> implements ProgressCancelListener {

    private ProgressDialogHandler mProgressDialogHandler;
    private boolean mIsShowProgressDialog = true;
    private Object mObject; // Activity or Fragment
    private Context mContext;
    private Object mRefreshLayout;

    public ProgressObserver(Object object, boolean isShowProgressDialog) {
        this(object, isShowProgressDialog, null);
    }

    /**
     * @param object               Activity or Fragment
     * @param isShowProgressDialog 是否显示进度条
     * @param refreshLayout                  用于停止刷新
     */
    public ProgressObserver(Object object, boolean isShowProgressDialog, Object refreshLayout) {
        this.mObject = object;
        this.mIsShowProgressDialog = isShowProgressDialog;
        this.mRefreshLayout = refreshLayout;

        if (object instanceof Activity) {
            mContext = (BaseActivity) object;
        } else if (object instanceof Fragment) {
            mContext = ((Fragment) object).getActivity();
        }
        mProgressDialogHandler = new ProgressDialogHandler(mContext, this, true);
    }

    /**
     * 订阅开始时调用
     * 显示ProgressDialog
     */
    public void showProgressDialogObserver() {
        if (mProgressDialogHandler != null && mIsShowProgressDialog) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.SHOW_PROGRESS_DIALOG).sendToTarget();
        }
    }

    public void dismissProgressDialogObserver() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.DISMISS_PROGRESS_DIALOG).sendToTarget();
            mProgressDialogHandler = null;
        }
    }

    private void dismissAll() {
        dismissProgressDialogObserver(); //关闭菊花
        if (mRefreshLayout instanceof SmartRefreshLayout) { //停止刷新
            ((SmartRefreshLayout) mRefreshLayout).finishRefresh(true);
            ((SmartRefreshLayout) mRefreshLayout).finishLoadMore(true);
        }
        if (!this.isDisposed()) this.dispose(); // 立马取消订阅
    }

    /**
     * 获取 Activity or Fragment
     */
    public Object getObject() {
        return mObject;
    }

    /**
     * 取消ProgressDialog的时候，取消对observable的订阅，同时也取消了http请求
     */
    @Override
    public void onCancelProgress() {
        dismissAll();
    }

    /**
     * 将onNext方法中的返回结果交给Activity或Fragment自己处理
     *
     * @param t 创建Observer时的泛型类型
     */
    public abstract void onNext(T t);

    /**
     * 对错误进行统一处理
     * 隐藏ProgressDialog
     */
    @Override
    public void onError(Throwable e) {
        if (e instanceof SocketTimeoutException || e instanceof ConnectException) {
            Toast.makeText(mContext, CommonData.NETWORK_ERROR_MSG, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        dismissAll();
    }

    /**
     * 完成，隐藏ProgressDialog
     */
    @Override
    public void onComplete() {
        dismissAll();
    }
}
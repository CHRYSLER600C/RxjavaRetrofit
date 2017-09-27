package com.born.frame.subscribers;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.born.frame.activity.BaseActivity;
import com.born.frame.common.CommonData;
import com.born.frame.subscribers.progress.ProgressCancelListener;
import com.born.frame.subscribers.progress.ProgressDialogHandler;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import rx.Subscriber;

/**
 * 用于在Http请求开始时，自动显示一个ProgressDialog
 * 在Http请求结束是，关闭ProgressDialog
 * 调用者自己对请求数据进行处理
 * Created by dongxie on 16/3/10.
 */
public abstract class ProgressSubscriber<T> extends Subscriber<T> implements ProgressCancelListener {

    private ProgressDialogHandler mProgressDialogHandler;
    private boolean mIsShowProgressDialog = true;
    private Object mObject; // Activity or Fragment
    private Context mContext;

    /**
     * @param object               Activity or Fragment
     * @param isShowProgressDialog 是否显示进度条
     */
    public ProgressSubscriber(Object object, boolean isShowProgressDialog) {
        this.mObject = object;
        this.mIsShowProgressDialog = isShowProgressDialog;

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
    public void showProgressDialog() {
        if (mProgressDialogHandler != null && mIsShowProgressDialog) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.SHOW_PROGRESS_DIALOG).sendToTarget();
        }
    }

    private void dismissProgressDialog() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.DISMISS_PROGRESS_DIALOG).sendToTarget();
            mProgressDialogHandler = null;
        }
    }

    /**
     * 获取 Activity or Fragment
     */
    public Object getObject() {
        return mObject;
    }

    /**
     * 完成，隐藏ProgressDialog
     */
    @Override
    public void onCompleted() {
        dismissProgressDialog();
    }

    /**
     * 对错误进行统一处理
     * 隐藏ProgressDialog
     *
     * @param e
     */
    @Override
    public void onError(Throwable e) {
        if (e instanceof SocketTimeoutException) {
            Toast.makeText(mContext, CommonData.NETWORK_ERROR_MSG, Toast.LENGTH_SHORT).show();
        } else if (e instanceof ConnectException) {
            Toast.makeText(mContext, CommonData.NETWORK_ERROR_MSG, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        dismissProgressDialog();

    }

    /**
     * 将onNext方法中的返回结果交给Activity或Fragment自己处理
     *
     * @param t 创建Subscriber时的泛型类型
     */
    public abstract void onNext(T t);

    /**
     * 取消ProgressDialog的时候，取消对observable的订阅，同时也取消了http请求
     */
    @Override
    public void onCancelProgress() {
        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }
    }
}
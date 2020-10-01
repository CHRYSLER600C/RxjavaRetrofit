package com.frame.observers.progress;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.blankj.utilcode.util.ObjectUtils;
import com.frame.R;

import java.lang.ref.WeakReference;

/**
 * Created by dongxie on 16/3/10.
 */
public class ProgressDialogHandler extends Handler {

    public static final int SHOW_PROGRESS_DIALOG = 1;
    public static final int DISMISS_PROGRESS_DIALOG = 2;

    private boolean mCancelable;
    private ProgressDialog mProgressDialog;
    private final WeakReference<Context> mContext;
    private ProgressCancelListener mProgressCancelListener;

    public ProgressDialogHandler(Context context, ProgressCancelListener progressCancelListener, boolean cancelable) {
        super();
        this.mContext = new WeakReference<>(context);
        this.mProgressCancelListener = progressCancelListener;
        this.mCancelable = cancelable;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case SHOW_PROGRESS_DIALOG:
                initProgressDialog();
                break;
            case DISMISS_PROGRESS_DIALOG:
                dismissProgressDialog();
                break;
        }
    }

    private void initProgressDialog() {
        Context context = mContext.get();
        if (mProgressDialog == null && context != null) {
            View view = View.inflate(context, R.layout.dialog_progress, null);
            ImageView ivProgress = view.findViewById(R.id.progress_dialog_img);
            ivProgress.setAnimation(AnimationUtils.loadAnimation(context, R.anim.dialog_loading_progressbar));
            mProgressDialog = ProgressDialog.show(context, "", "");
            mProgressDialog.setContentView(view);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(mCancelable);
            if (mProgressDialog.getWindow() != null) mProgressDialog.getWindow().setBackgroundDrawable(null);

            if (mCancelable && ObjectUtils.isNotEmpty(mProgressCancelListener)) {
                mProgressDialog.setOnCancelListener((dialogInterface) -> mProgressCancelListener.onCancelProgress());
            }
        }
        if (null != mProgressDialog && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        if (mProgressCancelListener != null) {
            mProgressCancelListener = null;
        }
    }

}

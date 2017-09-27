package com.born.frame.subscribers.progress;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.born.frame.R;

/**
 * Created by dongxie on 16/3/10.
 */
public class ProgressDialogHandler extends Handler {

    public static final int SHOW_PROGRESS_DIALOG = 1;
    public static final int DISMISS_PROGRESS_DIALOG = 2;

    private ProgressDialog mProgressDialog;

    private Context mContext;
    private boolean mCancelable;
    private ProgressCancelListener mProgressCancelListener;

    public ProgressDialogHandler(Context context, ProgressCancelListener mProgressCancelListener,
                                 boolean cancelable) {
        super();
        this.mContext = context;
        this.mProgressCancelListener = mProgressCancelListener;
        this.mCancelable = cancelable;
    }

    private void initProgressDialog(){
        if (mProgressDialog == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.dialog_progress, null);
            ImageView ivProgress = (ImageView) view.findViewById(R.id.progress_dialog_img);
            TextView tvMsg = (TextView) view.findViewById(R.id.progress_dialog_txt);
            ivProgress.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.dialog_loading_progressbar));
            tvMsg.setText(mContext.getResources().getString(R.string.loading));
            mProgressDialog = ProgressDialog.show(mContext, "", "");
            mProgressDialog.setContentView(view);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(mCancelable);

            if (mCancelable) {
                mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        mProgressCancelListener.onCancelProgress();
                    }
                });
            }

            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        }
    }

    private void dismissProgressDialog(){
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
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

}

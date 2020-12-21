package com.frame.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.frame.R;
import com.frame.adapter.CommVHolder;

/**
 * Simple Dialog 通知型对话框
 */
public class CommonDialog extends Dialog {

    private Activity mActivity;

    public enum DialogType {
        TYPE_SIMPLE, TYPE_INPUT
    }

    public CommonDialog(Activity activity, int theme) {
        super(activity, theme);
        this.mActivity = activity;
    }

    @Override
    public void show() {
        //防止Dlg附属的页面关闭后，延迟收到的show引起异常
        if(mActivity == null || mActivity.isFinishing() || mActivity.isDestroyed()) return;

        super.show();

        //设置宽度全屏，要设置在show的后面
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().getDecorView().setPadding(100, 0, 100, 0);
        getWindow().setAttributes(layoutParams);
    }

    @Override
    public void dismiss() {
        //防止Dlg附属的页面关闭后，延迟收到的dismiss引起IllegalArgumentException: View=DecorView not attached to window manager
        if(mActivity == null || mActivity.isFinishing() || mActivity.isDestroyed()) return;
        super.dismiss();
    }

    public static class Builder {
        private Activity activity;
        private DialogType dialogType;
        private CharSequence title;
        private String hint;
        private String etText;
        private CharSequence message;
        private String strCancel;
        private String strOk;
        private boolean canCancelable = true;

        private View.OnClickListener cancelListener;
        private OnOkClickListener okListener;


        public Builder(Activity activity, DialogType dialogType) {
            this.activity = activity;
            this.dialogType = dialogType;
        }

        public Builder setTitle(int title) {
            this.title = (String) activity.getText(title);
            return this;
        }

        public Builder setTitle(CharSequence title) {
            this.title = title;
            return this;
        }

        public Builder setEtText(String etText) {
            this.etText = etText;
            return this;
        }

        public Builder setHint(String hint) {
            this.hint = hint;
            return this;
        }

        public Builder setHint(int hint) {
            this.hint = (String) activity.getText(hint);
            return this;
        }

        public Builder setMessage(CharSequence message) {
            this.message = message;
            return this;
        }

        public Builder setMessage(int message) {
            this.message = (String) activity.getText(message);
            return this;
        }

        public Builder setCancelBtn(int strCancel, View.OnClickListener listener) {
            this.strCancel = (String) activity.getText(strCancel);
            this.cancelListener = listener;
            return this;
        }

        public Builder setCancelBtn(String strCancel, View.OnClickListener listener) {
            this.strCancel = strCancel;
            this.cancelListener = listener;
            return this;
        }

        public Builder setOkBtn(int strOk, OnOkClickListener listener) {
            this.strOk = (String) activity.getText(strOk);
            this.okListener = listener;
            return this;
        }

        public Builder setOkBtn(String strOk, OnOkClickListener listener) {
            this.strOk = strOk;
            this.okListener = listener;
            return this;
        }

        public Builder setCancelable(boolean canCancelable) {
            this.canCancelable = canCancelable;
            return this;
        }

        /**
         * Create the simple dialog
         */
        public CommonDialog create() {
            CommonDialog dialog = new CommonDialog(activity, R.style.CommonDialog);
            View vDialog = View.inflate(activity, R.layout.dialog_common, null);
            CommVHolder h = CommVHolder.get(null, vDialog);

            if (DialogType.TYPE_SIMPLE == dialogType) {
                h.setVisibility(R.id.tvCommonDialogMessage, View.VISIBLE);
            } else if (DialogType.TYPE_INPUT == dialogType) {
                h.setVisibility(R.id.etCommonDialog, View.VISIBLE);
            }
            EditText et = h.findViewById(R.id.etCommonDialog);

            if (ObjectUtils.isNotEmpty(title)) {
                h.setText(R.id.tvCommonDialogTitle, title);
            } else {
                h.setVisibility(R.id.tvCommonDialogTitle, View.GONE);
            }
            if (ObjectUtils.isNotEmpty(etText)) {
                et.setText(etText);
            }
            if (ObjectUtils.isNotEmpty(hint)) {
                et.setHint(hint);
            }

            if (ObjectUtils.isNotEmpty(message)) {
                h.setText(R.id.tvCommonDialogMessage,message);
            } else {
                h.setVisibility(R.id.tvCommonDialogMessage,View.GONE);
            }

            if (ObjectUtils.isNotEmpty(strCancel)) {
                h.setText(R.id.tvCommonDialogCancel,strCancel);
            } else {
                h.setVisibility(R.id.tvCommonDialogCancel,View.GONE);
            }

            if (ObjectUtils.isNotEmpty(strOk)) {
                h.setText(R.id.tvCommonDialogOk,strOk);
            } else {
                h.setVisibility(R.id.tvCommonDialogOk,View.GONE);
            }

            h.findViewById(R.id.tvCommonDialogCancel).setOnClickListener((View v) -> {
                if (ObjectUtils.isNotEmpty(cancelListener)) {
                    cancelListener.onClick(h.findViewById(R.id.tvCommonDialogCancel));
                }
                dialog.dismiss();
            });

            h.findViewById(R.id.tvCommonDialogOk).setOnClickListener((View v) -> {
                if (DialogType.TYPE_INPUT == dialogType && ObjectUtils.isEmpty(et.getText().toString())) {
                    ToastUtils.showShort(ObjectUtils.getOrDefault(et.getHint().toString(), "请输入"));
                    return;
                }
                if (ObjectUtils.isNotEmpty(okListener)) {
                    okListener.onClick(h.findViewById(R.id.tvCommonDialogOk), et.getText().toString());
                }
                dialog.dismiss();
            });
            dialog.setContentView(vDialog);
            dialog.setCancelable(canCancelable);
            return dialog;
        }
    }

    public interface OnOkClickListener {
        void onClick(View v, String value);
    }
}

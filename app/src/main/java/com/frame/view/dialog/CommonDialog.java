package com.frame.view.dialog;

import android.app.Dialog;
import android.content.Context;
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

    public enum DialogType {
        TYPE_SIMPLE, TYPE_INPUT
    }

    public CommonDialog(Context context) {
        super(context);
    }

    public CommonDialog(Context context, int theme) {
        super(context, theme);
    }

    protected CommonDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void show() {
        super.show();

        //设置宽度全屏，要设置在show的后面
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().getDecorView().setPadding(100, 0, 100, 0);
        getWindow().setAttributes(layoutParams);
    }

    public static class Builder {
        private Context context;
        private DialogType dialogType;
        private CharSequence title;
        private String hint;
        private CharSequence message;
        private String strCancel;
        private String strOk;
        private boolean canCancelable = true;

        private View.OnClickListener cancelListener;
        private OnOkClickListener okListener;


        public Builder(Context context, DialogType dialogType) {
            this.context = context;
            this.dialogType = dialogType;
        }

        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        public Builder setTitle(CharSequence title) {
            this.title = title;
            return this;
        }

        public Builder setHint(String hint) {
            this.hint = hint;
            return this;
        }

        public Builder setHint(int hint) {
            this.hint = (String) context.getText(hint);
            return this;
        }

        public Builder setMessage(CharSequence message) {
            this.message = message;
            return this;
        }

        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        public Builder setCancelBtn(int strCancel, View.OnClickListener listener) {
            this.strCancel = (String) context.getText(strCancel);
            this.cancelListener = listener;
            return this;
        }

        public Builder setCancelBtn(String strCancel, View.OnClickListener listener) {
            this.strCancel = strCancel;
            this.cancelListener = listener;
            return this;
        }

        public Builder setOkBtn(int strOk, OnOkClickListener listener) {
            this.strOk = (String) context.getText(strOk);
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
            final CommonDialog dialog = new CommonDialog(context, R.style.CommonDialog);
            View vDialog = View.inflate(context, R.layout.dialog_common, null);
            final CommVHolder holder = CommVHolder.get(null, vDialog);

            if (DialogType.TYPE_SIMPLE == dialogType) {
                holder.setVisibility(R.id.tvCommonDialogMessage, View.VISIBLE);
            } else if (DialogType.TYPE_INPUT == dialogType) {
                holder.setVisibility(R.id.etCommonDialog, View.VISIBLE);
            }
            EditText et = holder.findViewById(R.id.etCommonDialog);

            if (ObjectUtils.isNotEmpty(title)) {
                holder.setText(R.id.tvCommonDialogTitle, title);
            } else {
                holder.setVisibility(R.id.tvCommonDialogTitle, View.GONE);
            }
            if (ObjectUtils.isNotEmpty(hint)) {
                et.setHint(hint);
            }

            if (ObjectUtils.isNotEmpty(message)) {
                holder.setText(R.id.tvCommonDialogMessage,message);
            } else {
                holder.setVisibility(R.id.tvCommonDialogMessage,View.GONE);
            }

            if (ObjectUtils.isNotEmpty(strCancel)) {
                holder.setText(R.id.tvCommonDialogCancel,strCancel);
            } else {
                holder.setVisibility(R.id.tvCommonDialogCancel,View.GONE);
            }

            if (ObjectUtils.isNotEmpty(strOk)) {
                holder.setText(R.id.tvCommonDialogOk,strOk);
            } else {
                holder.setVisibility(R.id.tvCommonDialogOk,View.GONE);
            }

            holder.findViewById(R.id.tvCommonDialogCancel).setOnClickListener((View v) -> {
                if (ObjectUtils.isNotEmpty(cancelListener)) {
                    cancelListener.onClick(holder.findViewById(R.id.tvCommonDialogCancel));
                }
                dialog.dismiss();
            });

            holder.findViewById(R.id.tvCommonDialogOk).setOnClickListener((View v) -> {
                if (DialogType.TYPE_INPUT == dialogType && ObjectUtils.isEmpty(et.getText().toString())) {
                    ToastUtils.showShort(ObjectUtils.getOrDefault(et.getHint().toString(), "请输入"));
                    return;
                }
                if (ObjectUtils.isNotEmpty(okListener)) {
                    okListener.onClick(holder.findViewById(R.id.tvCommonDialogOk), et.getText().toString());
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

package com.born.frame.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.born.frame.R;
import com.born.frame.utils.JudgeUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Simple Dialog 通知型对话框
 */
public class SimpleDialog extends Dialog {

    public SimpleDialog(Context context) {
        super(context);
    }

    public SimpleDialog(Context context, int theme) {
        super(context, theme);
    }

    protected SimpleDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder {
        private Context context;
        private String title;
        private String message;
        private String leftText;
        private String rightText;

        private View.OnClickListener leftListener, rightListener;


        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        public Builder setLeftBtn(int leftText, View.OnClickListener listener) {
            this.leftText = (String) context.getText(leftText);
            this.leftListener = listener;
            return this;
        }

        public Builder setLeftBtn(String leftText, View.OnClickListener listener) {
            this.leftText = leftText;
            this.leftListener = listener;
            return this;
        }

        public Builder setRightBtn(int rightText, View.OnClickListener listener) {
            this.rightText = (String) context.getText(rightText);
            this.rightListener = listener;
            return this;
        }

        public Builder setRightBtn(String rightText, View.OnClickListener listener) {
            this.rightText = rightText;
            this.rightListener = listener;
            return this;
        }

        /**
         * Create the simple dialog
         */
        public SimpleDialog create() {
            final SimpleDialog dialog = new SimpleDialog(context, R.style.SimpleDialog);
            View vDialog = View.inflate(context, R.layout.dialog_simple, null);
            final SimpleDialogHolder holder = new SimpleDialogHolder(vDialog);

            if (JudgeUtil.isNotEmpty(title)) {
                holder.tvDialogTitle.setText(title);
            } else {
                holder.tvDialogTitle.setVisibility(View.GONE);
            }
            if (JudgeUtil.isNotEmpty(message)) {
                holder.tvDialogMessage.setText(message);
            } else {
                holder.tvDialogMessage.setVisibility(View.GONE);
            }

            if (JudgeUtil.isNotEmpty(leftText)) {
                holder.tvDialogLeft.setText(leftText);
            }
            if (JudgeUtil.isNotEmpty(rightText)) {
                holder.tvDialogRight.setText(rightText);
            } else {
                holder.vLineCenter.setVisibility(View.GONE);
                holder.tvDialogRight.setVisibility(View.GONE);
            }

            holder.tvDialogLeft.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (JudgeUtil.isNotEmpty(leftListener)) {
                        leftListener.onClick(holder.tvDialogLeft);
                    }
                    dialog.dismiss();
                }
            });

            holder.tvDialogRight.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (JudgeUtil.isNotEmpty(rightListener)) {
                        rightListener.onClick(holder.tvDialogRight);
                    }
                    dialog.dismiss();
                }
            });
            dialog.setContentView(vDialog);
            return dialog;
        }

        public class SimpleDialogHolder {
            @Bind(R.id.tvDialogTitle)
            TextView tvDialogTitle;
            @Bind(R.id.tvDialogMessage)
            TextView tvDialogMessage;

            @Bind(R.id.tvDialogLeft)
            TextView tvDialogLeft;
            @Bind(R.id.vLineCenter)
            View vLineCenter;
            @Bind(R.id.tvDialogRight)
            TextView tvDialogRight;

            public SimpleDialogHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

}

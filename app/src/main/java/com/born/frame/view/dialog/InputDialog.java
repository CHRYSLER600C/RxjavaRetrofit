package com.born.frame.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.born.frame.R;
import com.born.frame.utils.JudgeUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Input Dialog
 */
public class InputDialog extends Dialog {

    public InputDialog(Context context) {
        super(context);
    }

    public InputDialog(Context context, int theme) {
        super(context, theme);
    }

    protected InputDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder {
        private Context context;
        private String title;
        private String hint;
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

        public Builder setHint(String hint) {
            this.hint = hint;
            return this;
        }

        public Builder setHint(int hint) {
            this.hint = (String) context.getText(hint);
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
         * Create the input dialog
         */
        public InputDialog create() {
            final InputDialog dialog = new InputDialog(context, R.style.SimpleDialog);
            View vDialog = View.inflate(context, R.layout.dialog_input, null);
            final InputDialogHolder holder = new InputDialogHolder(vDialog);

            if (JudgeUtil.isNotEmpty(title)) {
                holder.tvDialogTitle.setText(title);
            } else {
                holder.tvDialogTitle.setVisibility(View.GONE);
            }
            if (JudgeUtil.isNotEmpty(hint)) {
                holder.etDialogInput.setHint(hint);
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
                    String textInput = holder.etDialogInput.getText().toString();
                    if (JudgeUtil.isEmpty(textInput)) {
                        Toast.makeText(context, holder.etDialogInput.getHint().toString(), Toast.LENGTH_LONG).show();
                        return;
                    }

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

        public class InputDialogHolder {
            @Bind(R.id.tvDialogTitle)
            TextView tvDialogTitle;
            @Bind(R.id.etDialogInput)
            EditText etDialogInput;

            @Bind(R.id.tvDialogLeft)
            TextView tvDialogLeft;
            @Bind(R.id.vLineCenter)
            View vLineCenter;
            @Bind(R.id.tvDialogRight)
            TextView tvDialogRight;

            public InputDialogHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

}

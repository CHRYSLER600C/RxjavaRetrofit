package com.frame.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.frame.R;
import com.frame.adapter.CommVHolder;

import androidx.annotation.NonNull;

/**
 * 通用弹窗，PopWindow好像7.0以后无法屏蔽物理返回键
 */
public class CustomDlg extends Dialog {

    private IBackPressCallBack mIBackPressCallBack;
    private WindowManager.LayoutParams mLayoutParams;
    private Object mDlgData;

    private CustomDlg(Context context, int theme) {
        super(context, theme);
    }

    @Override
    public void show() {
        super.show();

        //设置宽度全屏，要设置在show的后面
        if (mLayoutParams == null) {
            mLayoutParams = getWindow().getAttributes();
            mLayoutParams.gravity = Gravity.CENTER_VERTICAL;
            mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            mLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            getWindow().getDecorView().setPadding(0, 0, 0, 0);
            getWindow().getDecorView().setBackgroundColor(Color.parseColor("#00000000")); // 设置背景透明
            getWindow().setAttributes(mLayoutParams);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (mIBackPressCallBack != null) {
                super.onKeyDown(keyCode, event);
                mIBackPressCallBack.onBackPressCallBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 设置点击返回键时回调
     */
    public CustomDlg setIBackPressCallBack(IBackPressCallBack iBackPressCallBack) {
        mIBackPressCallBack = iBackPressCallBack;
        return this;
    }

    /**
     * 设置临时数据存储
     */
    public CustomDlg setDlgData(Object dlgData) {
        this.mDlgData = dlgData;
        return this;
    }

    public Object getDlgData() {return mDlgData;}

    // 构造模式设置CustomDlg的属性 ==============================================================================

    public static class Builder {

        // 必传属性
        private Context mContext;
        private int mLayoutId;
        private ICallBackCustomDlg mICallBackCustomDlg;

        // 选传属性
        private Object mData = null;              // 数据传输 eg. adapter
        private boolean mCancelable = true;       // 是否返回键可关闭
        private boolean mOtherCancelable = true;  // 点击其它地方是否可关闭
        private int mAnimResId = 0;               // 动画资源

        /**
         * 构造函数
         *
         * @param context
         * @param layoutId           dlg_id
         * @param iCallBackCustomDlg 回调回去自己处理
         */
        public Builder(Context context, int layoutId, ICallBackCustomDlg iCallBackCustomDlg) {
            this.mContext = context;
            this.mLayoutId = layoutId;
            this.mICallBackCustomDlg = iCallBackCustomDlg;
        }

        // 设置属性
        // ==============================================================================
        public Builder setData(Object data) {
            this.mData = data;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.mCancelable = cancelable;
            return this;
        }

        public Builder setOtherCancelable(boolean otherCancelable) {
            this.mOtherCancelable = otherCancelable;
            return this;
        }

        public Builder setAnimResId(int animResId) {
            this.mAnimResId = animResId;
            return this;
        }
        // ==============================================================================

        public CustomDlg create() {
            CustomDlg customDlg = new CustomDlg(mContext, R.style.CommonDialog);
            View view = View.inflate(mContext, mLayoutId, null);
            mICallBackCustomDlg.drawUI(customDlg, CommVHolder.get(null, view), mData);

            customDlg.setContentView(view);
            customDlg.setCancelable(mCancelable);
            if (mAnimResId > 0) customDlg.getWindow().setWindowAnimations(mAnimResId);
            if (mOtherCancelable) view.setOnClickListener(v -> customDlg.dismiss());
            return customDlg;
        }
    }

    public interface ICallBackCustomDlg {
        /**
         * 处理页面UI
         */
        void drawUI(CustomDlg dlg, CommVHolder h, Object data);
    }

    /**
     * 点击返回键时回调
     */
    public interface IBackPressCallBack {
        void onBackPressCallBack();
    }
}

package com.frame.view.dialog;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

import com.frame.adapter.CommonViewHolder;

/**
 * 通用弹窗
 */
public class CommonPopView extends PopupWindow {

    public CommonPopView() {
        super();
    }

    public CommonPopView(Context context) {
        super(context);
    }

    public CommonPopView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommonPopView(View contentView, int width, int height) {
        super(contentView, width, height);
    }

    public CommonPopView(View contentView) {
        super(contentView);
    }

    public CommonPopView(int width, int height) {
        super(width, height);
    }

    public CommonPopView(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
    }

    // 设置属性
    // ==============================================================================

    /**
     * 显示或者关闭
     */
    public void showOrDismiss(View parent, int gravity, int x, int y) {
        if (!isShowing()) {
            showAtLocation(parent, gravity, x, y);
        } else {
            dismiss();
        }
    }

    public void showOrDismiss(View parent) {
        showOrDismiss(parent, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 设置返回键和菜单键响应
     */
    public void setDefaultKeyListener(View v) {
        v.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_MENU) && (isShowing())) {
                    dismiss();
                } else if (((keyCode == KeyEvent.KEYCODE_BACK) && (isShowing()))) {
                    dismiss();
                }
                return false;
            }
        });
    }

    // 设置属性
    // ==============================================================================

    @SuppressWarnings("rawtypes")
    public static class Builder {

        // 必传属性
        private Context mContext;
        private int mLayoutId;
        private LayoutInflater mInflater;
        private ICallBackPopView mHandler;

        // 选传属性
        private Object mAdapter = null; // listview adapter
        private boolean mIsOutsideTouchDismiss = true; // 是否点击后dismiss
        private boolean mIsBackDismiss = true; // 是否点击返回键后dismiss
        private int mViewWidth = LayoutParams.MATCH_PARENT;
        private int mViewHeight = LayoutParams.MATCH_PARENT;

        /**
         * 构造函数
         *
         * @param context
         * @param layoutId popupWindow对应的id
         * @param handle   回调回去自己处理
         */
        public Builder(Context context, int layoutId, ICallBackPopView handle) {
            this.mContext = context;
            this.mLayoutId = layoutId;
            this.mHandler = handle;
            this.mInflater = LayoutInflater.from(context);
        }

        // 设置属性
        // ==============================================================================
        public Builder setAdapter(Object adapter) {
            this.mAdapter = adapter;
            return this;
        }

        public Builder setOutsideTouchDismiss(boolean isOutsideTouchDismiss) {
            this.mIsOutsideTouchDismiss = isOutsideTouchDismiss;
            return this;
        }

        public Builder seBackDismiss(boolean isBackDismiss) {
            this.mIsBackDismiss = isBackDismiss;
            return this;
        }

        public Builder setViewSize(int width, int height) {
            this.mViewWidth = width;
            this.mViewHeight = height;
            return this;
        }
        // ==============================================================================

        public CommonPopView create() {
            final CommonPopView popupWindow = new CommonPopView(mContext);
            View view = mInflater.inflate(mLayoutId, null);
            if (mIsOutsideTouchDismiss) {
                view.setOnClickListener( (View v)-> popupWindow.dismiss() );
            }
            mHandler.handlePopView(popupWindow, CommonViewHolder.get(null, view), mAdapter);

            popupWindow.setWidth(mViewWidth);
            popupWindow.setHeight(mViewHeight);
            popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            popupWindow.setContentView(view);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);
            popupWindow.setBackgroundDrawable(mIsBackDismiss ? new BitmapDrawable() : null);
            return popupWindow;
        }
    }

    // 回调接口
    public interface ICallBackPopView {
        /**
         * 处理PopupWindow的页面
         */
        void handlePopView(CommonPopView popView, CommonViewHolder h, Object adapter);
    }
}

package com.frame.activity;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.frame.R;
import com.frame.view.TitleBar;

/**
 * 此类在BaseActivity的基础上，自动给界面添加了title功能
 */
public class BaseTitleActivity extends BaseActivity {

    public TitleBar mTitleBar;

    public void setContentView(int layoutResId) {
        setContentView(View.inflate(this, layoutResId, null));
    }

    public void setContentView(View view) {
        setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public void setContentView(View view, LayoutParams params) {
        LinearLayout root = (LinearLayout) View.inflate(this, R.layout.activity_base_title, null);
        mTitleBar = root.findViewById(R.id.titleBar);
        root.addView(view, params);
        super.setContentView(root, params);
    }

    /**
     * TitleBar Method ==========================================================================
     */
    public void setLeftBarHide() {
        mTitleBar.getLeftBar().setVisibility(View.GONE);
    }

    public void setTitleBarHide() {
        mTitleBar.getTitleBar().setVisibility(View.GONE);
    }

    // setter title --------------------------------
    public void setTitleText(String title) {
        mTitleBar.setTitleText(title);
    }

    public void setTitleText(int title) {
        mTitleBar.setTitleText(title);
    }

    public void setTitleBgColor(int color) {
        mTitleBar.setTitleBgColor(color);
    }
}

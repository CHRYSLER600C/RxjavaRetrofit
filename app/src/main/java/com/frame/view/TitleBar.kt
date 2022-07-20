package com.frame.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.frame.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TitleBar extends RelativeLayout {

    @BindView(R.id.llTitleBar)
    LinearLayout mLlTitleBar;

    @BindView(R.id.llLeftBar)
    LinearLayout mLlLeftBar;
    @BindView(R.id.ivLeft)
    ImageView mIvLeft;
    @BindView(R.id.tvLeft)
    TextView mTvLeft;

    @BindView(R.id.tvTitle)
    TextView mTvTitle;

    @BindView(R.id.llRightBar)
    LinearLayout mLlRightBar;
    @BindView(R.id.ivRight)
    ImageView mIvRight;
    @BindView(R.id.tvRight)
    TextView mTvRight;

    public TitleBar(Context context) {
        this(context, null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    /**
     * 初始化界面
     */
    private void initView(Context context) {
        View root = View.inflate(context, R.layout.include_base_title, this);
        ButterKnife.bind(root);
        setLeftBackClick();
    }

    /**
     * Getter Method
     */
    public LinearLayout getTitleBar() {  // 可供不需要的时候隐藏
        return mLlTitleBar;
    }

    // getter left --------------------------------
    public LinearLayout getLeftBar() {
        return mLlLeftBar;
    }

    public TextView getLeftText() {
        return mTvLeft;
    }

    public ImageView getLeftImg() {
        return mIvLeft;
    }

    // getter title --------------------------------
    public TextView getTitleText() {
        return mTvTitle;
    }

    // getter right --------------------------------
    public LinearLayout getRightBar() {
        return mLlRightBar;
    }

    public TextView getRightText() {
        return mTvRight;
    }

    public ImageView getRightImg() {
        return mIvRight;
    }

    /**
     * Setter Method
     */
    // setter left --------------------------------

    private void setLeftBackClick() {
        mLlLeftBar.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Context ctx = TitleBar.this.getContext();
                if (ctx instanceof Activity) {
                    ((Activity) ctx).onBackPressed();
                }
            }
        });
    }

    public void setLeftText(String text) {
        mTvLeft.setText(text);
        mTvLeft.setVisibility(View.VISIBLE);
    }

    public void setLeftImageDrawable(Drawable drawable) {
        mIvLeft.setImageDrawable(drawable);
        mIvLeft.setVisibility(View.VISIBLE);
    }

    public void setLeftImageResource(int resId) {
        mIvRight.setImageResource(resId);
        mIvRight.setVisibility(View.VISIBLE);
    }

    // setter title --------------------------------
    public void setTitleText(String title) {
        mTvTitle.setText(Html.fromHtml(title));
    }

    public void setTitleText(int title) {
        mTvTitle.setText(title);
    }

    public void setTitleBgColor(int color) {
        mLlTitleBar.setBackgroundColor(color);
    }

    public void setTitleBgDrawable(Drawable background) {
        mLlTitleBar.setBackgroundDrawable(background);
    }

    public void setTitleBgResource(int resid) {
        mLlTitleBar.setBackgroundResource(resid);
    }

    // setter right --------------------------------
    public void setRightText(String text) {
        mTvRight.setText(text);
        mTvRight.setVisibility(View.VISIBLE);
    }

    public void setRightText(int text) {
        mTvRight.setText(text);
        mTvRight.setVisibility(View.VISIBLE);
    }

    public void setRightImageDrawable(Drawable drawable) {
        mIvRight.setImageDrawable(drawable);
        mIvRight.setVisibility(View.VISIBLE);
    }

    public void setRightImageResource(int resId) {
        mIvRight.setImageResource(resId);
        mIvRight.setVisibility(View.VISIBLE);
    }

}

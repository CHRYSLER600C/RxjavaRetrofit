package com.born.frame.activity;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.born.frame.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 此类在BaseActivity的基础上，自动给界面添加了title功能
 */
public class BaseTitleActivity extends BaseActivity {

    private RootHolder mRootHolder;

    public void setContentView(int layoutResId) {
        setContentView(View.inflate(this, layoutResId, null));
    }

    public void setContentView(View view) {
        setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public void setContentView(View view, LayoutParams params) {
        LinearLayout root = (LinearLayout) View.inflate(this, R.layout.activity_base_title, null);
        mRootHolder = new RootHolder(root);
        root.addView(view, params);
        super.setContentView(root, params);
    }

    /**
     * Getter Method
     */
    public RelativeLayout getTitleContainer() {  // 可供不需要的时候隐藏
        return mRootHolder.rlTitleContainer;
    }

    // getter left --------------------------------
    public LinearLayout getLeftContainer() {
        return mRootHolder.llLeftContainer;
    }

    public TextView getLeftText() {
        return mRootHolder.tvLeft;
    }

    public ImageView getLeftImg() {
        return mRootHolder.ivLeft;
    }

    // getter title --------------------------------
    public TextView getTitleText() {
        return mRootHolder.tvTitle;
    }

    // getter right --------------------------------
    public LinearLayout getRightContainer() {
        return mRootHolder.llRightContainer;
    }

    public TextView getRightText() {
        return mRootHolder.tvRight;
    }

    public ImageView getRightImg() {
        return mRootHolder.ivRight;
    }

    /**
     * Setter Method
     */
    // setter left --------------------------------
    public void setLeftBackClick() {
        mRootHolder.llLeftContainer.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setLeftTextContent(String content) {
        mRootHolder.tvLeft.setText(content);
        mRootHolder.tvLeft.setVisibility(View.VISIBLE);
    }

    public void setLeftImageSrc(Drawable drawable) {
        mRootHolder.ivLeft.setImageDrawable(drawable);
        mRootHolder.ivLeft.setVisibility(View.VISIBLE);
    }

    public void setLeftVisible(int visible) {
        mRootHolder.llLeftContainer.setVisibility(visible);
    }

    // setter title --------------------------------
    public void setTitle(String title) {
        mRootHolder.tvTitle.setText(title);
    }

    public void setTitle(int title) {
        mRootHolder.tvTitle.setText(title);
    }

    public void setTitleBgColor(int color) {
        mRootHolder.rlTitleContainer.setBackgroundColor(color);
    }

    public void setTitleBgDrawable(Drawable background) {
        mRootHolder.rlTitleContainer.setBackgroundDrawable(background);
    }

    public void setTitleBgResource(int resid) {
        mRootHolder.rlTitleContainer.setBackgroundResource(resid);
    }

    // setter right --------------------------------
    public void setRightTextContent(String content) {
        mRootHolder.tvRight.setText(content);
        mRootHolder.tvRight.setVisibility(View.VISIBLE);
    }

    public void setRightTextContent(int content) {
        mRootHolder.tvRight.setText(content);
        mRootHolder.tvRight.setVisibility(View.VISIBLE);
    }

    public void setRightImageSrc(Drawable drawable) {
        mRootHolder.ivRight.setImageDrawable(drawable);
        mRootHolder.ivRight.setVisibility(View.VISIBLE);
    }

    public void setRightImageSrc(int resId) {
        mRootHolder.ivRight.setImageResource(resId);
        mRootHolder.ivRight.setVisibility(View.VISIBLE);
    }


    public static class RootHolder {
        @Bind(R.id.rlTitleContainer)
        RelativeLayout rlTitleContainer;

        @Bind(R.id.llLeftContainer)
        LinearLayout llLeftContainer;
        @Bind(R.id.ivLeft)
        ImageView ivLeft;
        @Bind(R.id.tvLeft)
        TextView tvLeft;

        @Bind(R.id.tvTitle)
        TextView tvTitle;

        @Bind(R.id.llRightContainer)
        LinearLayout llRightContainer;
        @Bind(R.id.ivRight)
        ImageView ivRight;
        @Bind(R.id.tvRight)
        TextView tvRight;

        public RootHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

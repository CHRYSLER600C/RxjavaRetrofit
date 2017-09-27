package com.born.frame.fragment;

import android.view.ViewGroup.LayoutParams;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.born.frame.R;
import com.born.frame.utils.JudgeUtil;
import com.trello.rxlifecycle.components.support.RxFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * <pre>
 * 采用Lazy方式加载的Fragment: lazyInitControl(), 否则调用: initControl()
 *
 * 注1:
 * 如果是与ViewPager一起使用，调用的是setUserVisibleHint。
 *
 * 注2:
 * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
 * 针对初始就show的Fragment 为了触发onHiddenChanged事件 达到lazy效果 需要先hide再show
 * eg:
 * transaction.hide(aFragment);
 * transaction.show(aFragment);
 *
 * mIsForceLoad忽略isFirstLoad的值，强制刷新数据，但仍要Visible & Prepared
 * 一般用于PagerAdapter需要同时刷新全部子Fragment的场景, 不要new新的PagerAdapter而采取reset数据的方式
 * 所以要求Fragment重新走lazyInitControl方法,
 * 故使用 {@link BaseTitleFragment#setForceLoad(boolean)}来让Fragment下次执行lazyInitControl
 */
public abstract class BaseTitleFragment extends RxFragment {

    private boolean mIsFragmentVisible;
    /**
     * 标志View已经初始化完成。 而isAdded有可能出现onCreateView没走完但是isAdded了
     */
    private boolean mIsPrepared;
    /**
     * 是否第一次加载
     */
    private boolean mIsFirstLoad = true;
    /**
     * 强制刷新数据
     */
    private boolean mIsForceLoad = false;

    private LinearLayout mRootView;
    private RootHolder mRootHolder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (JudgeUtil.isEmpty(mRootView)) {
            mRootView = (LinearLayout) inflater.inflate(R.layout.activity_base_title, null);
            mRootHolder = new RootHolder(mRootView);
            View view = setContentView(savedInstanceState);
            if (JudgeUtil.isNotEmpty(view)) {
                mRootView.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                ButterKnife.bind(this, view);
            }
            initControl();
        }

        mIsFirstLoad = true;
        mIsPrepared = true;
        lazyLoad();

        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    abstract protected View setContentView(Bundle savedInstanceState);

    /**
     * 非延迟初始化控件
     */
    protected void initControl() {
    }

    /**
     * 延迟初始化控件
     */
    protected void lazyInitControl() {
    }

    /**
     * 每次Fragment可见的时候调用
     */
    protected void onFragmentVisible() {
    }

    /**
     * 如果是与ViewPager一起使用，调用的是setUserVisibleHint
     *
     * @param isVisibleToUser 是否显示出来了
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            onVisible();
        } else {
            onInvisible();
        }
    }

    /**
     * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
     * 若是初始就show的Fragment 为了触发该事件 需要先hide再show
     *
     * @param hidden hidden True if the fragment is now hidden, false if it is not visible.
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            onVisible();
        } else {
            onInvisible();
        }
    }

    protected void onVisible() {
        mIsFragmentVisible = true;
        lazyLoad();
    }

    protected void onInvisible() {
        mIsFragmentVisible = false;
    }

    /**
     * 要实现延迟加载Fragment内容, 需要在 onCreateView 设置 mIsPrepared = true;
     */
    protected void lazyLoad() {
        if (isPrepared() && isFragmentVisible()) {
            if (!isFirstLoad()) {
                onFragmentVisible();
            }
            if (mIsForceLoad || isFirstLoad()) {
                mIsForceLoad = false;
                mIsFirstLoad = false;
                lazyInitControl();
                onFragmentVisible();
            }
        }
    }

    /**
     * 忽略isFirstLoad的值，强制刷新数据，但仍要Visible & Prepared
     */
    public void setForceLoad(boolean forceLoad) {
        this.mIsForceLoad = forceLoad;
    }

    public boolean isPrepared() {
        return mIsPrepared;
    }

    public boolean isFirstLoad() {
        return mIsFirstLoad;
    }

    public boolean isFragmentVisible() {
        return mIsFragmentVisible;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mIsPrepared = false;
    }

    /**
     * Getter Method==========================================================================
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

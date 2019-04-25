package com.frame.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ObjectUtils;
import com.frame.R;
import com.frame.activity.BaseActivity;
import com.frame.dataclass.bean.Event;
import com.frame.view.TitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.Unbinder;

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
public abstract class BaseTitleFragment extends Fragment {

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
    public TitleBar mTitleBar;
    private Unbinder mUnbinder;
    protected BaseActivity mBaseActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseActivity = (BaseActivity) getActivity();
        if (ObjectUtils.isEmpty(mRootView)) {
            mRootView = (LinearLayout) inflater.inflate(R.layout.activity_base_title, null);
            mTitleBar = mRootView.findViewById(R.id.titleBar);
            View view = setContentView(savedInstanceState);
            if (ObjectUtils.isNotEmpty(view)) {
                mRootView.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                mUnbinder = ButterKnife.bind(this, view);
            }
            if (regEvent()) {
                EventBus.getDefault().register(this);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
        if (regEvent()) {
            EventBus.getDefault().unregister(this);
        }
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

    // setter right --------------------------------
    public void setRightText(String content) {
        mTitleBar.setRightText(content);
    }

    /**
     * ========================================= EventBus =========================================
     */
    /**
     * 需要接收事件 重写该方法 并返回true
     */
    protected boolean regEvent() {
        return false;
    }

    /**
     * 子类接受事件 重写该方法
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommonEventBus(Event event) {
    }

    /**
     * 子类接受事件 重写该方法
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onCommonEventBusSticky(Event event) {
    }

}

package com.frame.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.frame.R;
import com.frame.activity.BaseActivity;
import com.frame.dataclass.bean.Event;
import com.frame.view.TitleBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 *
 */
public abstract class BaseTitleFragment extends Fragment {

    private LinearLayout mRootView;
    public TitleBar mTitleBar;
    private Unbinder mUnBinder;
    protected BaseActivity mBActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBActivity = (BaseActivity) getActivity();
        if (ObjectUtils.isEmpty(mRootView)) {
            mRootView = (LinearLayout) inflater.inflate(R.layout.activity_base_title, null);
            mTitleBar = mRootView.findViewById(R.id.titleBar);
            View view = setContentView(savedInstanceState);
            if (ObjectUtils.isNotEmpty(view)) {
                mRootView.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                mUnBinder = ButterKnife.bind(this, view);
            }
            if (regEvent()) EventBus.getDefault().register(this);
        }

        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initControl();
        initSmartRefreshLayout();
    }

    abstract protected View setContentView(Bundle savedInstanceState);

    /**
     * 初始化控件
     */
    protected void initControl() { }

    /**
     * 重载后事件点击传递到Fragment
     */
    public void onViewClicked(View view) {}

    public boolean onBackPressed() {
        return false;
    }


    private void initSmartRefreshLayout() {
        SmartRefreshLayout srl = mBActivity.findViewById(R.id.smartRefreshLayout);
        if (srl != null) {
            if (getOnRefreshListener() != null) {
                srl.setRefreshHeader(new ClassicsHeader(mBActivity)).setOnRefreshListener(getOnRefreshListener());
            }
            if (getOnLoadMoreListener() != null) {
                srl.setRefreshFooter(new ClassicsFooter(mBActivity)).setOnLoadMoreListener(getOnLoadMoreListener());
            }
            srl.setEnableRefresh(getOnRefreshListener() != null);
            srl.setEnableLoadMore(getOnLoadMoreListener() != null);
        }
    }

    protected OnRefreshListener getOnRefreshListener() { return null;}

    protected OnLoadMoreListener getOnLoadMoreListener() { return null;}

    @Override
    public void onDestroy() {
        mRootView = null;
        if (mUnBinder != null) {
            mUnBinder.unbind();
            mUnBinder = null;
        }
        if (regEvent()) EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * ===================================== TitleBar Method =====================================
     */
    public void setLeftBarHide() {
        mTitleBar.getLeftBar().setVisibility(View.INVISIBLE);
    }

    public void setTitleBarHide() {
        mTitleBar.getTitleBar().setVisibility(View.GONE);
    }

    public void setTitleText(String title) {
        mTitleBar.setTitleText(title);
    }

    public void setTitleText(int title) {
        mTitleBar.setTitleText(title);
    }

    public void setRightText(String content) {
        mTitleBar.setRightText(content);
    }

    public void toast(@StringRes int resId) {
        ToastUtils.showShort(getResources().getString(resId));  //适配多语言
    }

    public void toast(String msg) {
        ToastUtils.showShort(msg);
    }

    /**
     * ========================================= EventBus =========================================
     * 需要接收事件 重写该方法
     */
    protected boolean regEvent() {
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommonEventBus(Event event) {}

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onCommonEventBusSticky(Event event) {}

}

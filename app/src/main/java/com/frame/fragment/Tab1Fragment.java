package com.frame.fragment;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.frame.R;
import com.frame.activity.BaseActivity;
import com.frame.activity.SearchActivity;
import com.frame.dataclass.DataClass;
import com.frame.dataclass.bean.Template;
import com.frame.observers.ProgressObserver;
import com.frame.utils.JU;
import com.frame.utils.LogicUtil;
import com.google.gson.internal.LinkedTreeMap;
import com.scwang.smartrefresh.header.PhoenixHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.youth.banner.Banner;

import org.byteam.superadapter.SuperAdapter;
import org.byteam.superadapter.SuperViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 首页
 */
public class Tab1Fragment extends BaseTitleFragment {

    @BindView(R.id.srlTab1)
    SmartRefreshLayout mSmartRefreshLayout;

    @BindView(R.id.nsvTab1)
    NestedScrollView mNsvTab1;

    @BindView(R.id.bannerAdv)
    Banner mBannerAdv;

    @BindView(R.id.rvBlock)
    RecyclerView mRvBlock;//分类列表
    @BindView(R.id.rvTab1)
    RecyclerView mRvTab1;//文章列表

    @BindView(R.id.llGotoTop)
    LinearLayout mLlGotoTop;

    private int mCurrPage = 0;
    private SuperAdapter mSAdapterArticle;
    private List<LinkedTreeMap<String, Object>> mList = new ArrayList<>();

    @Override
    protected View setContentView(Bundle savedInstanceState) {
        return View.inflate(mBaseActivity, R.layout.fragment_tab1, null);
    }

    @Override
    protected void initControl() {
        setLeftBarHide();
        setTitleText("首页");

        mTitleBar.setRightImageResource(R.drawable.ic_search_white_24dp);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTitleBar.getRightImg().getLayoutParams();
        lp.width = ConvertUtils.dp2px(25);
        mTitleBar.getRightImg().setLayoutParams(lp);
        mTitleBar.getRightBar().setOnClickListener(view -> ActivityUtils.startActivity(SearchActivity.class));

        mRvBlock.setLayoutManager(new GridLayoutManager(mBaseActivity, 4));
        mRvBlock.setAdapter(getSuperAdapterBlock());

        mRvTab1.setLayoutManager(new LinearLayoutManager(mBaseActivity));
        mRvTab1.setAdapter(mSAdapterArticle = WxArticleDetailFragment.getSuperAdapter(mBaseActivity, mList));
        mRvTab1.setNestedScrollingEnabled(false);

        mNsvTab1.setOnScrollChangeListener((NestedScrollView nestedScrollView, int scrollX, int scrollY, int
                oldScrollX, int oldScrollY) -> {
            mLlGotoTop.setVisibility(scrollY >= ScreenUtils.getScreenHeight() / 2 ? View.VISIBLE : View.GONE);
        });

        setSmartRefreshLayout();
        getBannerData();
        getNetData(mCurrPage, true);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mBannerAdv != null) {
            mBannerAdv.startAutoPlay();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mBannerAdv != null) {
            mBannerAdv.stopAutoPlay();
        }
    }

    @OnClick(R.id.llGotoTop)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llGotoTop:
                mNsvTab1.fling(0);
                mNsvTab1.smoothScrollTo(0, 0);
                break;
        }
    }

    private void setSmartRefreshLayout() {
        mSmartRefreshLayout.setRefreshHeader(new PhoenixHeader(mBaseActivity));
        mSmartRefreshLayout.setRefreshFooter(new BallPulseFooter(mBaseActivity));
        mSmartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            getBannerData();
            getNetData(mCurrPage = 0, false);
        });
        mSmartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            getNetData(++mCurrPage, false);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                default:
                    break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private SuperAdapter getSuperAdapterBlock() {
        return new SuperAdapter<Template>(mBaseActivity, LogicUtil.getBlockList(), R.layout.item_block_list) {
            @Override
            public void onBind(SuperViewHolder holder, int viewType, int layoutPosition, Template template) {
                holder.setImageResource(R.id.ivIconBlock, template.resId);
                holder.setText(R.id.tvTextBlock, template.content);
                holder.itemView.setOnClickListener(view -> {
                    if (!Build.MANUFACTURER.contains("samsung") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(mBaseActivity, view,
                                getString(R.string.share_view));
                        ActivityUtils.startActivity(template.cls, options.toBundle());
                    } else {
                        ActivityUtils.startActivity(template.cls);
                    }
                });
            }
        };
    }

    private void getBannerData() {
        BaseActivity.doCommonGetImpl("banner/json", null, new ProgressObserver<DataClass>(mBaseActivity, false) {
            @Override
            public void onNext(DataClass dc) {
                LogicUtil.setBannerDataAndShow(mBaseActivity, mBannerAdv, JU.al(dc.object, "data"));
            }
        });
    }

    private void getNetData(int currPage, boolean isLoading) {
        BaseActivity.doCommonGetImpl("article/list/" + currPage + "/json", null, new ProgressObserver<DataClass>
                (mBaseActivity, isLoading, mSmartRefreshLayout) {
            @Override
            public void onNext(DataClass dc) {
                LinkedTreeMap<String, Object> data = JU.m(dc.object, "data");
                mSmartRefreshLayout.setEnableLoadMore(!JU.b(data, "over"));
                if (0 == JU.i(data, "offset")) mList.clear();
                mList.addAll(JU.al(data, "datas"));
                mSAdapterArticle.notifyDataSetChanged();
            }
        });
    }

}

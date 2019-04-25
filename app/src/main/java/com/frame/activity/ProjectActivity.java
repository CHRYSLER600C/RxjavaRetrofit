package com.frame.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.frame.R;
import com.frame.dataclass.DataClass;
import com.frame.httputils.ImageLoaderUtil;
import com.frame.observers.ProgressObserver;
import com.frame.utils.CommonUtil;
import com.frame.utils.JU;
import com.google.gson.internal.LinkedTreeMap;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;

import org.byteam.superadapter.SuperAdapter;
import org.byteam.superadapter.SuperViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 */
public class ProjectActivity extends BaseTitleActivity {

    @BindView(R.id.srlProject)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.rvProject)
    RecyclerView mRecyclerView;

    @BindView(R.id.drawerLayout)
    public DrawerLayout mDrawerLayout; // 滑动菜单
    @BindView(R.id.rvSlide)
    RecyclerView mRecyclerViewSlide;

    private SuperAdapter mSuperAdapterType;
    private SuperAdapter mSuperAdapter;
    private List<LinkedTreeMap<String, Object>> mListType = new ArrayList<>();
    private List<LinkedTreeMap<String, Object>> mList = new ArrayList<>();
    private int mCurrId = 0;
    private int mCurrPage = 1;
    private int mLastClickPos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        initControl();
    }

    private void initControl() {
        setTitleText("知识体系");
        mTitleBar.setRightText("项目");
        mTitleBar.setRightImageResource(R.drawable.ic_arrow_drop_down_white_24dp);
        mTitleBar.getRightBar().setOnClickListener(view -> {
                    if (mDrawerLayout.isDrawerOpen(Gravity.END)) mDrawerLayout.closeDrawer(Gravity.END);
                    else mDrawerLayout.openDrawer(Gravity.END);
                }
        );

        mRecyclerViewSlide.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerViewSlide.setHasFixedSize(true);
        mRecyclerViewSlide.setAdapter(mSuperAdapterType = getSuperAdapterType());

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mSuperAdapter = getSuperAdapter());

        setSmartRefreshLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ObjectUtils.isEmpty(mListType)) {
            getNetDataType();
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.END)) {
            mDrawerLayout.closeDrawer(Gravity.END);
            return;
        }
        super.onBackPressed();
    }

    @OnClick(R.id.llGotoTop)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llGotoTop:
                mRecyclerView.smoothScrollToPosition(0);
                break;
        }
    }

    private void setSmartRefreshLayout() {
        mSmartRefreshLayout.setRefreshFooter(new ClassicsFooter(mContext));
        mSmartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            getNetData(mCurrId, mCurrPage = 1, false);
        });
        mSmartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            getNetData(mCurrId, ++mCurrPage, false);
        });
    }

    @SuppressWarnings("unchecked")
    private SuperAdapter getSuperAdapterType() {
        return new SuperAdapter<LinkedTreeMap<String, Object>>(mContext, mListType, R.layout.item_project_type) {
            @Override
            public void onBind(SuperViewHolder holder, int viewType, int layoutPosition, LinkedTreeMap<String,
                    Object> map) {
                holder.setText(R.id.tvProjectType, JU.sh(map, "name"));
                if (mLastClickPos == layoutPosition) {
                    holder.setBackgroundColor(R.id.tvProjectType, Color.parseColor("#d1d1d1"));
                }
                holder.itemView.setOnClickListener(view -> {
                            mLastClickPos = layoutPosition;
                            mSuperAdapterType.notifyDataSetChanged();
                            mDrawerLayout.closeDrawer(Gravity.END);
                            setTitleText(JU.s(mListType.get(mLastClickPos), "name"));
                            mCurrId = JU.i(mListType.get(mLastClickPos), "id");
                            getNetData(mCurrId, mCurrPage = 1, false);
                        }
                );
            }
        };
    }

    @SuppressWarnings("unchecked")
    private SuperAdapter getSuperAdapter() {
        return new SuperAdapter<LinkedTreeMap<String, Object>>(mContext, mList, R.layout.item_project_list) {
            @Override
            public void onBind(SuperViewHolder holder, int viewType, int layoutPosition, LinkedTreeMap<String,
                    Object> map) {
                ImageLoaderUtil.downloadImage(mContext, JU.s(map, "envelopePic"), holder.findViewById(R.id
                        .ivItemProject));
                holder.setText(R.id.tvItemProjectTitle, JU.s(map, "title"));
                holder.setTextColor(R.id.tvItemProjectTitle, CommonUtil.randomColor());
                holder.setText(R.id.tvItemProjectContent, JU.s(map, "desc"));
                holder.setText(R.id.tvItemProjectAuthor, "作者：" + JU.s(map, "author"));
                holder.setText(R.id.tvItemProjectTime, JU.s(map, "niceDate"));
                holder.itemView.setOnClickListener(view -> {
                            Intent i = new Intent(mContext, WebViewActivity.class)
                                    .putExtra(WebViewActivity.TYPE, WebViewActivity.TYPE_LOAD_URL)
                                    .putExtra("title", JU.s(map, "title"))
                                    .putExtra("url", JU.s(map, "link"));

                            if (!Build.MANUFACTURER.contains("samsung") && Build.VERSION.SDK_INT >= Build
                                    .VERSION_CODES.M) {
                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(mContext,
                                        view, getString(R.string.share_view));
                                ActivityUtils.startActivity(i, options.toBundle());
                            } else ActivityUtils.startActivity(i);
                        }
                );
            }
        };
    }

    private void getNetDataType() {
        BaseActivity.doCommonGetImpl("project/tree/json", null, new ProgressObserver<DataClass>(mContext, true) {
            @Override
            public void onNext(DataClass dc) {
                mListType.clear();
                mListType.addAll(JU.al(dc.object, "data"));
                mSuperAdapterType.notifyDataSetChanged();
                if (mListType.size() > 0) {
                    setTitleText(JU.s(mListType.get(0), "name"));
                    mCurrId = JU.i(mListType.get(0), "id");
                    getNetData(mCurrId, mCurrPage = 1, false);
                }
            }
        });
    }

    private void getNetData(int id, int currPage, boolean isLoading) {
        BaseActivity.doCommonGetImpl("project/list/" + currPage + "/json?cid=" + id, null, new
                ProgressObserver<DataClass>(mContext, isLoading, mSmartRefreshLayout) {
                    @Override
                    public void onNext(DataClass dc) {
                        LinkedTreeMap<String, Object> data = JU.m(dc.object, "data");
                        mSmartRefreshLayout.setEnableLoadMore(!JU.b(data, "over"));
                        if (0 == JU.i(data, "offset")) mList.clear();
                        mList.addAll(JU.al(data, "datas"));
                        mSuperAdapter.notifyDataSetChanged();
                    }
                });
    }


}
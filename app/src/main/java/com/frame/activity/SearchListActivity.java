package com.frame.activity;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.ObjectUtils;
import com.frame.R;
import com.frame.dataclass.DataClass;
import com.frame.fragment.WxArticleDetailFragment;
import com.frame.observers.ProgressObserver;
import com.frame.utils.JU;
import com.google.gson.internal.LinkedTreeMap;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;

import org.byteam.superadapter.SuperAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;


/**
 */
public class SearchListActivity extends BaseTitleActivity {

    @BindView(R.id.srlCommon)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.rvCommon)
    RecyclerView mRecyclerView;

    private String mCurrKey;
    private int mCurrPage = 0;
    private SuperAdapter mSuperAdapter;
    private List<LinkedTreeMap<String, Object>> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_layout_srl_rv);
        initControl();
    }

    protected void initControl() {
        mCurrKey = getIntent().getStringExtra("key");
        setTitleText(mCurrKey);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mSuperAdapter = WxArticleDetailFragment.getSuperAdapter(mContext, mList));

        setSmartRefreshLayout();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ObjectUtils.isEmpty(mList)) {
            getNetData(mCurrKey, mCurrPage = 0, true);
        }
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
            getNetData(mCurrKey, mCurrPage = 0, false);
        });
        mSmartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            getNetData(mCurrKey, ++mCurrPage, false);
        });
    }

    private void getNetData(String key, int currPage, boolean isLoading) {
        Map<String, Object> map = new HashMap<>();
        map.put("k", key);
        BaseActivity.doCommonPostImpl("article/query/" + currPage + "/json", map, new
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

package com.frame.fragment;


import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.frame.R;
import com.frame.activity.BaseActivity;
import com.frame.activity.WebViewActivity;
import com.frame.common.CommonData;
import com.frame.dataclass.DataClass;
import com.frame.observers.ProgressObserver;
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
public class WxArticleDetailFragment extends BaseTitleFragment {

    @BindView(R.id.srlCommon)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.rvCommon)
    RecyclerView mRecyclerView;

    private int mCurrId = 0; //当前公众号id
    private int mCurrPage = 1;
    private SuperAdapter mSuperAdapter;
    private List<LinkedTreeMap<String, Object>> mList = new ArrayList<>();

    public static WxArticleDetailFragment getInstance(String param1, String param2) {
        WxArticleDetailFragment fragment = new WxArticleDetailFragment();
        Bundle args = new Bundle();
        args.putString(CommonData.PARAM1, param1);
        args.putString(CommonData.PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View setContentView(Bundle savedInstanceState) {
        return View.inflate(mBActivity, R.layout.common_layout_srl_rv, null);
    }

    @Override
    protected void initControl() {
        super.initControl();
        setTitleBarHide();

        Bundle bundle = getArguments();
        try {
            mCurrId = (int) Double.parseDouble(bundle.getString(CommonData.PARAM1));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (mCurrId == 0) return;

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mBActivity));
        mRecyclerView.setAdapter(mSuperAdapter = getSuperAdapter(mBActivity, mList));

        setSmartRefreshLayout();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ObjectUtils.isEmpty(mList)) {
            getNetData(mCurrId, mCurrPage = 1, true);
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
        mSmartRefreshLayout.setRefreshFooter(new ClassicsFooter(mBActivity));
        mSmartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            getNetData(mCurrId, mCurrPage = 1, false);
        });
        mSmartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            getNetData(mCurrId, ++mCurrPage, false);
        });
    }

    @SuppressWarnings("unchecked")
    public static SuperAdapter getSuperAdapter(Activity activity, List<LinkedTreeMap<String, Object>> list) {
        return new SuperAdapter<LinkedTreeMap<String, Object>>(activity, list, R.layout.item_article_list) {
            @Override
            public void onBind(SuperViewHolder holder, int viewType, int layoutPosition, LinkedTreeMap<String,
                    Object> map) {
                holder.setText(R.id.tvArticleTitle, JU.sh(map, "title"))
                        .setText(R.id.tvChapterName, JU.s(map, "chapterName"))
                        .setText(R.id.tvSuperChapterName, JU.s(map, "superChapterName"))
                        .setText(R.id.tvAuthor, "作者：" + JU.s(map, "author"))
                        .setText(R.id.tvDate, JU.s(map, "niceDate"));
                holder.itemView.setOnClickListener(view -> {
                            Intent i = new Intent(activity, WebViewActivity.class)
                                    .putExtra(WebViewActivity.TYPE, WebViewActivity.TYPE_LOAD_URL)
                                    .putExtra("title", JU.s(map, "title"))
                                    .putExtra("url", JU.s(map, "link"));

                            if (!Build.MANUFACTURER.contains("samsung") && Build.VERSION.SDK_INT >= Build
                                    .VERSION_CODES.M) {
                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity,
                                        view, activity.getString(R.string.share_view));
                                ActivityUtils.startActivity(i, options.toBundle());
                            } else ActivityUtils.startActivity(i);
                        }
                );
            }
        };
    }

    private void getNetData(int id, int currPage, boolean isLoading) {
        BaseActivity.doCommonGet("wxarticle/list/" + id + "/" + currPage + "/json", null, new
                ProgressObserver<DataClass>(mBActivity, isLoading, mSmartRefreshLayout) {
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

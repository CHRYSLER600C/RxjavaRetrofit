package com.frame.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.frame.R;
import com.frame.common.CommonData;
import com.frame.dataclass.DataClass;
import com.frame.observers.ProgressObserver;
import com.frame.utils.CU;
import com.frame.utils.JU;
import com.google.gson.internal.LinkedTreeMap;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.byteam.superadapter.SuperAdapter;
import org.byteam.superadapter.SuperViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 */
public class KnowledgeHierarchyActivity extends BaseTitleActivity {

    @BindView(R.id.srlCommon)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.rvCommon)
    RecyclerView mRecyclerView;

    private SuperAdapter mSuperAdapter;
    private List<LinkedTreeMap<String, Object>> mList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_layout_srl_rv);
        initControl();
    }

    private void initControl() {
        setTitleText("知识体系");

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mBActivity));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mSuperAdapter = getSuperAdapter());

        setSmartRefreshLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ObjectUtils.isEmpty(mList)) {
            getNetData();
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
        mSmartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            getNetData();
        });
    }

    @SuppressWarnings("unchecked")
    private SuperAdapter getSuperAdapter() {
        return new SuperAdapter<LinkedTreeMap<String, Object>>(mBActivity, mList, R.layout.item_knowledge_hierarchy) {
            @Override
            public void onBind(SuperViewHolder holder, int viewType, int layoutPosition, LinkedTreeMap<String,
                    Object> map) {
                holder.setText(R.id.tvKnowledgeHierarchyTitle, JU.s(map, "name"))
                        .setTextColor(R.id.tvKnowledgeHierarchyTitle, CU.randomColor());

                StringBuilder content = new StringBuilder();
                List<LinkedTreeMap<String, Object>> list = JU.al(map, "children");
                for (LinkedTreeMap<String, Object> ltm : list) {
                    content.append(JU.s(ltm, "name")).append("   ");
                }
                holder.setText(R.id.tvKnowledgeHierarchyContent, content);
                holder.itemView.setOnClickListener(view -> {
                            ActivityUtils.startActivity(new Intent(mBActivity, WxArticleActivity.class)
                                    .putExtra(CommonData.PARAM1, map));
                        }
                );
            }
        };
    }

    private void getNetData() {
        BaseActivity.doCommonGet("tree/json", null, new ProgressObserver<DataClass>(mBActivity, true,
                mSmartRefreshLayout) {
            @Override
            public void onNext(DataClass dc) {
                mList.clear();
                mList.addAll(JU.al(dc.object, "data"));
                mSuperAdapter.notifyDataSetChanged();
            }
        });
    }


}
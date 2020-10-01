package com.frame.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.frame.R;
import com.frame.activity.ExampleActivity;
import com.frame.dataclass.bean.Template;
import com.frame.utils.CU;
import com.frame.utils.LU;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.byteam.superadapter.SuperAdapter;
import org.byteam.superadapter.SuperViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 我的
 */
public class Tab4Fragment extends BaseTitleFragment {

    @BindView(R.id.srlTab4)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.rvTab4)
    RecyclerView mRecyclerView;

    private SuperAdapter mSuperAdapter;
    private List<Template> mList = new ArrayList<>();

    @Override
    protected View setContentView(Bundle savedInstanceState) {
        return View.inflate(mBActivity, R.layout.fragment_tab4, null);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void initControl() {
        setLeftBarHide();
        setTitleText("我的");


        mRecyclerView.setLayoutManager(new LinearLayoutManager(mBActivity));
        mRecyclerView.setAdapter(mSuperAdapter = getSuperAdapter(mBActivity, LU.getListTab4()));

        setSmartRefreshLayout();
    }

    private void setSmartRefreshLayout() {
        mSmartRefreshLayout.setEnableRefresh(false);
        mSmartRefreshLayout.setEnableLoadMore(false);
        mSmartRefreshLayout.setOnRefreshListener(refreshLayout -> {
        });
    }

    public SuperAdapter getSuperAdapter(Activity activity, List<Template> list) {
        return new SuperAdapter<Template>(activity, list, R.layout.fg_item_text_text) {
            @Override
            public void onBind(SuperViewHolder h, int viewType, int layoutPosition, Template item) {
                CU.setTVDrawableLeft(h.findViewById(R.id.tvItemContent), item.resId, 25, 25, 15);
                CU.setTVDrawableRight(h.findViewById(R.id.tvItemMsg), R.drawable.ic_arrow_right, 10, 16
                        , 20);
                h.setText(R.id.tvItemContent, item.content);
                h.setText(R.id.tvItemMsg, item.content);
                h.itemView.setOnClickListener((View v) -> {
                    if (item.cls == ExampleActivity.class) {
                        ActivityUtils.startActivity(new Intent(mBActivity, item.cls).putExtra("id", ""));
                    } else {
                        ActivityUtils.startActivity(new Intent(mBActivity, item.cls)); // 不需要特殊处理的
                    }
                });
            }
        };
    }
}

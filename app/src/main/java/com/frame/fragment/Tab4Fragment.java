package com.frame.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.ActivityUtils;
import com.frame.R;
import com.frame.activity.ExampleActivity;
import com.frame.adapter.CommonAdapter;
import com.frame.adapter.CommonViewHolder;
import com.frame.dataclass.bean.Template;
import com.frame.utils.LogicUtil;
import com.frame.view.xlistview.XListView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

/**
 * 我的
 */
public class Tab4Fragment extends BaseTitleFragment {


    @BindView(R.id.xListView)
    XListView mXlv;

    private CommonAdapter mCommonAdapter;

    @Override
    protected View setContentView(Bundle savedInstanceState) {
        return View.inflate(mBaseActivity, R.layout.activity_common_xlv, null);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void initControl() {
        setLeftBarHide();
        setTitleText("我的");

        mCommonAdapter = new CommonAdapter(mBaseActivity, LogicUtil.getSelectList(), R.layout.fg_item_text_text,
                iItemCallback);
        mXlv.setAdapter(mCommonAdapter);
        mXlv.setPullRefreshEnable(true);
        mXlv.setPullLoadEnable(false);
        mXlv.setRefreshTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        mXlv.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public CommonAdapter.IItemCallback iItemCallback = new CommonAdapter.IItemCallback() {
        @Override
        public void handleItem(int position, ViewGroup parent, CommonViewHolder h, List list) {
            final Template item = (Template) list.get(position);

            h.setTextViewDrawableLeft(R.id.tvItemContent, item.resId, 25, 25, 20);
            h.setTextViewDrawableRight(R.id.tvItemMsg, R.drawable.ic_arrow_right, 10, 16, 20);
            h.setText(R.id.tvItemContent, item.content);
            h.setText(R.id.tvItemMsg, item.content);
            h.itemView.setOnClickListener((View v) -> {
                if (item.cls == ExampleActivity.class) {
                    ActivityUtils.startActivity(new Intent(mBaseActivity, item.cls).putExtra("id", ""));
                } else {
                    ActivityUtils.startActivity(new Intent(mBaseActivity, item.cls)); // 不需要特殊处理的
                }
            });
        }
    };
}

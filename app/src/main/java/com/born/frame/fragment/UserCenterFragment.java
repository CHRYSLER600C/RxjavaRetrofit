package com.born.frame.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.born.frame.R;
import com.born.frame.activity.ExampleActivity;
import com.born.frame.activity.GroupActivity;
import com.born.frame.utils.ImageUtil;
import com.born.frame.view.xlistview.XListView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 我的
 */
public class UserCenterFragment extends BaseTitleFragment implements View.OnClickListener {


    @Bind(R.id.xListView)
    XListView mXListView;

    private UserCenterHeader mUserCenterHeader;
    private boolean mIsLoadingMore = false;
    private GroupActivity mParent;

    @Override
    protected View setContentView(Bundle savedInstanceState) {
        mParent = (GroupActivity) getActivity();
        return View.inflate(mParent, R.layout.activity_common_xlv, null);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void initControl() {
        getTitleContainer().setVisibility(View.GONE);

        View header = View.inflate(mParent, R.layout.fragment_user_center_header, null);
        mUserCenterHeader = new UserCenterHeader(header);
        addHeader2ListView(header);

        mUserCenterHeader.tvExample.setOnClickListener(this);

        ImageUtil.setTextViewDrawableLeft(mUserCenterHeader.tvExample, R.drawable.ic_default, 25, 25, 20);
        ImageUtil.setTextViewDrawableRight(mUserCenterHeader.tvExample, R.drawable.ic_arrow_right, 11, 20, 20);
    }

    private void addHeader2ListView(View headerView) {
        mXListView.addHeaderView(headerView);
        mXListView.setAdapter(null);
        mXListView.setPullRefreshEnable(true);
        mXListView.setPullLoadEnable(false);
        mXListView.setRefreshTime("");
        mXListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                if (mIsLoadingMore) {
                    return;
                }
                mIsLoadingMore = true;
            }

            @Override
            public void onLoadMore() {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvExample:
                startActivity(new Intent(mParent, ExampleActivity.class));
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    class UserCenterHeader {
        @Bind(R.id.tvExample)
        TextView tvExample;

        public UserCenterHeader(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

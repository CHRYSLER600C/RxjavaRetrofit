package com.born.frame.view.pullrecyclerview;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * RecyclerView中的所有方法都可以在此类中设置，暴露出去以供调用
 */
public class PullRecyclerView extends com.born.frame.view.pullrecyclerview.PullBaseView<RecyclerView> {

    private TextView mTvNodata;

    public PullRecyclerView(Context context) {
        this(context, null);
    }

    public PullRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected LinearLayout createRecyclerView(Context context, AttributeSet attrs) {

        LinearLayout llHomeItem = new LinearLayout(context);
        llHomeItem.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams
                .MATCH_PARENT));
        llHomeItem.setOrientation(LinearLayout.VERTICAL);

        mTvNodata = new TextView(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
                .WRAP_CONTENT);
        mTvNodata.setText("暂无数据");
        mTvNodata.setLayoutParams(lp);
        mTvNodata.setBackgroundColor(Color.WHITE);
        mTvNodata.setGravity(Gravity.CENTER);
        mTvNodata.setVisibility(GONE);

        RecyclerView recyclerView = new RecyclerView(context, attrs);
        llHomeItem.addView(mTvNodata);
        llHomeItem.addView(recyclerView);

        return llHomeItem;
    }

    public void setNoData(Boolean isVisible) {
        if (mTvNodata != null) {
            mTvNodata.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        mRecyclerView.setLayoutManager(manager);
    }

}

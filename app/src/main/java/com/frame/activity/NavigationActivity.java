package com.frame.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.frame.R;
import com.frame.dataclass.DataClass;
import com.frame.observers.ProgressObserver;
import com.frame.utils.CU;
import com.frame.utils.JU;
import com.google.gson.internal.LinkedTreeMap;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.byteam.superadapter.SuperAdapter;
import org.byteam.superadapter.SuperViewHolder;

import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import q.rorbin.verticaltablayout.VerticalTabLayout;
import q.rorbin.verticaltablayout.adapter.TabAdapter;
import q.rorbin.verticaltablayout.widget.ITabView;
import q.rorbin.verticaltablayout.widget.TabView;

/**
 *
 */
public class NavigationActivity extends BaseTitleActivity {

    @BindView(R.id.vtlNavigation)
    VerticalTabLayout mTabLayout;
    @BindView(R.id.rvNavigation)
    RecyclerView mRecyclerView;

    private LinearLayoutManager mManager;
    private SuperAdapter mSuperAdapter;
    private List<LinkedTreeMap<String, Object>> mList = new ArrayList<>();

    private boolean needScroll;
    private int index;
    private boolean isClickTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        initControl();
    }

    protected void initControl() {
        setTitleText("导航");

        mManager = new LinearLayoutManager(mBActivity);
        mRecyclerView.setLayoutManager(mManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mSuperAdapter = getSuperAdapter());
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
                if (mTabLayout != null) {
                    mTabLayout.setTabSelected(0);
                }
                break;
        }
    }

    /**
     * Left tabLayout and right recyclerView linkage
     */
    private void leftRightLinkage() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (needScroll && (newState == RecyclerView.SCROLL_STATE_IDLE)) {
                    scrollRecyclerView();
                }
                rightLinkageLeft(newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (needScroll) {
                    scrollRecyclerView();
                }
            }
        });

        mTabLayout.addOnTabSelectedListener(new VerticalTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabView tabView, int i) {
                isClickTab = true;
                index = i;
                mRecyclerView.stopScroll();
                smoothScrollToPosition(i);
            }

            @Override
            public void onTabReselected(TabView tabView, int i) {
            }
        });
    }

    private void scrollRecyclerView() {
        needScroll = false;
        int indexDistance = index - mManager.findFirstVisibleItemPosition();
        if (indexDistance >= 0 && indexDistance < mRecyclerView.getChildCount()) {
            int top = mRecyclerView.getChildAt(indexDistance).getTop();
            mRecyclerView.smoothScrollBy(0, top);
        }
    }

    /**
     * Right recyclerView linkage left tabLayout
     * SCROLL_STATE_IDLE just call once
     *
     * @param newState RecyclerView new scroll state
     */
    private void rightLinkageLeft(int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            if (isClickTab) {
                isClickTab = false;
                return;
            }
            int firstPosition = mManager.findFirstVisibleItemPosition();
            if (index != firstPosition) {
                index = firstPosition;
                setChecked(index);
            }
        }
    }

    /**
     * Smooth right to select the position of the left tab
     *
     * @param position checked position
     */
    private void setChecked(int position) {
        if (isClickTab) {
            isClickTab = false;
        } else {
            if (mTabLayout == null) {
                return;
            }
            mTabLayout.setTabSelected(index);
        }
        index = position;
    }

    private void smoothScrollToPosition(int currentPosition) {
        int firstPosition = mManager.findFirstVisibleItemPosition();
        int lastPosition = mManager.findLastVisibleItemPosition();
        if (currentPosition <= firstPosition) {
            mRecyclerView.smoothScrollToPosition(currentPosition);
        } else if (currentPosition <= lastPosition) {
            int top = mRecyclerView.getChildAt(currentPosition - firstPosition).getTop();
            mRecyclerView.smoothScrollBy(0, top);
        } else {
            mRecyclerView.smoothScrollToPosition(currentPosition);
            needScroll = true;
        }
    }

    @SuppressWarnings("unchecked")
    private SuperAdapter getSuperAdapter() {
        return new SuperAdapter<LinkedTreeMap<String, Object>>(mBActivity, mList, R.layout.item_navigation) {
            @Override
            public void onBind(SuperViewHolder h, int viewType, int layoutPosition, LinkedTreeMap<String, Object> map) {
                h.setText(R.id.tvNavigationTitle, JU.s(map, "name"));
                TagFlowLayout tagFlowLayout = h.findViewById(R.id.tflNavigation);
                List<LinkedTreeMap<String, Object>> list = JU.al(map, "articles");
                tagFlowLayout.setAdapter(new TagAdapter<LinkedTreeMap<String, Object>>(list) {
                    @Override
                    public View getView(FlowLayout parent, int position, LinkedTreeMap<String, Object> map2) {
                        TextView tv = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout
                                .flow_layout_tv, tagFlowLayout, false);
                        tv.setPadding(ConvertUtils.dp2px(10), ConvertUtils.dp2px(6), ConvertUtils.dp2px(10),
                                ConvertUtils.dp2px(6));
                        tv.setText(JU.s(map2, "title"));
                        tv.setTextColor(CU.randomColor());
                        return tv;
                    }
                });
                tagFlowLayout.setOnTagClickListener((view, position1, parent1) -> {
                    ActivityOptions options = ActivityOptions.makeScaleUpAnimation(view, view.getWidth() / 2,
                            view.getHeight() / 2, 0, 0);
                    Intent i = new Intent(mBActivity, WebViewActivity.class)
                            .putExtra(WebViewActivity.TYPE, WebViewActivity.TYPE_LOAD_URL)
                            .putExtra("title", JU.s(list.get(position1), "title"))
                            .putExtra("url", JU.s(list.get(position1), "link"));
                    if (options != null && !Build.MANUFACTURER.contains("samsung") && Build.VERSION.SDK_INT
                            >= Build.VERSION_CODES.M) {
                        ActivityUtils.startActivity(i, options.toBundle());
                    } else ActivityUtils.startActivity(i);
                    return true;
                });
            }
        };
    }


    private void getNetData() {
        BaseActivity.doCommonGet("navi/json", null, new ProgressObserver<DataClass>(this, true) {
            @Override
            public void onNext(DataClass dc) {
                mList.addAll(JU.al(dc.object, "data"));
                mSuperAdapter.notifyDataSetChanged();

                mTabLayout.setTabAdapter(new TabAdapter() {
                    @Override
                    public int getCount() {
                        return mList == null ? 0 : mList.size();
                    }

                    @Override
                    public ITabView.TabBadge getBadge(int i) {
                        return null;
                    }

                    @Override
                    public ITabView.TabIcon getIcon(int i) {
                        return null;
                    }

                    @Override
                    public ITabView.TabTitle getTitle(int i) {
                        return new TabView.TabTitle.Builder()
                                .setContent(JU.s(mList.get(i), "name"))
                                .setTextColor(ContextCompat.getColor(mBActivity, R.color.shallow_green),
                                        ContextCompat.getColor(mBActivity, R.color.shallow_grey))
                                .build();
                    }

                    @Override
                    public int getBackground(int i) {
                        return -1;
                    }
                });

                leftRightLinkage();
            }
        });
    }
}

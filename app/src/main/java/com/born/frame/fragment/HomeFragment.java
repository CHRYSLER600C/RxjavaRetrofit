package com.born.frame.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.born.frame.R;
import com.born.frame.activity.GroupActivity;
import com.born.frame.adapter.ViewPagerAdapter;
import com.born.frame.common.CommonData;
import com.born.frame.dataclass.HomepgAdvDataClass;
import com.born.frame.dataclass.HomepgAdvDataClass.HomepgAdvInfo;
import com.born.frame.httputils.ImageLoaderUtil;
import com.born.frame.subscribers.ProgressSubscriber;
import com.born.frame.utils.DeviceUtil;
import com.born.frame.utils.JudgeUtil;
import com.born.frame.utils.LogicUtil;
import com.born.frame.view.xlistview.XListView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 首页
 */
public class HomeFragment extends BaseTitleFragment implements View.OnClickListener {

    @Bind(R.id.xListView)
    XListView mXListView;

    HeaderView mHeaderView;

    private boolean mIsLoadingMore = false;

    // banner
    private List<View> mHomeAdvPics;
    private ViewPagerAdapter mHomeBannerAdapter;
    private List<ImageView> mIndicatorList;
    private GroupActivity mParent;
    private AtomicInteger mWhat = new AtomicInteger(0);


    @Override
    protected View setContentView(Bundle savedInstanceState) {
        mParent = (GroupActivity) getActivity();
        return View.inflate(mParent, R.layout.activity_common_xlv, null);
    }

    @Override
    protected void initControl() {
        setLeftVisible(View.GONE);
        setTitle("首页");
        setRightTextContent("登录");
        getRightContainer().setOnClickListener(this);

        View headerView = View.inflate(mParent, R.layout.fragment_home_header, null);
        mHeaderView = new HeaderView(headerView);
        addHeader2ListView(headerView);
        initBannerPager();
    }

    @SuppressLint("SimpleDateFormat")
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
                getBannderImages();
            }

            @Override
            public void onLoadMore() {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (CommonData.IS_LOGIN) {
            setRightTextContent("已登录");
            getRightContainer().setEnabled(false);
        } else {
            setRightTextContent("登录");
            getRightContainer().setEnabled(true);
        }
        if (JudgeUtil.isEmpty(mHomeAdvPics)) {
            getBannderImages();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llRightContainer:
                LogicUtil.loginIntent(mParent);
//                startActivity(new Intent(mParent, WebViewActivity.class).putExtra("type", "type_load_url"));
                break;
        }
    }

    private void initBannerPager() {
        mIndicatorList = new ArrayList<>();
        mHomeAdvPics = new ArrayList<>();
        mHomeBannerAdapter = new ViewPagerAdapter(mHomeAdvPics);
        mHeaderView.homePagerAdv.setAdapter(mHomeBannerAdapter);
        mHeaderView.homePagerAdv.setOnPageChangeListener(new GuidePageChangeListener());
        DeviceUtil.setScrollerSpeed(mParent, 500, mHeaderView.homePagerAdv);

        Observable.interval(5, 5, TimeUnit.SECONDS)
                .compose(this.<Long>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        mWhat.incrementAndGet();
                        if (mWhat.get() > mHomeAdvPics.size() - 1) {
                            mWhat.getAndSet(0);
                        }
                        if (mWhat.get() >= 0 && mWhat.get() < mHomeAdvPics.size()) {
                            mHeaderView.homePagerAdv.setCurrentItem(mWhat.get());
                        }
                    }
                });
    }

    private ImageView getImageView(final HomepgAdvInfo advInfo) {
        ImageView iv = new ImageView(getActivity());
        iv.setScaleType(ScaleType.CENTER_CROP);
        iv.setImageResource(R.drawable.ic_default);
        iv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!TextUtils.isEmpty(advInfo.linkUrl)) {

                }
            }
        });

        // 加载网络图片
        if (!TextUtils.isEmpty(advInfo.imgUrl)) {
            ImageLoaderUtil.downloadImage(mParent, advInfo.imgUrl, iv, R.drawable.ic_default);
        }
        return iv;
    }

    private void refreshPager(List<HomepgAdvInfo> mAdList) {
        mHeaderView.homeIndicatorGroup.removeAllViews();
        mIndicatorList.clear();
        mHomeAdvPics.clear();
        if (mAdList != null && mAdList.size() > 0) {
            mHeaderView.homePagerAdv.setBackgroundColor(getResources().getColor(R.color.color_6));
            for (int i = 0; i < mAdList.size(); i++) {
                mHomeAdvPics.add(getImageView(mAdList.get(i)));
            }
            for (int i = 0; i < mHomeAdvPics.size(); i++) {
                ImageView iv = new ImageView(getActivity());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
                params.setMargins(5, 0, 5, 0);
                iv.setLayoutParams(params/* new LayoutParams(20, 20) */);
                iv.setPadding(5, 5, 5, 5);
                mIndicatorList.add(iv);
                if (i == mWhat.get()) {
                    mIndicatorList.get(i).setBackgroundResource(R.drawable.shape_oval_red);
                } else {
                    mIndicatorList.get(i).setBackgroundResource(R.drawable.shape_oval_white);
                }
                mHeaderView.homeIndicatorGroup.addView(mIndicatorList.get(i));
            }
        } else {
            mHeaderView.homePagerAdv.setBackgroundResource(R.drawable.ic_default);
        }
        mHomeBannerAdapter.notifyDataSetChanged();
    }

    private final class GuidePageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            mWhat.getAndSet(arg0);
            for (int i = 0; i < mIndicatorList.size(); i++) {
                mIndicatorList.get(arg0).setBackgroundResource(R.drawable.shape_oval_red);
                if (arg0 != i) {
                    mIndicatorList.get(i).setBackgroundResource(R.drawable.shape_oval_white);
                }
            }
        }
    }

    class HeaderView {
        @Bind(R.id.homePagerAdv)
        ViewPager homePagerAdv;
        @Bind(R.id.homeIndicatorGroup)
        LinearLayout homeIndicatorGroup;

        public HeaderView(View view) {
            ButterKnife.bind(this, view);
        }
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

    private void getBannderImages() {
        mIsLoadingMore = true;
        mParent.doRequestImpl("getBannerImages", new ProgressSubscriber<HomepgAdvDataClass>(mParent, false) {
            @Override
            public void onNext(HomepgAdvDataClass dataClass) {
                mIsLoadingMore = false;
                mXListView.stopRefresh();
                if (JudgeUtil.isNotEmpty(dataClass.imgInfo)) {
                    refreshPager(dataClass.imgInfo);
                }
            }
        });
    }

}

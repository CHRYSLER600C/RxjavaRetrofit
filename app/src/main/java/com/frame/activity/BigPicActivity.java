package com.frame.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.RegexUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.frame.R;
import com.frame.adapter.ViewPagerAdapter;
import com.frame.view.MyViewPager;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

public class BigPicActivity extends BaseActivity {

    private MyViewPager mVpBigPic;
    private List<View> mViews = new ArrayList<>();
    private List<String> mPicUrls = new ArrayList<>();
    private List<Integer> mPicIds = new ArrayList<>();
    private ViewPagerAdapter mViewPagerAdapter;
    private int mIndexStart = 0;
    private TextView mTvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_pic);

        getIntentParams();
        initControls();
    }

    protected void getIntentParams() {
        mIndexStart = getIntent().getIntExtra("index", 0);
        mPicUrls = getIntent().getStringArrayListExtra("picUrls");
        mPicIds = getIntent().getIntegerArrayListExtra("picIds");
        mTvTitle = findViewById(R.id.tvTitleContent);
        if (mPicUrls != null) {
            mTvTitle.setText((mIndexStart + 1) + "/" + mPicUrls.size());
        } else if (mPicIds != null) {
            mTvTitle.setText((mIndexStart + 1) + "/" + mPicIds.size());
        }
    }

    private void initControls() {
        findViewById(R.id.ivTitleLeft).setOnClickListener(v -> finish());
        mVpBigPic = findViewById(R.id.myViewPager);

        if (mPicUrls != null) {
            for (int i = 0; i < mPicUrls.size(); i++) {
                View vParent = View.inflate(this, R.layout.big_pic_loading, null);
                loadUrlImage(vParent, i);
                mViews.add(vParent);
            }
        } else if (mPicIds != null) {
            for (int i = 0; i < mPicIds.size(); i++) {
                View rv = View.inflate(this, R.layout.big_pic_loading, null);
                ImageView iv = rv.findViewById(R.id.ivBigPicLoading);
                iv.setImageResource(mPicIds.get(i));
                rv.findViewById(R.id.pbBigPicLoading).setVisibility(View.GONE);
                mViews.add(rv);
            }
        }

        mViewPagerAdapter = new ViewPagerAdapter(mViews);
        mVpBigPic.setAdapter(mViewPagerAdapter);
        mVpBigPic.addOnPageChangeListener(new GuidePageChangeListener());
        mVpBigPic.setCurrentItem(mIndexStart);
    }

    private void loadUrlImage(View vParent, int position) {// 加载网络图片
        String picUrl = mPicUrls.get(position);
        if (RegexUtils.isURL(picUrl)) {
            Glide.with(mBActivity).load(picUrl).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource,
                                            @Nullable Transition<? super Drawable> transition) {
                    ImageView ivCompanyPic = vParent.findViewById(R.id.ivBigPicLoading);
                    vParent.findViewById(R.id.pbBigPicLoading).setVisibility(View.GONE);
                    ivCompanyPic.setImageDrawable(resource);
                }
            });
        }

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
            if (mPicUrls != null) {
                mTvTitle.setText((arg0 + 1) + "/" + mPicUrls.size());
                loadUrlImage(mViews.get(arg0), arg0);
            } else if (mPicIds != null) {
                mTvTitle.setText((arg0 + 1) + "/" + mPicIds.size());
            }
        }
    }
}

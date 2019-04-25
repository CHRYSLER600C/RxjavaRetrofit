package com.frame.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blankj.utilcode.util.ObjectUtils;
import com.frame.R;
import com.frame.adapter.ViewPagerAdapter;
import com.frame.httputils.OkHttpUtil2;
import com.frame.view.MyViewPager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
		mTvTitle = (TextView) findViewById(R.id.tvTitleContent);
		if (mPicUrls != null) {
			mTvTitle.setText((mIndexStart + 1) + "/" + mPicUrls.size());
		} else if (mPicIds != null) {
			mTvTitle.setText((mIndexStart + 1) + "/" + mPicIds.size());
		}
	}

	private void initControls() {
		findViewById(R.id.ivTitleLeft).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mVpBigPic = (MyViewPager) findViewById(R.id.myViewPager);

		if (mPicUrls != null) {
			for (int i = 0; i < mPicUrls.size(); i++) {
				mViews.add(View.inflate(this, R.layout.big_pic_loading, null));
			}
		} else if (mPicIds != null) {
			for (int i = 0; i < mPicIds.size(); i++) {
				View rv = View.inflate(this, R.layout.big_pic_loading, null);
				ImageView iv = (ImageView) rv.findViewById(R.id.ivBigPicLoading);
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

	private void loadUrlImage(View v, int position) {// 加载网络图片
		if(ObjectUtils.isNotEmpty(v.getTag())) return;
		v.setTag("loaded");
		final ImageView ivCompanyPic = (ImageView) v.findViewById(R.id.ivBigPicLoading);
		final ProgressBar pb = (ProgressBar) v.findViewById(R.id.pbBigPicLoading);

		String picUrl = mPicUrls.get(position);
		if (!TextUtils.isEmpty(picUrl)) {
			this.downLoadImage(picUrl, new OkHttpUtil2.IRequestCallback() {
				@Override
				public <T> void ObjResponse(Boolean isSuccess, T responseObj, IOException e) {
					pb.setVisibility(View.GONE);
					ivCompanyPic.setImageBitmap((Bitmap)responseObj);
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

package com.frame.activity;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.ObjectUtils;
import com.flyco.tablayout.SlidingTabLayout;
import com.frame.R;
import com.frame.common.CommonData;
import com.frame.dataclass.DataClass;
import com.frame.fragment.BaseTitleFragment;
import com.frame.fragment.WxArticleDetailFragment;
import com.frame.observers.ProgressObserver;
import com.frame.utils.CU;
import com.frame.utils.JU;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 */
public class WxArticleActivity extends BaseTitleActivity {

    @BindView(R.id.stlCommon)
    SlidingTabLayout mTabLayout;
    @BindView(R.id.vpCommon)
    ViewPager mViewPager;

    private List<BaseTitleFragment> mFragments = new ArrayList<>();
    HashMap<String, Object> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablayout_viewpager);
        initControl();
    }

    protected void initControl() {
        map = CU.cast(getIntent().getSerializableExtra(CommonData.PARAM1));
        if (ObjectUtils.isNotEmpty(JU.s(map, "name"))) setTitleText(JU.s(map, "name"));
        else setTitleText("公众号");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ObjectUtils.isEmpty(mFragments)) {
            ArrayList<AbstractMap<String, Object>> list = JU.al(map, "children");
            if(ObjectUtils.isNotEmpty(list))initTabLayoutAndViewPager(list);
            else getWxArticleTitleData();
        }
    }

    private void initTabLayoutAndViewPager(ArrayList<AbstractMap<String, Object>> list) {
        mFragments.clear();
        for (AbstractMap<String, Object> map : list) {
            mFragments.add(WxArticleDetailFragment.getInstance(map.get("id").toString(), map.get("name").toString()));
        }
        mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments == null ? 0 : mFragments.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return list.get(position).get("name").toString();
            }
        });
        mTabLayout.setViewPager(mViewPager);
    }

    private void getWxArticleTitleData() {
        BaseActivity.doCommonGet("wxarticle/chapters/json", null, new ProgressObserver<DataClass>(this, true) {
            @Override
            public void onNext(DataClass dc) {
                initTabLayoutAndViewPager(JU.al(dc.object, "data"));
            }
        });
    }
}

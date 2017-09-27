package com.born.frame.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.born.frame.R;
import com.born.frame.common.CommonData;
import com.born.frame.fragment.Tab2Fragment;
import com.born.frame.fragment.Tab3Fragment;
import com.born.frame.fragment.HomeFragment;
import com.born.frame.fragment.UserCenterFragment;
import com.born.frame.utils.LogicUtil;

import butterknife.Bind;

public class GroupActivity extends BaseActivity {

    public static final String TAG = GroupActivity.class.getSimpleName();

    public static final String TAB1 = "首页";
    public static final String TAB2 = "货源";
    public static final String TAB3 = "车源";
    public static final String TAB4 = "我的";

    @Bind(android.R.id.tabhost)
    FragmentTabHost mTabhost;

    private long mExitTime = 0;
    private String mCurrentTag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        initControl();
    }

    @Override
    protected void onStart() {
        if (TAB4.equals(mCurrentTag)) {
            switchTab(CommonData.IS_LOGIN ? TAB4 : TAB1);
        }
        super.onStart();
    }

    private void initControl() {
        mTabhost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabhost.addTab(buildTabSpec(TAB1, R.drawable.selector_group_tab1), HomeFragment.class, null);
        mTabhost.addTab(buildTabSpec(TAB2, R.drawable.selector_group_tab2), Tab2Fragment.class, null);
        mTabhost.addTab(buildTabSpec(TAB3, R.drawable.selector_group_tab3), Tab3Fragment.class, null);
        mTabhost.addTab(buildTabSpec(TAB4, R.drawable.selector_group_tab4), UserCenterFragment.class, null);
        mTabhost.getTabWidget().setDividerDrawable(null); // 去掉底部竖线

        // 先判断登录
//		mTabhost.getTabWidget().getChildAt(3).setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (!CommonData.IS_LOGIN) {
//					mCurrentTag = TAB4;
//					LogicUtil.loginIntent(GroupActivity.this);
//				} else {
//					switchTab(TAB4);
//				}
//			}
//		});
    }

    private TabSpec buildTabSpec(String tag, int icon) {
        View view = View.inflate(this, R.layout.item_group_tab, null);
        ((ImageView) view.findViewById(R.id.iv_tab_icon)).setImageResource(icon);
        ((TextView) view.findViewById(R.id.tv_tab_text)).setText(tag);
        return mTabhost.newTabSpec(tag).setIndicator(view);
    }

    public void switchTab(String tag) {
        mTabhost.setCurrentTabByTag(tag);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (System.currentTimeMillis() - mExitTime < 2000) {
                        LogicUtil.logout();
                        finish();
                    } else {
                        showToast("再点一次将退出程序！");
                        mExitTime = System.currentTimeMillis();
                    }
                    return true;
                }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}

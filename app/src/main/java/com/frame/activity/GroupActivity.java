package com.frame.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.blankj.utilcode.util.PermissionUtils;
import com.frame.R;
import com.frame.common.CommonData;
import com.frame.fragment.Tab1Fragment;
import com.frame.fragment.Tab2Fragment;
import com.frame.fragment.Tab3Fragment;
import com.frame.fragment.Tab4Fragment;
import com.frame.utils.LU;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTabHost;
import butterknife.BindView;

public class GroupActivity extends BaseActivity {

    public static final String TAG = GroupActivity.class.getSimpleName();

    public static final String TAB1 = "首页";
    public static final String TAB2 = "货源";
    public static final String TAB3 = "车源";
    public static final String TAB4 = "我的";

    @BindView(android.R.id.tabhost)
    FragmentTabHost mTabHost;

    private long mExitTime = 0;
    private String mCurrentTag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        initControl();

        //申请多个权限
        rxPermissionsRequest(isGranted -> {
            if (PermissionUtils.isGranted(Manifest.permission.READ_PHONE_STATE)) {
//                CommonData.IMEI = ObjectUtils.getOrDefault(PhoneUtils.getIMEI(), "");
            }
        }, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager
                .PERMISSION_GRANTED) {
        }
    }

    @Override
    protected void onStart() {
        if (mCurrentTag.equals(TAB4)) switchTab(CommonData.IS_LOGIN ? TAB4 : TAB1);
        super.onStart();
    }

    private void initControl() {
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.addTab(buildTabSpec(TAB1, R.drawable.selector_group_tab1), Tab1Fragment.class, null);
        mTabHost.addTab(buildTabSpec(TAB2, R.drawable.selector_group_tab2), Tab2Fragment.class, null);
        mTabHost.addTab(buildTabSpec(TAB3, R.drawable.selector_group_tab3), Tab3Fragment.class, null);
        mTabHost.addTab(buildTabSpec(TAB4, R.drawable.selector_group_tab4), Tab4Fragment.class, null);
        mTabHost.getTabWidget().setDividerDrawable(null); // 去掉底部竖线

        // 先判断登录
//		mTabHost.getTabWidget().getChildAt(3).setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (!CommonData.IS_LOGIN) {
//					mCurrentTag = TAB4;
//					LU.gotoLogin(GroupActivity.this);
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
        return mTabHost.newTabSpec(tag).setIndicator(view);
    }

    public void switchTab(String tag) {
        mTabHost.setCurrentTabByTag(tag);
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mExitTime < 2000) {
            LU.logout();
            super.onBackPressed();
        } else {
            showToast("再点一次将退出程序！");
            mExitTime = System.currentTimeMillis();
        }
    }
}

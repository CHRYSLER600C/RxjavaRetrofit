package com.frame.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.blankj.utilcode.util.ActivityUtils;
import com.frame.R;
import com.frame.common.CommonData;
import com.frame.common.SPreferences;
import com.frame.dataclass.DataClass;
import com.frame.observers.ProgressObserver;
import com.frame.utils.CommonUtil;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 登录界面
 */
public class LoginActivity extends BaseTitleActivity {

    @BindView(R.id.etUserName)
    EditText mEtUserName;
    @BindView(R.id.etPwd)
    EditText mEtPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initControl();
    }

    private void initControl() {
        setTitleText("登录");
        setTitleBgColor(getResources().getColor(R.color.color_6));

        CommonData.IS_LOGIN = false;
//        CommonUtil.setTextViewDrawableLeft(mEtUserName, R.drawable.ic_registration_user, 18, 20, 10);
//        CommonUtil.setTextViewDrawableLeft(mEtPwd, R.drawable.ic_registration_pwd, 18, 22, 10);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String userName = SPreferences.getData(SPreferences.USER_NAME, "");
        if (!TextUtils.isEmpty(userName)) {
            mEtUserName.setText(userName);
        }
    }

    @OnClick({R.id.tvLogin, R.id.tvForgetPwd, R.id.tvRegister})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvLogin:
                String name = mEtUserName.getText().toString().trim();
                String pwd = mEtPwd.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    showToast("请输入您的用户名");
                    return;
                }
                if (TextUtils.isEmpty(pwd)) {
                    showToast("请输入登录密码");
                    return;
                }
                doLoginRequest(name, pwd);
                break;
            case R.id.tvForgetPwd:
                break;
            case R.id.tvRegister:
                ActivityUtils.startActivity(new Intent(this, RegistrationActivity.class));
                break;
        }
    }

    private void doLoginRequest(final String name, String pwd) {
        Map<String, Object> map = new HashMap<>();
        map.put("userName", URLEncoder.encode(name));
        map.put("passWord", CommonUtil.encodePwd(pwd));
        doCommonGetImpl("login", map, new ProgressObserver<DataClass>(this, true) {
            @Override
            public void onNext(DataClass dc) {
                SPreferences.saveData(SPreferences.USER_NAME, name);
                CommonData.IS_LOGIN = true;
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
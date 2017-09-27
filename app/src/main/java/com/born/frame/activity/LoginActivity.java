package com.born.frame.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.born.frame.R;
import com.born.frame.common.CommonData;
import com.born.frame.common.SPreferences;
import com.born.frame.dataclass.DataClass;
import com.born.frame.dataclass.bean.NameValue;
import com.born.frame.subscribers.ProgressSubscriber;
import com.born.frame.subscribers.RxBus;
import com.born.frame.utils.ImageUtil;
import com.born.frame.utils.SecurityUtil;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 登录界面
 */
public class LoginActivity extends BaseTitleActivity {

    @Bind(R.id.etUserName)
    EditText mEtUserName;
    @Bind(R.id.etPwd)
    EditText mEtPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initControl();
    }

    private void initControl() {
        setLeftBackClick();
        setTitle("登录");
        setTitleBgColor(getResources().getColor(R.color.color_6));

        CommonData.IS_LOGIN = false;
        ImageUtil.setTextViewDrawableLeft(mEtUserName, R.drawable.ic_registration_user, 18, 20, 10);
        ImageUtil.setTextViewDrawableLeft(mEtPwd, R.drawable.ic_registration_pwd, 18, 22, 10);

        RxBus.getInstance().toObservable(NameValue.class)
                .compose(mContext.<NameValue>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<NameValue>() {
                    @Override
                    public void call(NameValue nv) {
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String userName = (String) SPreferences.getData(this, "", SPreferences.USER_NAME);
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
                Intent registerIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(registerIntent);
                break;
        }
    }

    private void doLoginRequest(final String name, String pwd) {
        Map<String, Object> map = new HashMap<>();
        map.put("userName", URLEncoder.encode(name));
        map.put("passWord", SecurityUtil.encodePwd(pwd));
        doCommonGetImpl("login", map, new ProgressSubscriber<DataClass>(this, true) {
            @Override
            public void onNext(DataClass dc) {
                showToast(dc.message);
                SPreferences.saveData(LoginActivity.this, name, SPreferences.USER_NAME);
                CommonData.IS_LOGIN = true;
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
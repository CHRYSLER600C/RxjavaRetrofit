package com.frame.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.blankj.utilcode.util.ObjectUtils;
import com.frame.R;
import com.frame.common.CommonData;
import com.frame.dataclass.DataClass;
import com.frame.httputils.OkHttpUtil2;
import com.frame.httputils.RequestBuilder;
import com.frame.observers.ProgressObserver;
import com.frame.observers.RecycleObserver;
import com.frame.utils.CommonUtil;
import com.frame.utils.ViewUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * 注册界面
 */
public class RegistrationActivity extends BaseTitleActivity implements OnCheckedChangeListener {

    @BindView(R.id.etRegUserName)
    EditText mEtRegUserName;
    @BindView(R.id.etRegPwd)
    EditText mEtRegPwd;
    @BindView(R.id.cbRegPwdEye)
    CheckBox mCbRegPwdEye;

    @BindView(R.id.etRegImgCode)
    EditText mEtRegImgCode;
    @BindView(R.id.ivRegCodeImgView)
    ImageView mIvRegCodeImgView;

    @BindView(R.id.etRegMobile)
    EditText mEtRegMobile;
    @BindView(R.id.registrationSmsCode)
    EditText mRegistrationSmsCode;
    @BindView(R.id.btnRegSmsCode)
    Button mBtnRegSmsCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initControl();
        getImgCode();
    }

    private void initControl() {
        setTitleText("注册");
        mCbRegPwdEye.setOnCheckedChangeListener(this);

//        CommonUtil.setTextViewDrawableLeft(mEtRegUserName, R.drawable.ic_registration_user, 18, 20, 10);
//        CommonUtil.setTextViewDrawableLeft(mEtRegPwd, R.drawable.ic_registration_pwd, 18, 20, 10);
//        CommonUtil.setTextViewDrawableLeft(mEtRegImgCode, R.drawable.ic_registration_img_code, 18, 20, 10);
//        CommonUtil.setTextViewDrawableLeft(mEtRegMobile, R.drawable.ic_registration_mobile, 18, 20, 10);
//        CommonUtil.setTextViewDrawableLeft(mRegistrationSmsCode, R.drawable.ic_registration_sms_code, 18, 20, 10);
    }


    @OnClick({R.id.ivRegCodeImgView, R.id.btnRegSmsCode, R.id.btnRegistration})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivRegCodeImgView:
                getImgCode();
                break;
            case R.id.btnRegSmsCode:
                String mobileSms = ViewUtil.getEtTrim(mEtRegMobile);
                String imgCodeSms = ViewUtil.getEtTrim(mEtRegImgCode);

                if (TextUtils.isEmpty(mobileSms) || mobileSms.length() < 11) {
                    showToast("请输入11位手机号码");
                    return;
                }
                if (TextUtils.isEmpty(imgCodeSms)) {
                    showToast("请输入图中字符");
                    return;
                }
                verifyImgCode(imgCodeSms);
                break;
            case R.id.btnRegistration:
                String name = ViewUtil.getEtTrim(mEtRegUserName);
                String pwd = ViewUtil.getEtTrim(mEtRegPwd);
                String imgCode = ViewUtil.getEtTrim(mEtRegImgCode);
                String mobile = ViewUtil.getEtTrim(mEtRegMobile);
                String smsCode = ViewUtil.getEtTrim(mRegistrationSmsCode);
                if (TextUtils.isEmpty(name) || name.length() < 6 || name.length() > 20) {
                    showToast("请输入6-20位用户名");
                    return;
                }
                if (TextUtils.isEmpty(pwd) || pwd.length() < 6 || pwd.length() > 14) {
                    showToast("请输入6-14位密码");
                    return;
                }

                if (TextUtils.isEmpty(imgCode)) {
                    showToast("请输入图中字符");
                    return;
                }
                if (TextUtils.isEmpty(mobile) || mobile.length() < 11) {
                    showToast("请输入11位手机号码");
                    return;
                }
                if (TextUtils.isEmpty(smsCode)) {
                    showToast("请输入短信验证码");
                    return;
                }

                Map<String, Object> map = new HashMap<>();
                map.put("name", name);
                map.put("pwd", CommonUtil.encodePwd(pwd));
                map.put("mobile", mobile);
                map.put("smsCode", smsCode);
                doRegistrationRequest(map);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.equals(mCbRegPwdEye)) {
            mEtRegPwd.setInputType(isChecked ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

    private void doRegistrationRequest(Map<String, Object> map) {
        doCommonGetImpl("registerGRSubmit", map, new ProgressObserver<DataClass>(this, true) {
            @Override
            public void onNext(DataClass dc) {
//                showToast(dc.message);
                finish();
            }
        });
    }

    private void verifyImgCode(String imgCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("imgCode", imgCode);
        doCommonGetImpl("verifyImgCode", map, new ProgressObserver<DataClass>(this, true) {
            @Override
            public void onNext(DataClass dc) {
                sendSmsCode();
            }
        });
    }

    private void sendSmsCode() {

        Map<String, Object> map = new HashMap<>();
        map.put("mobile", ViewUtil.getEtTrim(mEtRegMobile));
        map.put("business", "register");
        String imgCode = ViewUtil.getEtTrim(mEtRegImgCode);
        if (ObjectUtils.isNotEmpty(imgCode)) {
            map.put("imgCode", imgCode);
        }
        doCommonGetImpl("sendSmsCode", map, new ProgressObserver<DataClass>(this, true) {
            @Override
            public void onNext(DataClass dc) {
                mBtnRegSmsCode.setClickable(false);
                add2Disposable(Observable.interval(0, 1, TimeUnit.SECONDS, AndroidSchedulers.mainThread()) // 倒计时
                        .take(61)
                        .subscribeWith(new RecycleObserver<Long>() {
                            @Override
                            public void onNext(Long aLong) {
                                ViewUtil.setViewText(mBtnRegSmsCode, String.format("%02d秒", 60 - aLong));
                            }

                            @Override
                            public void onComplete() {
                                super.onComplete();
                                mBtnRegSmsCode.setClickable(true);
                                ViewUtil.setViewText(mBtnRegSmsCode, "获取验证码");
                            }
                        }));
            }
        });
    }

    private void getImgCode() {
        RequestBuilder.RequestObject request = new RequestBuilder.RequestObject();
        request.method = "getImgCode";
        downLoadImage(RequestBuilder.build(request), new OkHttpUtil2.IRequestCallback() {
            @Override
            public <T> void ObjResponse(Boolean isSuccess, T responseObj, IOException e) {
                if (isSuccess) {
                    if (ObjectUtils.isNotEmpty(responseObj)) {
                        mIvRegCodeImgView.setImageBitmap((Bitmap) responseObj);
                    } else {
                        showToast(CommonData.NETWORK_ERROR_MSG);
                    }
                } else {
                    showToast(CommonData.NETWORK_ERROR_MSG);
                }
            }
        });
    }
}

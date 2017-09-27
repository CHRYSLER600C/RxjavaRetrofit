package com.born.frame.activity;

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

import com.born.frame.R;
import com.born.frame.common.CommonData;
import com.born.frame.dataclass.DataClass;
import com.born.frame.httputils.OkHttpUtil2.IRequestBitmapCallback;
import com.born.frame.httputils.RequestBuilder;
import com.born.frame.subscribers.ProgressSubscriber;
import com.born.frame.utils.ImageUtil;
import com.born.frame.utils.JudgeUtil;
import com.born.frame.utils.SecurityUtil;
import com.born.frame.utils.ViewUtil;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * 注册界面
 */
public class RegistrationActivity extends BaseTitleActivity implements OnCheckedChangeListener {

    @Bind(R.id.etRegUserName)
    EditText mEtRegUserName;
    @Bind(R.id.etRegPwd)
    EditText mEtRegPwd;
    @Bind(R.id.cbRegPwdEye)
    CheckBox mCbRegPwdEye;

    @Bind(R.id.etRegImgCode)
    EditText mEtRegImgCode;
    @Bind(R.id.ivRegCodeImgView)
    ImageView mIvRegCodeImgView;

    @Bind(R.id.etRegMobile)
    EditText mEtRegMobile;
    @Bind(R.id.registrationSmsCode)
    EditText mRegistrationSmsCode;
    @Bind(R.id.btnRegSmsCode)
    Button mBtnRegSmsCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initControl();
        getImgCode();
    }

    private void initControl() {
        setTitle("注册");
        setLeftBackClick();
        mCbRegPwdEye.setOnCheckedChangeListener(this);

        ImageUtil.setTextViewDrawableLeft(mEtRegUserName, R.drawable.ic_registration_user, 18, 20, 10);
        ImageUtil.setTextViewDrawableLeft(mEtRegPwd, R.drawable.ic_registration_pwd, 18, 20, 10);
        ImageUtil.setTextViewDrawableLeft(mEtRegImgCode, R.drawable.ic_registration_img_code, 18, 20, 10);
        ImageUtil.setTextViewDrawableLeft(mEtRegMobile, R.drawable.ic_registration_mobile, 18, 20, 10);
        ImageUtil.setTextViewDrawableLeft(mRegistrationSmsCode, R.drawable.ic_registration_sms_code, 18, 20, 10);
    }


    @OnClick({R.id.ivRegCodeImgView, R.id.btnRegSmsCode, R.id.btnRegistration})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivRegCodeImgView:
                getImgCode();
                break;
            case R.id.btnRegSmsCode:
                String mobileSms = ViewUtil.getTextTrim(mEtRegMobile);
                String imgCodeSms = ViewUtil.getTextTrim(mEtRegImgCode);

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
                String name = ViewUtil.getTextTrim(mEtRegUserName);
                String pwd = ViewUtil.getTextTrim(mEtRegPwd);
                String imgCode = ViewUtil.getTextTrim(mEtRegImgCode);
                String mobile = ViewUtil.getTextTrim(mEtRegMobile);
                String smsCode = ViewUtil.getTextTrim(mRegistrationSmsCode);
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
                map.put("pwd", SecurityUtil.encodePwd(pwd));
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
        doCommonGetImpl("registerGRSubmit", map, new ProgressSubscriber<DataClass>(this, true) {
            @Override
            public void onNext(DataClass dc) {
                showToast(dc.message);
                finish();
            }
        });
    }

    private void verifyImgCode(String imgCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("imgCode", imgCode);
        doCommonGetImpl("verifyImgCode", map, new ProgressSubscriber<DataClass>(this, true) {
            @Override
            public void onNext(DataClass dc) {
                sendSmsCode();
            }
        });
    }

    private void sendSmsCode() {

        Map<String, Object> map = new HashMap<>();
        map.put("mobile", ViewUtil.getTextTrim(mEtRegMobile));
        map.put("business", "register");
        String imgCode = ViewUtil.getTextTrim(mEtRegImgCode);
        if (JudgeUtil.isNotEmpty(imgCode)) {
            map.put("imgCode", imgCode);
        }
        doCommonGetImpl("sendSmsCode", map, new ProgressSubscriber<DataClass>(this, true) {
            @Override
            public void onNext(DataClass dc) {
                RxView.clickable(mBtnRegSmsCode).call(false);
                Observable.interval(0, 1, TimeUnit.SECONDS, AndroidSchedulers.mainThread()) // 倒计时
                        .compose(mContext.<Long>bindToLifecycle())
                        .take(61)
                        .subscribe(new Subscriber<Long>() {
                            @Override
                            public void onCompleted() {
                                RxView.clickable(mBtnRegSmsCode).call(true);
                                RxTextView.text(mBtnRegSmsCode).call("获取验证码");
                            }

                            @Override
                            public void onError(Throwable e) {
                            }

                            @Override
                            public void onNext(Long aLong) {
                                RxTextView.text(mBtnRegSmsCode).call(String.format("%02d秒", 60 - aLong));
                            }
                        });
                showToast(dc.message);
            }
        });
    }

    private void getImgCode() {
        RequestBuilder.RequestObject requestObject = new RequestBuilder.RequestObject();
        requestObject.method = "getImgCode";
        downLoadImage(requestObject, new IRequestBitmapCallback() {
            @Override
            public void BitmapResponse(Boolean isSuccess, Bitmap responseBmp, IOException e) {
                if (isSuccess) {
                    if (JudgeUtil.isNotEmpty(responseBmp)) {
                        mIvRegCodeImgView.setImageBitmap(responseBmp);
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

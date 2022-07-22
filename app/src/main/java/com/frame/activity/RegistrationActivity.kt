package com.frame.activity

import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.view.View
import android.widget.CompoundButton
import com.blankj.utilcode.util.ObjectUtils
import com.frame.R
import com.frame.dataclass.DataClass
import com.frame.observers.ProgressObserver
import com.frame.observers.RecycleObserver
import com.frame.utils.CU
import com.frame.utils.LU
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_registration.*
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 注册界面
 */
class RegistrationActivity : BaseTitleActivity(), CompoundButton.OnCheckedChangeListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        initControl()
    }

    private fun initControl() {
        setTitleText("注册")
        cbRegPwdEye.setOnCheckedChangeListener(this)

//        CU.setTVDrawableLeft(etRegUserName, R.drawable.ic_registration_user, 18, 20, 10);
//        CU.setTVDrawableLeft(etRegPwd, R.drawable.ic_registration_pwd, 18, 20, 10);
//        CU.setTVDrawableLeft(etRegImgCode, R.drawable.ic_registration_img_code, 18, 20, 10);
//        CU.setTVDrawableLeft(etRegMobile, R.drawable.ic_registration_mobile, 18, 20, 10);
//        CU.setTVDrawableLeft(mRegistrationSmsCode, R.drawable.ic_registration_sms_code, 18, 20, 10);
    }

    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.btnRegSmsCode -> {
                val mobileSms = LU.getEtTrim(etRegMobile)
                val imgCodeSms = LU.getEtTrim(etRegImgCode)
                if (TextUtils.isEmpty(mobileSms) || mobileSms.length < 11) {
                    showShort("请输入11位手机号码")
                    return
                }
                if (TextUtils.isEmpty(imgCodeSms)) {
                    showShort("请输入图中字符")
                    return
                }
                verifyImgCode(imgCodeSms)
            }
            R.id.btnRegistration -> {
                val name = LU.getEtTrim(etRegUserName)
                val pwd = LU.getEtTrim(etRegPwd)
                val imgCode = LU.getEtTrim(etRegImgCode)
                val mobile = LU.getEtTrim(etRegMobile)
                val smsCode = LU.getEtTrim(registrationSmsCode)
                if (TextUtils.isEmpty(name) || name.length < 6 || name.length > 20) {
                    showShort("请输入6-20位用户名")
                    return
                }
                if (TextUtils.isEmpty(pwd) || pwd.length < 6 || pwd.length > 14) {
                    showShort("请输入6-14位密码")
                    return
                }
                if (TextUtils.isEmpty(imgCode)) {
                    showShort("请输入图中字符")
                    return
                }
                if (TextUtils.isEmpty(mobile) || mobile.length < 11) {
                    showShort("请输入11位手机号码")
                    return
                }
                if (TextUtils.isEmpty(smsCode)) {
                    showShort("请输入短信验证码")
                    return
                }
                val map: MutableMap<String?, Any?> = HashMap()
                map["name"] = name
                map["pwd"] = CU.encodePwd(pwd)
                map["mobile"] = mobile
                map["smsCode"] = smsCode
                doRegistrationRequest(map)
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (buttonView == cbRegPwdEye) {
            etRegPwd.inputType = if (isChecked) InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
    }

    private fun doRegistrationRequest(map: Map<String?, Any?>) {
        doCommonGet("registerGRSubmit", map, object : ProgressObserver<DataClass>(this, true) {
            override fun onNext(dc: DataClass) {
//                showShort(dc.message);
                finish()
            }
        })
    }

    private fun verifyImgCode(imgCode: String) {
        val map: MutableMap<String?, Any?> = HashMap()
        map["imgCode"] = imgCode
        doCommonGet("verifyImgCode", map, object : ProgressObserver<DataClass>(this, true) {
            override fun onNext(dc: DataClass) {
                sendSmsCode()
            }
        })
    }

    private fun sendSmsCode() {
        val map: MutableMap<String?, Any?> = HashMap()
        map["mobile"] = LU.getEtTrim(etRegMobile)
        map["business"] = "register"
        val imgCode = LU.getEtTrim(etRegImgCode)
        if (ObjectUtils.isNotEmpty(imgCode)) {
            map["imgCode"] = imgCode
        }
        doCommonGet("sendSmsCode", map, object : ProgressObserver<DataClass>(this, true) {
            override fun onNext(dc: DataClass) {
                countDown()
            }
        })
    }

    private fun countDown() {
        btnRegSmsCode.isClickable = false
        add2Disposable(Observable.interval(0, 1, TimeUnit.SECONDS, AndroidSchedulers.mainThread()) // 倒计时
            .take(61)
            .subscribeWith(object : RecycleObserver<Long?>() {
                override fun onNext(aLong: Long) {
                    btnRegSmsCode.text = String.format("%02d秒", 60 - aLong)
                }

                override fun onComplete() {
                    super.onComplete()
                    btnRegSmsCode.isClickable = true
                    btnRegSmsCode.text = "获取验证码"
                }
            }))
    }

}
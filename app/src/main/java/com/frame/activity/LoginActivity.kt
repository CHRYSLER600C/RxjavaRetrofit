package com.frame.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.frame.R
import com.frame.common.CommonData
import com.frame.common.SPreferences
import com.frame.dataclass.DataClass
import com.frame.observers.ProgressObserver
import com.frame.utils.CU
import kotlinx.android.synthetic.main.activity_login.*
import java.net.URLEncoder
import java.util.*

/**
 * 登录界面
 */
class LoginActivity : BaseTitleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initControl()
    }

    private fun initControl() {
        setTitleText("登录")
        setTitleBgColor(resources.getColor(R.color.color_6))
        CommonData.IS_LOGIN = false
//        CU.setTVDrawableLeft(etUserName, R.drawable.ic_registration_user, 18, 20, 10);
//        CU.setTVDrawableLeft(etPwd, R.drawable.ic_registration_pwd, 18, 22, 10);
    }

    override fun onResume() {
        super.onResume()
        val userName = SPreferences.getData(SPreferences.USER_NAME, "")
        if (!TextUtils.isEmpty(userName)) {
            etUserName?.setText(userName)
        }
    }

    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.tvLogin -> {
                val name = etUserName?.text.toString()
                val pwd = etPwd?.text.toString()
                if (TextUtils.isEmpty(name)) {
                    showShort("请输入您的用户名")
                    return
                }
                if (TextUtils.isEmpty(pwd)) {
                    showShort("请输入登录密码")
                    return
                }
                doLoginRequest(name, pwd)
            }
            R.id.tvForgetPwd -> {
            }
            R.id.tvRegister -> ActivityUtils.startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }

    private fun doLoginRequest(name: String, pwd: String) {
        val map: MutableMap<String?, Any?> = HashMap()
        map["userName"] = URLEncoder.encode(name)
        map["passWord"] = CU.encodePwd(pwd)
        doCommonGet("login", map, object : ProgressObserver<DataClass>(this, true) {
            override fun onNext(dc: DataClass) {
                SPreferences.saveData(SPreferences.USER_NAME, name)
                CommonData.IS_LOGIN = true
                setResult(RESULT_OK)
                finish()
            }
        })
    }
}
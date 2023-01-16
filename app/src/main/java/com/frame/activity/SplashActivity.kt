package com.frame.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.PermissionUtils
import com.frame.R
import com.frame.dataclass.DataClass
import com.frame.httputils.OkHttpUtil
import com.frame.observers.ProgressObserver
import com.frame.other.ICallBack
import com.frame.utils.CU
import com.frame.utils.JU
import com.frame.view.dialog.CommonDialog
import com.google.gson.internal.LinkedTreeMap
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_common_ll.*
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 启动页，此页还负责获取升级信息
 */
class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common_ll)
        llContainer?.setBackgroundResource(R.drawable.splash)

        //申请多个权限
        rxPermissionsRequest(ICallBack { isGranted: Any? ->
            if (PermissionUtils.isGranted(Manifest.permission.READ_PHONE_STATE)) {
//                CommonData.IMEI = ObjectUtils.getOrDefault(PhoneUtils.getIMEI(), "");
            }
            goToGroup() //pauseAndEnter();  //
        }, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun goToGroup() {
        ActivityUtils.startActivity(GroupActivity::class.java)
        finish()
    }

    private fun goToUpdate(updateUrl: String) {
        ActivityUtils.startActivity(Intent(this, UpdateActivity::class.java)
            .putExtra("updateUrl", updateUrl))
        finish()
    }

    private fun pauseAndEnter() { //zip操作符 避免网络请求很慢时还需要再等2秒钟
        val map: MutableMap<String, Any> = HashMap()
        map["verCode"] = AppUtils.getAppVersionCode()
        map["verName"] = AppUtils.getAppVersionName()
        map["channelCode"] = CU.getAppChanel()
        map["type"] = "ANDROID"
        val observableReq = OkHttpUtil.getInstance().mRequestService.get()
            .commonGet("https://www.yiqiyiqi.cn/app/appUpdateInfo.htm", map)?.subscribeOn(Schedulers.io())

        val observer = object : ProgressObserver<DataClass>(mBActivity, true) {
            override fun onNext(dc: DataClass) {
                val data = JU.m<LinkedTreeMap<String, Any>>(dc.obj, "updateInfo")
                val builder = CommonDialog.Builder(mBActivity, CommonDialog.DialogType.TYPE_SIMPLE)
                    .setTitle("有新版本")
                    .setMessage(JU.s(data, "updateInfo"))
                    .setCancelBtn("立即升级") { goToUpdate(JU.s(data, "updateUrl")) }

                when {
                    AppUtils.getAppVersionCode() < JU.d(data, "forceUpdateCode") -> { // 强制更新
                        val dialog = builder.create()
                        dialog.setOnCancelListener { finish() }
                        dialog.show()
                    }
                    AppUtils.getAppVersionCode() < JU.d(data, "optionalUpdateCode") -> { // 可选更新
                        val dialog = builder.setOkBtn("下次再说") { _: View?, _: String? -> goToGroup() }.create()
                        dialog.setCancelable(false)
                        dialog.show()
                    }
                    else -> pauseAndEnter()
                }
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                goToGroup()
            }
        }
        val observableTimer = Observable.timer(2000, TimeUnit.MILLISECONDS)
        add2Disposable(Observable.zip(observableReq, observableTimer, { dc: DataClass, _: Long -> dc })
            .doOnSubscribe { observer.showProgressDialogObserver() }
            .subscribeOn(AndroidSchedulers.mainThread()) // 指定doOnSubscribe运行在主线程
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer))
    }
}
package com.frame.activity

import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import com.frame.R
import com.frame.common.CommonData
import com.frame.dataclass.bean.Event
import com.frame.httputils.OkHttpUtil
import com.frame.httputils.OkHttpUtil2
import com.frame.httputils.OkHttpUtil2.IRequestCallback
import com.frame.httputils.OkHttpUtil2.IRequestFileCallback
import com.frame.httputils.RequestBuilder
import com.frame.httputils.RequestBuilder.RequestObject
import com.frame.observers.ProgressObserver
import com.frame.observers.RecycleObserver
import com.frame.observers.progress.ProgressDialogHandler
import com.frame.other.ICallBack
import com.frame.utils.CU
import com.frame.utils.gone
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.tbruyelle.rxpermissions2.RxPermissions
import com.zouxianbin.android.slide.SlideBackAppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.util.*


/**
 * activity基类，封装了数据请求，请求提示框和toast显示方法。
 */
open class BaseActivity : SlideBackAppCompatActivity() {

    lateinit var mBActivity: BaseActivity
    private var mProgressDialogHandler: ProgressDialogHandler? = null
    private var mCompositeDisposable: CompositeDisposable? = null
    protected var vStatusBar: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        isSlideable = isActivityCanSlideBack() //设置是否可以左滑返回，必须在super.onCreate（）之前
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        mBActivity = this
        if (regEvent()) EventBus.getDefault().register(this)
        initImmersionBar()
    }

    override fun setContentView(layoutResId: Int) {
        setContentView(LayoutInflater.from(this).inflate(layoutResId, null, false)) //滑动返回要求必须是setContentView(view)
    }

    override fun setContentView(view: View) {
        val root = View.inflate(this, R.layout.activity_base_title, null) as LinearLayout
        vStatusBar = root.findViewById(R.id.vStatusBar)
        if (getStatusBarBgColor() > 0) {
            vStatusBar?.layoutParams?.height = BarUtils.getStatusBarHeight()
            vStatusBar?.setBackgroundColor(resources.getColor(getStatusBarBgColor()))
        } else {
            vStatusBar?.gone() //返回0时隐藏
        }
        root.addView(view, ViewGroup.LayoutParams(-1, -1))
        super.setContentView(root)
        setShadowResource(R.drawable.shape_sliding_back_shadow) //设置Slide Back的阴影
    }

    /**
     * 初始化沉浸式状态栏，个性化请重载
     */
    protected open fun initImmersionBar() {
        CU.setImmersionBar(this, false)
    }


    open fun isActivityCanSlideBack(): Boolean {
        return true
    }

    open fun getStatusBarBgColor(): Int {
        return R.color.title_bg_color
    }

    fun rxPermissionsRequest(icb: ICallBack?, vararg permissions: String?) {
        if (PermissionUtils.isGranted(*permissions)) {
            icb?.dataCallback(true)
            return
        }
        add2Disposable(RxPermissions(this).request(*permissions).subscribeWith(object : RecycleObserver<Boolean?>() {
            override fun onNext(isGranted: Boolean) { // 权限申请结果回调
                icb?.dataCallback(isGranted)
            }
        }))
    }

    fun showShort(resText: Int) {
        ToastUtils.showShort(getString(resText))
    }

    fun showShort(text: String?) {
        ToastUtils.showShort(text)
    }

    fun showLong(resText: Int) {
        ToastUtils.showLong(getString(resText))
    }

    fun showLong(text: String?) {
        ToastUtils.showLong(text)
    }

    fun showProgressDialog() {
        if (mProgressDialogHandler == null) {
            mProgressDialogHandler = ProgressDialogHandler(mBActivity, null, true)
        }
        mProgressDialogHandler?.obtainMessage(ProgressDialogHandler.SHOW_PROGRESS_DIALOG)?.sendToTarget()
    }

    fun dismissProgressDialog() {
        mProgressDialogHandler?.obtainMessage(ProgressDialogHandler.DISMISS_PROGRESS_DIALOG)?.sendToTarget()
    }

    fun add2Disposable(any: Any?) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        if (any is Disposable) {
            mCompositeDisposable?.add(any)
        }
    }

    override fun onDestroy() {
        if (regEvent()) EventBus.getDefault().unregister(this)

        mCompositeDisposable?.clear()
        mProgressDialogHandler = null
        try {
            OkHttpUtil2.getInstance().cancelRequest(this.localClassName)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }
    /**
     * ========================================= EventBus =========================================
     */
    /**
     * 需要接收事件 重写该方法 并返回true
     */
    protected fun regEvent() = false

    /**
     * 子类接受事件 重写该方法
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCommonEventBus(event: Event?) {
    }

    /**
     * 子类接受事件 重写该方法
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onCommonEventBusSticky(event: Event?) {
    }

    /**
     * ========================================= Request Method =========================================
     */
    fun <T> getRequest(request: RequestObject?, t: Class<T>?, callback: IRequestCallback?) {
        // 经测试，getLocalClassName方法拿的是对应子activity的
        OkHttpUtil2.getInstance().getGson(RequestBuilder.build(request), t, callback, this.localClassName)
    }

    /**
     * 使用post方法上传json格式数据并获取请求返回的json数据
     *
     * @param obj Map<String></String>, Object>
     */
    fun <T> postRequest(request: RequestObject?, obj: Any?, t: Class<T>?, callback: IRequestCallback?) {
        OkHttpUtil2.getInstance().postGson(RequestBuilder.build(request), obj, t, callback, this.localClassName)
    }

    /**
     * 使用post方法上传map格式数据并获取请求返回的json数据
     */
    fun <T> postRequest(request: RequestObject?, params: Map<String?, String?>?, t: Class<T>?, callback: IRequestCallback?) {
        OkHttpUtil2.getInstance().postGson(RequestBuilder.build(request), params, t, callback, this.localClassName)
    }

    fun <T> uploadFile(request: RequestObject?, params: Map<String?, Any?>?, t: Class<T>?, callback: IRequestFileCallback?) {
        OkHttpUtil2.getInstance().uploadFile(RequestBuilder.build(request), params, t, callback, this
            .localClassName)
    }

    fun downLoadImage(url: String?, callback: IRequestCallback?) {
        OkHttpUtil2.getInstance().downLoadImage(url, callback, this.localClassName)
    }

    fun downLoadImage(url: String?, iv: ImageView) {
        OkHttpUtil2.getInstance().downLoadImage(url, object : IRequestCallback {
            override fun <T> ObjResponse(isSuccess: Boolean, responseObj: T, e: IOException) {
                iv.setImageBitmap(responseObj as Bitmap)
            }
        }, this.localClassName)
    }

    fun downLoadFile(url: String?, saveDir: String?, saveFileName: String?, callback: IRequestFileCallback?) {
        OkHttpUtil2.getInstance().downLoadFile(url, saveDir, saveFileName, callback, this.localClassName)
    }

    companion object {
        /**
         * 通用网络请求方法 ============================================================================================
         *
         * @param methodName       方法名
         * @param params           url/path/header等参数集合，必须和RequestService里的定义函数保存顺序一致, 没有传null
         * @param map              ( GET:@QueryMap / POST:@FieldMap ), 没有传null
         * @param progressObserver 订阅者
         * @param <T>              如果传入String则返回原始数据，传DataClass则解析成json返回
        </T> */
        fun <T> doCommonRequest(methodName: String?, params: List<String>?, map: Map<String?, Any?>?,
                                progressObserver: ProgressObserver<T>?) {
            OkHttpUtil.getInstance().doRequestImpl(methodName, params, map, progressObserver)
        }

        /**
         * 如果T传入String则返回原始数据，传DataClass则解析成json返回
         *
         * @param url 如果只有方法名，会采用默认的BaseURL
         * @param map 没有参数传null
         */
        @JvmStatic
        fun <T> doCommonGet(url: String, map: Map<String?, Any?>?, progressObserver: ProgressObserver<T>) {
            val params: MutableList<String> = ArrayList()
            params.add((if (url.startsWith("http")) "" else CommonData.SEVER_URL) + url)
            doCommonRequest(getMethodName(progressObserver, "commonGet"), params, map, progressObserver)
        }

        /**
         * 如果T传入String则返回原始数据，传DataClass则解析成json返回
         *
         * @param url 如果只有方法名，会采用默认的BaseURL
         * @param map 没有参数传null
         */
        @JvmStatic
        fun <T> doCommonPost(url: String, map: Map<String?, Any?>?, progressObserver: ProgressObserver<T>) {
            val params: MutableList<String> = ArrayList()
            params.add((if (url.startsWith("http")) "" else CommonData.SEVER_URL) + url)
            doCommonRequest(getMethodName(progressObserver, "commonPost"), params, map, progressObserver)
        }

        /**
         * 以json字符串传送，如果T传入String则返回原始数据，传DataClass则解析成json返回
         *
         * @param url 如果只有方法名，会采用默认的BaseURL
         * @param map 没有参数传null
         */
        fun <T> doCommonPostJson(url: String, map: MutableMap<String?, Any?>?, progressObserver: ProgressObserver<T>) {
            val params: MutableList<String> = ArrayList()
            params.add((if (url.startsWith("http")) "" else CommonData.SEVER_URL) + url)
            if (map != null) map["POST_TYPE"] = "JSON_STRING"
            doCommonRequest(getMethodName(progressObserver, "commonPostJson"), params, map, progressObserver)
        }

        /**
         * 根据T的类型（String/DataClass）来生成RequestServices的方法名
         */
        private fun <T> getMethodName(progressObserver: ProgressObserver<T>, name: String): String {
            val genType = progressObserver.javaClass.genericSuperclass
            val types = (genType as ParameterizedType).actualTypeArguments
            return name + if (types[0] == String::class.java) "Raw" else ""
        }
    }
}
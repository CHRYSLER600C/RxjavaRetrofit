package com.frame.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ObjectUtils
import com.blankj.utilcode.util.ToastUtils
import com.frame.R
import com.frame.activity.BaseActivity
import com.frame.dataclass.bean.Event
import com.frame.utils.gone
import com.frame.utils.invisible
import com.frame.view.TitleBar
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 *
 */
abstract class BaseTitleFragment : Fragment() {

    private var mRootView: LinearLayout? = null
    lateinit var mTitleBar: TitleBar
    protected lateinit var mBActivity: BaseActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBActivity = activity as BaseActivity
        if (ObjectUtils.isEmpty(mRootView)) {
            mRootView = inflater.inflate(R.layout.fragment_base_title, null) as LinearLayout
            mTitleBar = mRootView!!.findViewById(R.id.titleBar)
            val view = setContentView(savedInstanceState)
            if (ObjectUtils.isNotEmpty(view)) {
                mRootView?.addView(view, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
            }
            if (regEvent()) EventBus.getDefault().register(this)
        }
        val parent = mRootView?.parent as? ViewGroup
        parent?.removeView(mRootView)
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initControl()
    }

    protected abstract fun setContentView(savedInstanceState: Bundle?): View

    /**
     * 初始化控件
     */
    protected open fun initControl() {}

    /**
     * 重载后事件点击传递到Fragment
     */
    open fun onViewClicked(view: View?) {}

    fun onBackPressed(): Boolean {
        return false
    }

    override fun onDestroy() {
        mRootView = null
        if (regEvent()) EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    /**
     * ===================================== TitleBar Method =====================================
     */
    fun setLeftBarHide() {
        mTitleBar.getLeftBar()?.invisible()
    }

    fun setTitleBarHide() {
        mTitleBar.getTitleBar()?.gone()
    }

    fun setTitleText(title: String?) {
        mTitleBar.setTitleText(title)
    }

    fun setTitleText(title: Int) {
        mTitleBar.setTitleText(title)
    }

    fun setRightText(content: String?) {
        mTitleBar.setRightText(content)
    }

    fun toast(@StringRes resId: Int) {
        ToastUtils.showShort(resources.getString(resId)) //适配多语言
    }

    fun toast(msg: String?) {
        ToastUtils.showShort(msg)
    }

    /**
     * ========================================= EventBus =========================================
     * 需要接收事件 重写该方法
     */
    protected fun regEvent(): Boolean {
        return false
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCommonEventBus(event: Event?) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onCommonEventBusSticky(event: Event?) {
    }
}
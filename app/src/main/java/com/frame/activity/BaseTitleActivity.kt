package com.frame.activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.frame.R
import com.frame.view.TitleBar

/**
 * 此类在BaseActivity的基础上，自动给界面添加了title功能
 */
open class BaseTitleActivity : BaseActivity() {

    @JvmField
    var mTitleBar: TitleBar? = null

    override fun setContentView(layoutResId: Int) {
        setContentView(LayoutInflater.from(this).inflate(layoutResId, null, false))
    }

    override fun setContentView(view: View) {
        super.setContentView(view)
        mTitleBar = findViewById(R.id.titleBar)
        mTitleBar?.visibility = View.VISIBLE
    }

    /**
     * TitleBar Method ==========================================================================
     */
    fun setLeftBarHide() {
        mTitleBar?.leftBar?.visibility = View.GONE
    }

    fun setTitleBarHide() {
        mTitleBar?.visibility = View.GONE
    }

    // setter title --------------------------------
    fun setTitleText(title: String?) {
        mTitleBar?.setTitleText(title)
    }

    fun setTitleText(title: Int) {
        mTitleBar?.setTitleText(title)
    }

    fun setTitleBgColor(color: Int) {
        mTitleBar?.setTitleBgColor(color)
    }
}
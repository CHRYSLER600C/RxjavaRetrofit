package com.frame.view

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Html
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.frame.R
import com.frame.utils.visible
import kotlinx.android.synthetic.main.include_base_title.view.*

class TitleBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : RelativeLayout(context, attrs) {


    init {
        initView(context)
    }

    /**
     * 初始化界面
     */
    private fun initView(context: Context) {
        View.inflate(context, R.layout.include_base_title, this)
        setLeftBackClick()
    }

    /**
     * Getter Method
     */
    fun getTitleBar(): LinearLayout? {  // 可供不需要的时候隐藏
        return llTitleBar
    }

    // getter left --------------------------------
    fun getLeftBar(): LinearLayout? {
        return llLeftBar
    }

    fun getLeftText(): TextView? {
        return tvLeft
    }

    fun getLeftImg(): ImageView? {
        return ivLeft
    }

    // getter title --------------------------------
    fun getTitleText(): TextView? {
        return tvTitle
    }

    // getter right --------------------------------
    fun getRightBar(): LinearLayout? {
        return llRightBar
    }

    fun getRightText(): TextView? {
        return tvRight
    }

    fun getRightImg(): ImageView? {
        return ivRight
    }
    /**
     * Setter Method
     */
    // setter left --------------------------------
    private fun setLeftBackClick() {
        llLeftBar.setOnClickListener {
            val ctx = this@TitleBar.context
            if (ctx is Activity) {
                ctx.onBackPressed()
            }
        }
    }

    fun setLeftText(text: String?) {
        tvLeft.text = text
        tvLeft.visible()
    }

    fun setLeftImageDrawable(drawable: Drawable?) {
        ivLeft.setImageDrawable(drawable)
        ivLeft.visible()
    }

    fun setLeftImageResource(resId: Int) {
        ivRight.setImageResource(resId)
        ivRight.visible()
    }

    // setter title --------------------------------
    fun setTitleText(title: String?) {
        tvTitle.text = Html.fromHtml(title)
    }

    fun setTitleText(title: Int) {
        tvTitle.setText(title)
    }

    fun setTitleBgColor(color: Int) {
        llTitleBar.setBackgroundColor(color)
    }

    fun setTitleBgDrawable(background: Drawable?) {
        llTitleBar.setBackgroundDrawable(background)
    }

    fun setTitleBgResource(resid: Int) {
        llTitleBar.setBackgroundResource(resid)
    }

    // setter right --------------------------------
    fun setRightText(text: String?) {
        tvRight.text = text
        tvRight.visible()
    }

    fun setRightText(text: Int) {
        tvRight.setText(text)
        tvRight.visible()
    }

    fun setRightImageDrawable(drawable: Drawable?) {
        ivRight.setImageDrawable(drawable)
        ivRight.visible()
    }

    fun setRightImageResource(resId: Int) {
        ivRight.setImageResource(resId)
        ivRight.visible()
    }
}
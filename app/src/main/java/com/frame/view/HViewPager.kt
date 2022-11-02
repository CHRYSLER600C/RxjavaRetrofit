package com.frame.view

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager

/**
 * 解决高度问题
 */
class HViewPager : ViewPager {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var height = 0

        //下面遍历所有child的高度
        for (i in 0 until childCount) {
            val child = getChildAt(i)

            val params = child.layoutParams
            child.measure(widthMeasureSpec, getChildMeasureSpec(heightMeasureSpec, 0, params.height))

            //错误写法
//            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

            //采用最大的view的高度
            height = height.coerceAtLeast(child.measuredHeight)
        }
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY))
    }
}
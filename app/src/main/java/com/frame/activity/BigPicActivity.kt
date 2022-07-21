package com.frame.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.ObjectUtils
import com.blankj.utilcode.util.RegexUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.frame.R
import com.frame.adapter.ViewPagerAdapter
import com.frame.view.MyViewPager
import java.util.*

class BigPicActivity : BaseActivity() {

    private var mViewPager: MyViewPager? = null
    private val mViews: MutableList<View> = ArrayList()
    private var mPicUrls: MutableList<String> = ArrayList()
    private var mPicIds: MutableList<Int> = ArrayList()
    private var mViewPagerAdapter: ViewPagerAdapter? = null
    private var mIndexStart = 0
    private var mTvTitle: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_big_pic)
        intentParams()
        initControls()
    }

    private fun intentParams() {
        mIndexStart = intent.getIntExtra("index", 0)
        intent.getStringArrayListExtra("picUrls")?.let { mPicUrls.addAll(it) }
        intent.getIntegerArrayListExtra("picIds")?.let { mPicIds.addAll(it) }

        mTvTitle = findViewById(R.id.tvTitleContent)
        if (ObjectUtils.isNotEmpty(mPicUrls)) {
            mTvTitle?.text = (mIndexStart + 1).toString() + "/" + mPicUrls.size
        } else if (ObjectUtils.isNotEmpty(mPicIds)) {
            mTvTitle?.text = (mIndexStart + 1).toString() + "/" + mPicIds.size
        }
    }

    private fun initControls() {
        findViewById<View>(R.id.ivTitleLeft).setOnClickListener { v: View? -> finish() }
        mViewPager = findViewById(R.id.myViewPager)
        if (ObjectUtils.isNotEmpty(mPicUrls)) {
            for (i in mPicUrls.indices) {
                val vParent = View.inflate(this, R.layout.big_pic_loading, null)
                loadUrlImage(vParent, i)
                mViews.add(vParent)
            }
        } else if (ObjectUtils.isNotEmpty(mPicIds)) {
            for (i in mPicIds.indices) {
                val rv = View.inflate(this, R.layout.big_pic_loading, null)
                val iv = rv.findViewById<ImageView>(R.id.ivBigPicLoading)
                iv.setImageResource(mPicIds!![i])
                rv.findViewById<View>(R.id.pbBigPicLoading).visibility = View.GONE
                mViews.add(rv)
            }
        }
        mViewPagerAdapter = ViewPagerAdapter(mViews)
        mViewPager?.adapter = mViewPagerAdapter
        mViewPager?.addOnPageChangeListener(GuidePageChangeListener())
        mViewPager?.currentItem = mIndexStart
    }

    private fun loadUrlImage(vParent: View, position: Int) { // 加载网络图片
        val picUrl = mPicUrls!![position]
        if (RegexUtils.isURL(picUrl)) {
            Glide.with(mBActivity).load(picUrl).into(object : SimpleTarget<Drawable?>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable?>?) {
                    val ivCompanyPic = vParent.findViewById<ImageView>(R.id.ivBigPicLoading)
                    vParent.findViewById<View>(R.id.pbBigPicLoading).visibility = View.GONE
                    ivCompanyPic.setImageDrawable(resource)
                }
            })
        }
    }

    private inner class GuidePageChangeListener : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(arg0: Int) {}
        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
        override fun onPageSelected(arg0: Int) {
            if (ObjectUtils.isNotEmpty(mPicUrls)) {
                mTvTitle?.text = (arg0 + 1).toString() + "/" + mPicUrls.size
                loadUrlImage(mViews[arg0], arg0)
            } else if (ObjectUtils.isNotEmpty(mPicIds)) {
                mTvTitle?.text = (arg0 + 1).toString() + "/" + mPicIds.size
            }
        }
    }
}
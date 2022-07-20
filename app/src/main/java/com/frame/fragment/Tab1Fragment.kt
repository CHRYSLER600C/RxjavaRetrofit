package com.frame.fragment

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ScreenUtils
import com.frame.R
import com.frame.activity.BaseActivity.Companion.doCommonGet
import com.frame.activity.SearchActivity
import com.frame.dataclass.DataClass
import com.frame.dataclass.bean.Template
import com.frame.observers.ProgressObserver
import com.frame.utils.JU
import com.frame.utils.KLU
import com.frame.utils.LU
import com.google.gson.internal.LinkedTreeMap
import com.scwang.smartrefresh.header.PhoenixHeader
import com.scwang.smartrefresh.layout.footer.BallPulseFooter
import kotlinx.android.synthetic.main.fragment_tab1.*
import org.byteam.superadapter.SuperAdapter
import org.byteam.superadapter.SuperViewHolder
import java.util.*

/**
 * 首页
 */
class Tab1Fragment : BaseTitleFragment() {

    private var mCurrPage = 0
    private var mSAdapterArticle: SuperAdapter<*>? = null
    private val mList: MutableList<LinkedTreeMap<String, Any>> = ArrayList()
    override fun setContentView(savedInstanceState: Bundle?): View {
        return View.inflate(mBActivity, R.layout.fragment_tab1, null)
    }

    override fun initControl() {
        setLeftBarHide()
        setTitleText("首页")
        mTitleBar.setRightImageResource(R.drawable.ic_search_white_24dp)
        val lp = mTitleBar.getRightImg()?.layoutParams as LinearLayout.LayoutParams
        lp.width = ConvertUtils.dp2px(25f)
        mTitleBar.getRightBar()?.setOnClickListener { view: View? -> ActivityUtils.startActivity(SearchActivity::class.java) }
        rvBlock.layoutManager = GridLayoutManager(mBActivity, 4)
        rvBlock.adapter = getSuperAdapterBlock()
        rvTab1.layoutManager = LinearLayoutManager(mBActivity)
        rvTab1.adapter = WxArticleDetailFragment.getSuperAdapter(mBActivity, mList).also { mSAdapterArticle = it }
        rvTab1.isNestedScrollingEnabled = false
        nsvTab1.setOnScrollChangeListener { _: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            llGotoTop.visibility = if (scrollY >= ScreenUtils.getScreenHeight() / 2) View.VISIBLE else View.GONE
        }
        setSmartRefreshLayout()
        getBannerData()
        getNetData(mCurrPage, true)
    }

    override fun onResume() {
        super.onResume()
        bannerAdv?.startAutoPlay()
    }

    override fun onStop() {
        super.onStop()
        bannerAdv?.stopAutoPlay()
    }

    override fun onViewClicked(view: View?) {
        when (view?.id) {
            R.id.llGotoTop -> {
                nsvTab1.fling(0)
                nsvTab1.smoothScrollTo(0, 0)
            }
        }
    }

    private fun setSmartRefreshLayout() {
        srlTab1.setRefreshHeader(PhoenixHeader(mBActivity))
        srlTab1.setRefreshFooter(BallPulseFooter(mBActivity))
        srlTab1.setOnRefreshListener {
            getBannerData()
            getNetData(0.also { mCurrPage = it }, false)
        }
        srlTab1.setOnLoadMoreListener { refreshLayout -> getNetData(++mCurrPage, false) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                else -> {
                }
            }
        }
    }

    private fun getSuperAdapterBlock(): SuperAdapter<*> {
        return object : SuperAdapter<Template>(mBActivity, LU.getBlockList(), R.layout.item_block_list) {
            override fun onBind(holder: SuperViewHolder, viewType: Int, layoutPosition: Int, template: Template) {
                holder.setImageResource(R.id.ivIconBlock, template.resId)
                holder.setText(R.id.tvTextBlock, template.content)
                holder.itemView.setOnClickListener { view: View? ->
                    KLU.gotoActivityAnim(mBActivity, template.cls, view)
                }
            }
        }
    }

    private fun getBannerData() {
        doCommonGet("banner/json", null, object : ProgressObserver<DataClass>(mBActivity, false) {
            override fun onNext(dc: DataClass) {
                LU.initBanner(mBActivity, bannerAdv, JU.al(dc.obj, "data"))
            }
        })
    }

    private fun getNetData(currPage: Int, isLoading: Boolean) {
        doCommonGet("article/list/$currPage/json", null, object : ProgressObserver<DataClass>(mBActivity, isLoading, srlTab1) {
            override fun onNext(dc: DataClass) {
                val data = JU.m<LinkedTreeMap<String, Any>>(dc.obj, "data")
                srlTab1.setEnableLoadMore(!JU.b(data, "over"))
                if (0 == JU.i(data, "offset")) mList.clear()
                mList.addAll(JU.al(data, "datas"))
                mSAdapterArticle!!.notifyDataSetChanged()
            }
        })
    }
}
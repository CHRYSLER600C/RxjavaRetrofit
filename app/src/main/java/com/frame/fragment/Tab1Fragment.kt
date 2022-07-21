package com.frame.fragment

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.widget.NestedScrollView
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ScreenUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.frame.R
import com.frame.activity.BaseActivity.Companion.doCommonGet
import com.frame.activity.SearchActivity
import com.frame.dataclass.DataClass
import com.frame.dataclass.bean.Template
import com.frame.observers.ProgressObserver
import com.frame.utils.JU
import com.frame.utils.LU
import com.google.gson.internal.LinkedTreeMap
import com.scwang.smartrefresh.header.PhoenixHeader
import com.scwang.smartrefresh.layout.footer.BallPulseFooter
import kotlinx.android.synthetic.main.fragment_tab1.*
import kotlinx.android.synthetic.main.layout_goto_top.*
import java.util.*

/**
 * 首页
 */
class Tab1Fragment : BaseTitleFragment() {

    private var mCurrPage = 0

    override fun setContentView(savedInstanceState: Bundle?): View {
        return View.inflate(mBActivity, R.layout.fragment_tab1, null)
    }

    override fun initControl() {
        setLeftBarHide()
        setTitleText("首页")
        mTitleBar.setRightImageResource(R.drawable.ic_search_white_24dp)
        (mTitleBar.getRightImg()?.layoutParams as? LinearLayout.LayoutParams)?.width = ConvertUtils.dp2px(25f)
        mTitleBar.getRightBar()?.setOnClickListener { ActivityUtils.startActivity(SearchActivity::class.java) }

        rvBlock?.adapter = getQuickAdapter()
        rvTab1?.adapter = WxArticleDetailFragment.getQuickAdapter(mBActivity)
        rvTab1?.isNestedScrollingEnabled = false
        nsvTab1?.setOnScrollChangeListener { _: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            ivGotoTop.visibility = if (scrollY >= ScreenUtils.getScreenHeight() / 2) View.VISIBLE else View.GONE
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
            R.id.ivGotoTop -> {
                nsvTab1.fling(0)
                nsvTab1.smoothScrollTo(0, 0)
            }
        }
    }

    private fun setSmartRefreshLayout() {
        srlTab1.setRefreshHeader(PhoenixHeader(mBActivity))
            .setRefreshFooter(BallPulseFooter(mBActivity))
            .setOnRefreshListener {
                getBannerData()
                getNetData(0.also { mCurrPage = it }, false)
            }
            .setOnLoadMoreListener { getNetData(++mCurrPage, false) }
    }

    private fun getQuickAdapter(): BaseQuickAdapter<Template, BaseViewHolder> {
        return object : BaseQuickAdapter<Template, BaseViewHolder>(R.layout.item_block_list, LU.getBlockList()) {
            override fun convert(holder: BaseViewHolder, template: Template) {
                holder.setImageResource(R.id.ivIconBlock, template.resId)
                    .setText(R.id.tvTextBlock, template.content)
                holder.itemView.setOnClickListener { view: View? -> LU.gotoActivityAnim(view, template.cls) }
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

                val adapter = rvTab1?.adapter as? BaseQuickAdapter<LinkedTreeMap<String, Any>, BaseViewHolder>
                JU.al<ArrayList<LinkedTreeMap<String, Any>>>(data, "datas")?.let {
                    if (0 == JU.i(data, "offset")) adapter?.setList(it) else adapter?.addData(it)
                }
            }
        })
    }
}
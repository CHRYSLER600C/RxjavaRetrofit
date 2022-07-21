package com.frame.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ObjectUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.frame.R
import com.frame.activity.BaseActivity.Companion.doCommonGet
import com.frame.activity.WebViewActivity
import com.frame.common.CommonData
import com.frame.dataclass.DataClass
import com.frame.observers.ProgressObserver
import com.frame.utils.CU
import com.frame.utils.JU
import com.frame.utils.LU
import com.google.gson.internal.LinkedTreeMap
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import kotlinx.android.synthetic.main.common_layout_srl_rv.*
import java.util.*

/**
 */
class WxArticleDetailFragment : BaseTitleFragment() {

    private var mCurrId = 0 //当前公众号id
    private var mCurrPage = 1
    private var mQuickAdapter: BaseQuickAdapter<LinkedTreeMap<String, Any>, BaseViewHolder>? = null

    override fun setContentView(savedInstanceState: Bundle?): View {
        return View.inflate(mBActivity, R.layout.common_layout_srl_rv, null)
    }

    override fun initControl() {
        super.initControl()
        setTitleBarHide()
        mCurrId = CU.parserInt(requireArguments().getString(CommonData.PARAM1))
        if (mCurrId == 0) return
        recyclerView?.layoutManager = LinearLayoutManager(mBActivity)
        recyclerView?.adapter = getQuickAdapter(mBActivity).also { mQuickAdapter = it }
        setSmartRefreshLayout()
    }

    override fun onResume() {
        super.onResume()
        if (ObjectUtils.isEmpty(mQuickAdapter?.data)) {
            getNetData(mCurrId, 1.also { mCurrPage = it }, true)
        }
    }

    override fun onViewClicked(view: View?) {
        when (view?.id) {
            R.id.ivGotoTop -> recyclerView.smoothScrollToPosition(0)
        }
    }

    private fun setSmartRefreshLayout() {
        refreshLayout.setRefreshFooter(ClassicsFooter(mBActivity))
        refreshLayout.setOnRefreshListener { getNetData(mCurrId, 1.also { mCurrPage = it }, false) }
        refreshLayout.setOnLoadMoreListener { getNetData(mCurrId, ++mCurrPage, false) }
    }

    private fun getNetData(id: Int, currPage: Int, isLoading: Boolean) {
        doCommonGet("wxarticle/list/$id/$currPage/json", null,
            object : ProgressObserver<DataClass>(mBActivity, isLoading, refreshLayout) {
                override fun onNext(dc: DataClass) {
                    val data = JU.m<LinkedTreeMap<String, Any>>(dc.obj, "data")
                    refreshLayout.setEnableLoadMore(!JU.b(data, "over"))

                    JU.al<ArrayList<LinkedTreeMap<String, Any>>>(data, "datas")?.let {
                        if (0 == JU.i(data, "offset")) mQuickAdapter?.setList(it) else mQuickAdapter?.addData(it)
                    }
                }
            })
    }

    companion object {
        fun getInstance(param1: String?, param2: String?): WxArticleDetailFragment {
            val fragment = WxArticleDetailFragment()
            val args = Bundle()
            args.putString(CommonData.PARAM1, param1)
            args.putString(CommonData.PARAM2, param2)
            fragment.arguments = args
            return fragment
        }

        fun getQuickAdapter(activity: Activity): BaseQuickAdapter<LinkedTreeMap<String, Any>, BaseViewHolder> {
            return object : BaseQuickAdapter<LinkedTreeMap<String, Any>, BaseViewHolder>(R.layout.item_article_list) {
                override fun convert(holder: BaseViewHolder, map: LinkedTreeMap<String, Any>) {
                    holder.setText(R.id.tvArticleTitle, JU.sh(map, "title"))
                        .setText(R.id.tvChapterName, JU.s(map, "chapterName"))
                        .setText(R.id.tvSuperChapterName, JU.s(map, "superChapterName"))
                        .setText(R.id.tvAuthor, "作者：" + JU.s(map, "author"))
                        .setText(R.id.tvDate, JU.s(map, "niceDate"))
                    holder.itemView.setOnClickListener { view: View? ->
                        LU.gotoActivityAnim(view, Intent(activity, WebViewActivity::class.java)
                            .putExtra(WebViewActivity.TYPE, WebViewActivity.TYPE_LOAD_URL)
                            .putExtra("title", JU.s(map, "title"))
                            .putExtra("url", JU.s(map, "link")))

                    }
                }
            }
        }
    }
}
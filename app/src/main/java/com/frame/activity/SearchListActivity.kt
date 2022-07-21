package com.frame.activity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ObjectUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.frame.R
import com.frame.dataclass.DataClass
import com.frame.fragment.WxArticleDetailFragment
import com.frame.observers.ProgressObserver
import com.frame.utils.JU
import com.google.gson.internal.LinkedTreeMap
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import kotlinx.android.synthetic.main.common_layout_srl_rv.*
import java.util.*

/**
 */
class SearchListActivity : BaseTitleActivity() {

    private var mCurrKey: String? = null
    private var mCurrPage = 0
    private var mQuickAdapter: BaseQuickAdapter<LinkedTreeMap<String, Any>, BaseViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.common_layout_srl_rv)
        initControl()
    }

    private fun initControl() {
        mCurrKey = intent.getStringExtra("key")
        setTitleText(mCurrKey)
        recyclerView?.layoutManager = LinearLayoutManager(mBActivity)
        recyclerView?.adapter = WxArticleDetailFragment.getQuickAdapter(mBActivity).also { mQuickAdapter = it }
        setSmartRefreshLayout()
    }

    public override fun onResume() {
        super.onResume()
        if (ObjectUtils.isEmpty(mQuickAdapter?.data)) {
            getNetData(mCurrKey, 0.also { mCurrPage = it }, true)
        }
    }

    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.ivGotoTop -> recyclerView.smoothScrollToPosition(0)
        }
    }

    private fun setSmartRefreshLayout() {
        refreshLayout.setRefreshFooter(ClassicsFooter(mBActivity))
            .setOnRefreshListener { getNetData(mCurrKey, 0.also { mCurrPage = it }, false) }
            .setOnLoadMoreListener { getNetData(mCurrKey, ++mCurrPage, false) }
    }

    private fun getNetData(key: String?, currPage: Int, isLoading: Boolean) {
        val map: MutableMap<String?, Any?> = HashMap()
        map["k"] = key
        doCommonPost("article/query/$currPage/json", map, object : ProgressObserver<DataClass>(mBActivity, isLoading, refreshLayout) {
            override fun onNext(dc: DataClass) {
                val data = JU.m<LinkedTreeMap<String, Any>>(dc.obj, "data")
                refreshLayout.setEnableLoadMore(!JU.b(data, "over"))

                JU.al<ArrayList<LinkedTreeMap<String, Any>>>(data, "datas")?.let {
                    if (0 == JU.i(data, "offset")) mQuickAdapter?.setList(it) else mQuickAdapter?.addData(it)
                }
            }
        })
    }
}
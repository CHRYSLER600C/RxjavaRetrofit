package com.frame.activity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ObjectUtils
import com.frame.R
import com.frame.dataclass.DataClass
import com.frame.fragment.WxArticleDetailFragment
import com.frame.observers.ProgressObserver
import com.frame.utils.JU
import com.google.gson.internal.LinkedTreeMap
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import kotlinx.android.synthetic.main.common_layout_srl_rv.*
import org.byteam.superadapter.SuperAdapter
import java.util.*

/**
 */
class SearchListActivity : BaseTitleActivity() {

    private var mCurrKey: String? = null
    private var mCurrPage = 0
    private var mSuperAdapter: SuperAdapter<*>? = null
    private val mList: MutableList<LinkedTreeMap<String, Any>?> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.common_layout_srl_rv)
        initControl()
    }

    private fun initControl() {
        mCurrKey = intent.getStringExtra("key")
        setTitleText(mCurrKey)
        recyclerView?.layoutManager = LinearLayoutManager(mBActivity)
        recyclerView?.adapter = WxArticleDetailFragment.getSuperAdapter(mBActivity, mList).also { mSuperAdapter = it }
        setSmartRefreshLayout()
    }

    public override fun onResume() {
        super.onResume()
        if (ObjectUtils.isEmpty(mList)) {
            getNetData(mCurrKey, 0.also { mCurrPage = it }, true)
        }
    }

    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.llGotoTop -> recyclerView.smoothScrollToPosition(0)
        }
    }

    private fun setSmartRefreshLayout() {
        refreshLayout.setRefreshFooter(ClassicsFooter(mBActivity))
        refreshLayout.setOnRefreshListener { getNetData(mCurrKey, 0.also { mCurrPage = it }, false) }
        refreshLayout.setOnLoadMoreListener { getNetData(mCurrKey, ++mCurrPage, false) }
    }

    private fun getNetData(key: String?, currPage: Int, isLoading: Boolean) {
        val map: MutableMap<String?, Any?> = HashMap()
        map["k"] = key
        doCommonPost("article/query/$currPage/json", map, object : ProgressObserver<DataClass>(mBActivity, isLoading, refreshLayout) {
            override fun onNext(dc: DataClass) {
                val data = JU.m<LinkedTreeMap<String, Any>>(dc.obj, "data")
                refreshLayout.setEnableLoadMore(!JU.b(data, "over"))
                if (0 == JU.i(data, "offset")) mList.clear()
                mList.addAll(JU.al(data, "datas"))
                mSuperAdapter!!.notifyDataSetChanged()
            }
        })
    }
}
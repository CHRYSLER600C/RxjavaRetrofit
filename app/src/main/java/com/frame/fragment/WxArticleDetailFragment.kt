package com.frame.fragment

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ObjectUtils
import com.frame.R
import com.frame.activity.BaseActivity.Companion.doCommonGet
import com.frame.activity.WebViewActivity
import com.frame.common.CommonData
import com.frame.dataclass.DataClass
import com.frame.observers.ProgressObserver
import com.frame.utils.JU
import com.frame.utils.KLU
import com.google.gson.internal.LinkedTreeMap
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import kotlinx.android.synthetic.main.common_layout_srl_rv.*
import org.byteam.superadapter.SuperAdapter
import org.byteam.superadapter.SuperViewHolder
import java.util.*

/**
 */
class WxArticleDetailFragment : BaseTitleFragment() {

    private var mCurrId = 0 //当前公众号id
    private var mCurrPage = 1
    private var mSuperAdapter: SuperAdapter<*>? = null
    private val mList: MutableList<LinkedTreeMap<String, Any>?> = ArrayList()

    override fun setContentView(savedInstanceState: Bundle?): View {
        return View.inflate(mBActivity, R.layout.common_layout_srl_rv, null)
    }

    override fun initControl() {
        super.initControl()
        setTitleBarHide()
        try {
            mCurrId = requireArguments().getString(CommonData.PARAM1)?.toDouble()?.toInt() ?: 0
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        if (mCurrId == 0) return
        recyclerView.layoutManager = LinearLayoutManager(mBActivity)
        recyclerView.adapter = getSuperAdapter(mBActivity, mList).also { mSuperAdapter = it }
        setSmartRefreshLayout()
    }

    override fun onResume() {
        super.onResume()
        if (ObjectUtils.isEmpty(mList)) {
            getNetData(mCurrId, 1.also { mCurrPage = it }, true)
        }
    }

    override fun onViewClicked(view: View?) {
        when (view?.id) {
            R.id.llGotoTop -> recyclerView.smoothScrollToPosition(0)
        }
    }

    private fun setSmartRefreshLayout() {
        refreshLayout.setRefreshFooter(ClassicsFooter(mBActivity))
        refreshLayout.setOnRefreshListener { refreshLayout -> getNetData(mCurrId, 1.also { mCurrPage = it }, false) }
        refreshLayout.setOnLoadMoreListener { refreshLayout -> getNetData(mCurrId, ++mCurrPage, false) }
    }

    private fun getNetData(id: Int, currPage: Int, isLoading: Boolean) {
        doCommonGet("wxarticle/list/$id/$currPage/json", null,
            object : ProgressObserver<DataClass>(mBActivity, isLoading, refreshLayout) {
                override fun onNext(dc: DataClass) {
                    val data = JU.m<LinkedTreeMap<String, Any>>(dc.obj, "data")
                    refreshLayout.setEnableLoadMore(!JU.b(data, "over"))
                    if (0 == JU.i(data, "offset")) mList.clear()
                    mList.addAll(JU.al(data, "datas"))
                    mSuperAdapter!!.notifyDataSetChanged()
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

        fun getSuperAdapter(activity: Activity, list: List<LinkedTreeMap<String, Any>?>?): SuperAdapter<*> {
            return object : SuperAdapter<LinkedTreeMap<String, Any>>(activity, list, R.layout.item_article_list) {
                override fun onBind(holder: SuperViewHolder, viewType: Int, layoutPosition: Int, map: LinkedTreeMap<String, Any>) {
                    holder.setText(R.id.tvArticleTitle, JU.sh(map, "title"))
                        .setText(R.id.tvChapterName, JU.s(map, "chapterName"))
                        .setText(R.id.tvSuperChapterName, JU.s(map, "superChapterName"))
                        .setText(R.id.tvAuthor, "作者：" + JU.s(map, "author"))
                        .setText(R.id.tvDate, JU.s(map, "niceDate"))
                    holder.itemView.setOnClickListener { view: View? ->
                        val i = Intent(activity, WebViewActivity::class.java)
                            .putExtra(WebViewActivity.TYPE, WebViewActivity.TYPE_LOAD_URL)
                            .putExtra("title", JU.s(map, "title"))
                            .putExtra("url", JU.s(map, "link"))
                        KLU.gotoActivityAnim(activity, i, view)

                    }
                }
            }
        }
    }
}
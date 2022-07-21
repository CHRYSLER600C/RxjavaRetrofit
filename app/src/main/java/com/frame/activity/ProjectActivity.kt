package com.frame.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ObjectUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.frame.R
import com.frame.dataclass.DataClass
import com.frame.httputils.ImageLoaderUtil
import com.frame.observers.ProgressObserver
import com.frame.utils.CU
import com.frame.utils.JU
import com.frame.utils.LU
import com.google.gson.internal.LinkedTreeMap
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import kotlinx.android.synthetic.main.activity_project.*
import kotlin.collections.ArrayList

/**
 */
class ProjectActivity : BaseTitleActivity() {

    private val mListType: MutableList<LinkedTreeMap<String, Any>> = ArrayList()
    private var mCurrId = 0
    private var mCurrPage = 1
    private var mLastClickPos = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)
        initControl()
    }

    private fun initControl() {
        setTitleText("知识体系")
        mTitleBar?.setRightText("项目")
        mTitleBar?.setRightImageResource(R.drawable.ic_arrow_drop_down_white_24dp)
        mTitleBar?.getRightBar()?.setOnClickListener { view: View? ->
            if (drawerLayout?.isDrawerOpen(GravityCompat.END) == true)
                drawerLayout?.closeDrawer(GravityCompat.END) else drawerLayout?.openDrawer(GravityCompat.END)
        }
        rvSlide?.layoutManager = LinearLayoutManager(mBActivity)
        rvSlide?.setHasFixedSize(true)
        rvSlide?.adapter = getQuickAdapterType()
        rvProject?.layoutManager = LinearLayoutManager(mBActivity)
        rvProject?.setHasFixedSize(true)
        rvProject?.adapter = getQuickAdapter()
        setSmartRefreshLayout()
    }

    override fun onResume() {
        super.onResume()
        if (ObjectUtils.isEmpty(mListType)) getNetDataType()
    }

    override fun onBackPressed() {
        if (drawerLayout?.isDrawerOpen(GravityCompat.END) == true) {
            drawerLayout?.closeDrawer(GravityCompat.END)
            return
        }
        super.onBackPressed()
    }

    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.ivGotoTop -> rvProject?.smoothScrollToPosition(0)
        }
    }

    private fun setSmartRefreshLayout() {
        refreshLayout?.setRefreshFooter(ClassicsFooter(mBActivity))
        refreshLayout?.setOnRefreshListener { getNetData(mCurrId, 1.also { mCurrPage = it }, false) }
        refreshLayout?.setOnLoadMoreListener { getNetData(mCurrId, ++mCurrPage, false) }
    }

    private fun getQuickAdapterType(): BaseQuickAdapter<LinkedTreeMap<String, Any>, BaseViewHolder> {
        return object : BaseQuickAdapter<LinkedTreeMap<String, Any>, BaseViewHolder>(R.layout.item_project_type, mListType) {
            override fun convert(holder: BaseViewHolder, map: LinkedTreeMap<String, Any>) {
                holder.setText(R.id.tvProjectType, JU.sh(map, "name"))
                if (mLastClickPos == holder.adapterPosition) {
                    holder.setBackgroundColor(R.id.tvProjectType, Color.parseColor("#d1d1d1"))
                }
                holder.itemView.setOnClickListener { view: View? ->
                    mLastClickPos = holder.adapterPosition
                    rvSlide?.adapter?.notifyDataSetChanged()
                    drawerLayout?.closeDrawer(GravityCompat.END)
                    setTitleText(JU.s(mListType[mLastClickPos], "name"))
                    mCurrId = JU.i(mListType[mLastClickPos], "id")
                    getNetData(mCurrId, 1.also { mCurrPage = it }, false)
                }
            }
        }
    }

    private fun getQuickAdapter(): BaseQuickAdapter<LinkedTreeMap<String, Any>, BaseViewHolder> {
        return object : BaseQuickAdapter<LinkedTreeMap<String, Any>, BaseViewHolder>(R.layout.item_project_list) {
            override fun convert(holder: BaseViewHolder, map: LinkedTreeMap<String, Any>) {
                ImageLoaderUtil.loadImage(mBActivity, JU.s(map, "envelopePic"), holder.getView(R.id.ivItemProject), 0, 0)
                holder.setText(R.id.tvItemProjectTitle, JU.s(map, "title"))
                holder.setTextColor(R.id.tvItemProjectTitle, CU.randomColor())
                holder.setText(R.id.tvItemProjectContent, JU.s(map, "desc"))
                holder.setText(R.id.tvItemProjectAuthor, "作者：" + JU.s(map, "author"))
                holder.setText(R.id.tvItemProjectTime, JU.s(map, "niceDate"))
                holder.itemView.setOnClickListener { view: View? ->
                    LU.gotoActivityAnim(view, Intent(mBActivity, WebViewActivity::class.java)
                        .putExtra(WebViewActivity.TYPE, WebViewActivity.TYPE_LOAD_URL)
                        .putExtra("title", JU.s(map, "title"))
                        .putExtra("url", JU.s(map, "link")))
                }
            }
        }
    }

    private fun getNetDataType() {
        doCommonGet("project/tree/json", null, object : ProgressObserver<DataClass>(mBActivity, true) {
            override fun onNext(dc: DataClass) {
                mListType.clear()
                mListType.addAll(JU.al(dc.obj, "data"))
                rvSlide?.adapter?.notifyDataSetChanged()
                if (mListType.size > 0) {
                    setTitleText(JU.s(mListType[0], "name"))
                    mCurrId = JU.i(mListType[0], "id")
                    getNetData(mCurrId, 1.also { mCurrPage = it }, false)
                }
            }
        })
    }

    private fun getNetData(id: Int, currPage: Int, isLoading: Boolean) {
        doCommonGet("project/list/$currPage/json?cid=$id", null,
            object : ProgressObserver<DataClass>(mBActivity, isLoading, refreshLayout) {
                override fun onNext(dc: DataClass) {
                    val data = JU.m<LinkedTreeMap<String, Any>>(dc.obj, "data")
                    refreshLayout?.setEnableLoadMore(!JU.b(data, "over"))

                    val adapter = rvProject?.adapter as? BaseQuickAdapter<LinkedTreeMap<String, Any>, BaseViewHolder>
                    JU.al<ArrayList<LinkedTreeMap<String, Any>>>(data, "datas")?.let {
                        if (0 == JU.i(data, "offset")) adapter?.setList(it) else adapter?.addData(it)
                    }
                }
            })
    }
}
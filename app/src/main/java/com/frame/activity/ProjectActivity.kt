package com.frame.activity

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ObjectUtils
import com.frame.R
import com.frame.dataclass.DataClass
import com.frame.httputils.ImageLoaderUtil
import com.frame.observers.ProgressObserver
import com.frame.utils.CU
import com.frame.utils.JU
import com.frame.utils.KLU
import com.google.gson.internal.LinkedTreeMap
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import kotlinx.android.synthetic.main.activity_project.*
import org.byteam.superadapter.SuperAdapter
import org.byteam.superadapter.SuperViewHolder
import java.util.*

/**
 */
class ProjectActivity : BaseTitleActivity() {

    private val mListType: MutableList<LinkedTreeMap<String, Any>?> = ArrayList()
    private val mList: MutableList<LinkedTreeMap<String, Any>> = ArrayList()
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
        rvSlide?.adapter = getSuperAdapterType()
        rvProject?.layoutManager = LinearLayoutManager(mBActivity)
        rvProject?.setHasFixedSize(true)
        rvProject?.adapter = getSuperAdapter()
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
            R.id.llGotoTop -> rvProject?.smoothScrollToPosition(0)
        }
    }

    private fun setSmartRefreshLayout() {
        refreshLayout?.setRefreshFooter(ClassicsFooter(mBActivity))
        refreshLayout?.setOnRefreshListener { getNetData(mCurrId, 1.also { mCurrPage = it }, false) }
        refreshLayout?.setOnLoadMoreListener { getNetData(mCurrId, ++mCurrPage, false) }
    }

    private fun getSuperAdapterType(): SuperAdapter<*> {
        return object : SuperAdapter<LinkedTreeMap<String, Any>>(mBActivity, mListType, R.layout.item_project_type) {
            override fun onBind(holder: SuperViewHolder, viewType: Int, layoutPosition: Int, map: LinkedTreeMap<String, Any>) {
                holder.setText(R.id.tvProjectType, JU.sh(map, "name"))
                if (mLastClickPos == layoutPosition) {
                    holder.setBackgroundColor(R.id.tvProjectType, Color.parseColor("#d1d1d1"))
                }
                holder.itemView.setOnClickListener { view: View? ->
                    mLastClickPos = layoutPosition
                    rvSlide?.adapter?.notifyDataSetChanged()
                    drawerLayout?.closeDrawer(GravityCompat.END)
                    setTitleText(JU.s(mListType[mLastClickPos], "name"))
                    mCurrId = JU.i(mListType[mLastClickPos], "id")
                    getNetData(mCurrId, 1.also { mCurrPage = it }, false)
                }
            }
        }
    }

    private fun getSuperAdapter(): SuperAdapter<*> {
        return object : SuperAdapter<LinkedTreeMap<String, Any>>(mBActivity, mList, R.layout.item_project_list) {
            override fun onBind(holder: SuperViewHolder, viewType: Int, layoutPosition: Int, map: LinkedTreeMap<String, Any>) {
                ImageLoaderUtil.loadImage(mBActivity, JU.s(map, "envelopePic"), holder.findViewById(R.id.ivItemProject), 0, 0)
                holder.setText(R.id.tvItemProjectTitle, JU.s(map, "title"))
                holder.setTextColor(R.id.tvItemProjectTitle, CU.randomColor())
                holder.setText(R.id.tvItemProjectContent, JU.s(map, "desc"))
                holder.setText(R.id.tvItemProjectAuthor, "作者：" + JU.s(map, "author"))
                holder.setText(R.id.tvItemProjectTime, JU.s(map, "niceDate"))
                holder.itemView.setOnClickListener { view: View? ->
                    val i = Intent(mBActivity, WebViewActivity::class.java)
                        .putExtra(WebViewActivity.TYPE, WebViewActivity.TYPE_LOAD_URL)
                        .putExtra("title", JU.s(map, "title"))
                        .putExtra("url", JU.s(map, "link"))
                    KLU.gotoActivityAnim(mBActivity, i, view)
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
                    if (0 == JU.i(data, "offset")) mList.clear()
                    mList.addAll(JU.al(data, "datas"))
                    rvProject?.adapter?.notifyDataSetChanged()
                }
            })
    }
}
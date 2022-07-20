package com.frame.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ObjectUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.frame.R
import com.frame.common.CommonData
import com.frame.dataclass.DataClass
import com.frame.observers.ProgressObserver
import com.frame.utils.CU
import com.frame.utils.JU
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.common_layout_srl_rv.*
import java.util.*

/**
 */
class KnowledgeHierarchyActivity : BaseTitleActivity() {

    private val mList: MutableList<LinkedTreeMap<String, Any>> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.common_layout_srl_rv)
        initControl()
    }

    private fun initControl() {
        setTitleText("知识体系")
        recyclerView?.layoutManager = LinearLayoutManager(mBActivity)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.adapter = getAdapter()
        setSmartRefreshLayout()
    }

    override fun onResume() {
        super.onResume()
        if (ObjectUtils.isEmpty(mList)) getNetData()
    }

    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.llGotoTop -> recyclerView?.smoothScrollToPosition(0)
        }
    }

    private fun setSmartRefreshLayout() {
        refreshLayout?.setOnRefreshListener { getNetData() }
    }

    private fun getAdapter(): BaseQuickAdapter<*, *> {
        return object : BaseQuickAdapter<LinkedTreeMap<String, Any>, BaseViewHolder>(R.layout.item_knowledge_hierarchy, mList) {
            override fun convert(holder: BaseViewHolder, map: LinkedTreeMap<String, Any>) {
                holder.setText(R.id.tvKnowledgeHierarchyTitle, JU.s(map, "name"))
                    .setTextColor(R.id.tvKnowledgeHierarchyTitle, CU.randomColor())
                val content = StringBuilder()
                val list = JU.al<List<LinkedTreeMap<String, Any>>>(map, "children")
                for (ltm in list) {
                    content.append(JU.s(ltm, "name")).append("   ")
                }
                holder.setText(R.id.tvKnowledgeHierarchyContent, content)
                holder.itemView.setOnClickListener { view: View? ->
                    ActivityUtils.startActivity(Intent(mBActivity, WxArticleActivity::class.java)
                        .putExtra(CommonData.PARAM1, map))
                }
            }
        }
    }

    private fun getNetData() {
        doCommonGet("tree/json", null, object : ProgressObserver<DataClass>(mBActivity, true, refreshLayout) {
            override fun onNext(dc: DataClass) {
                mList.clear()
                mList.addAll(JU.al(dc.obj, "data"))
                recyclerView?.adapter?.notifyDataSetChanged()
            }
        })
    }
}
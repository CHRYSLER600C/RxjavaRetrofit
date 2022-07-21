package com.frame.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ObjectUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.frame.R
import com.frame.common.CommonData
import com.frame.dataclass.DataClass
import com.frame.observers.ProgressObserver
import com.frame.utils.CU
import com.frame.utils.JU
import com.frame.utils.LU
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.common_layout_srl_rv.*
import java.util.*

/**
 */
class KnowledgeHierarchyActivity : BaseTitleActivity() {

    private var mQuickAdapter: BaseQuickAdapter<LinkedTreeMap<String, Any>, BaseViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.common_layout_srl_rv)
        initControl()
    }

    private fun initControl() {
        setTitleText("知识体系")
        recyclerView?.run {
            layoutManager = LinearLayoutManager(mBActivity)
            setHasFixedSize(true)
            adapter = getQuickAdapter().also { mQuickAdapter = it }
        }
        setSmartRefreshLayout()
    }

    override fun onResume() {
        super.onResume()
        if (ObjectUtils.isEmpty(mQuickAdapter?.data)) getNetData()
    }

    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.ivGotoTop -> recyclerView?.smoothScrollToPosition(0)
        }
    }

    private fun setSmartRefreshLayout() {
        refreshLayout?.setOnRefreshListener { getNetData() }
    }

    private fun getQuickAdapter(): BaseQuickAdapter<LinkedTreeMap<String, Any>, BaseViewHolder> {
        return object : BaseQuickAdapter<LinkedTreeMap<String, Any>, BaseViewHolder>(R.layout.item_knowledge_hierarchy) {
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
                    LU.gotoActivityAnim(view, Intent(mBActivity, WxArticleActivity::class.java)
                        .putExtra(CommonData.PARAM1, map))
                }
            }
        }
    }

    private fun getNetData() {
        doCommonGet("tree/json", null, object : ProgressObserver<DataClass>(mBActivity, true, refreshLayout) {
            override fun onNext(dc: DataClass) {
                mQuickAdapter?.setList(JU.al<ArrayList<LinkedTreeMap<String, Any>>>(dc.obj, "data"))
            }
        })
    }
}
package com.frame.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ObjectUtils
import com.frame.R
import com.frame.common.CommonData
import com.frame.dataclass.DataClass
import com.frame.observers.ProgressObserver
import com.frame.utils.CU
import com.frame.utils.JU
import com.google.gson.internal.LinkedTreeMap
import com.scwang.smartrefresh.layout.api.RefreshLayout
import kotlinx.android.synthetic.main.common_layout_srl_rv.*
import org.byteam.superadapter.SuperAdapter
import org.byteam.superadapter.SuperViewHolder
import java.util.*

/**
 */
class KnowledgeHierarchyActivity : BaseTitleActivity() {

    private var mSuperAdapter: SuperAdapter<*>? = null
    private val mList: MutableList<LinkedTreeMap<String, Any>?> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.common_layout_srl_rv)
        initControl()
    }

    private fun initControl() {
        setTitleText("知识体系")
        recyclerView?.layoutManager = LinearLayoutManager(mBActivity)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.adapter = getSuperAdapter().also { mSuperAdapter = it }
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
        refreshLayout?.setOnRefreshListener { refreshLayout: RefreshLayout? -> getNetData() }
    }

    private fun getSuperAdapter(): SuperAdapter<*> {
        return object : SuperAdapter<LinkedTreeMap<String, Any>>(mBActivity, mList, R.layout.item_knowledge_hierarchy) {
            override fun onBind(holder: SuperViewHolder, viewType: Int, layoutPosition: Int, map: LinkedTreeMap<String, Any>) {
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
                mSuperAdapter?.notifyDataSetChanged()
            }
        })
    }
}
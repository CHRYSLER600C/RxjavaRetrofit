package com.frame.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ActivityUtils
import com.frame.R
import com.frame.activity.ExampleActivity
import com.frame.dataclass.bean.Template
import com.frame.utils.CU
import com.frame.utils.LU
import kotlinx.android.synthetic.main.fragment_tab4.*
import org.byteam.superadapter.SuperAdapter
import org.byteam.superadapter.SuperViewHolder
import java.util.*

/**
 * 我的
 */
class Tab4Fragment : BaseTitleFragment() {

    override fun setContentView(savedInstanceState: Bundle?): View {
        return View.inflate(mBActivity, R.layout.fragment_tab4, null)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun initControl() {
        setLeftBarHide()
        setTitleText("我的")

        rvTab4.layoutManager = LinearLayoutManager(mBActivity)
        rvTab4.adapter = getSuperAdapter(mBActivity, LU.getListTab4())
        setSmartRefreshLayout()
    }

    private fun setSmartRefreshLayout() {
        srlTab4.setEnableRefresh(false)
        srlTab4.setEnableLoadMore(false)
        srlTab4.setOnRefreshListener { refreshLayout -> }
    }

    private fun getSuperAdapter(activity: Activity?, list: List<Template>): SuperAdapter<*> {
        return object : SuperAdapter<Template>(activity, list, R.layout.fg_item_text_text) {
            override fun onBind(h: SuperViewHolder, viewType: Int, layoutPosition: Int, item: Template) {
                CU.setTVDrawableLeft(h.findViewById(R.id.tvItemContent), item.resId, 25, 25, 15)
                CU.setTVDrawableRight(h.findViewById(R.id.tvItemMsg), R.drawable.ic_arrow_right, 10, 16, 20)
                h.setText(R.id.tvItemContent, item.content)
                h.setText(R.id.tvItemMsg, item.content)
                h.itemView.setOnClickListener { v: View? ->
                    if (item.cls == ExampleActivity::class.java) {
                        ActivityUtils.startActivity(Intent(mBActivity, item.cls).putExtra("id", ""))
                    } else {
                        ActivityUtils.startActivity(Intent(mBActivity, item.cls)) // 不需要特殊处理的
                    }
                }
            }
        }
    }
}
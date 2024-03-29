package com.frame.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ObjectUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.frame.R
import com.frame.dataclass.DataClass
import com.frame.observers.ProgressObserver
import com.frame.utils.CU
import com.frame.utils.JU
import com.frame.utils.LU
import com.google.gson.internal.LinkedTreeMap
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import com.zhy.view.flowlayout.TagFlowLayout
import kotlinx.android.synthetic.main.activity_navigation.*
import q.rorbin.verticaltablayout.VerticalTabLayout.OnTabSelectedListener
import q.rorbin.verticaltablayout.adapter.TabAdapter
import q.rorbin.verticaltablayout.widget.ITabView.*
import q.rorbin.verticaltablayout.widget.TabView
import java.util.*


/**
 *
 */
class NavigationActivity : BaseTitleActivity() {

    private var mManager: LinearLayoutManager? = null
    private val mList: MutableList<LinkedTreeMap<String, Any>> = ArrayList()
    private var needScroll = false
    private var index = 0
    private var isClickTab = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        initControl()
    }

    private fun initControl() {
        setTitleText("导航")
        rvNavigation?.run {
            layoutManager = LinearLayoutManager(mBActivity).also { mManager = it }
            setHasFixedSize(true)
            adapter = getQuickAdapter()
        }
    }

    override fun onResume() {
        super.onResume()
        if (ObjectUtils.isEmpty(mList)) getNetData()
    }

    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.ivGotoTop -> {
                rvNavigation?.smoothScrollToPosition(0)
                vtlNavigation?.setTabSelected(0)
            }
        }
    }

    /**
     * Left tabLayout and right recyclerView linkage
     */
    private fun leftRightLinkage() {
        rvNavigation?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (needScroll && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    scrollRecyclerView()
                }
                rightLinkageLeft(newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (needScroll) {
                    scrollRecyclerView()
                }
            }
        })
        vtlNavigation?.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tabView: TabView, i: Int) {
                isClickTab = true
                index = i
                rvNavigation?.stopScroll()
                smoothScrollToPosition(i)
            }

            override fun onTabReselected(tabView: TabView, i: Int) {}
        })
    }

    private fun scrollRecyclerView() {
        needScroll = false
        val indexDistance = index - mManager!!.findFirstVisibleItemPosition()
        if (indexDistance >= 0 && indexDistance < rvNavigation!!.childCount) {
            val top = rvNavigation?.getChildAt(indexDistance)?.top ?: 0
            rvNavigation?.smoothScrollBy(0, top)
        }
    }

    /**
     * Right recyclerView linkage left tabLayout
     * SCROLL_STATE_IDLE just call once
     *
     * @param newState RecyclerView new scroll state
     */
    private fun rightLinkageLeft(newState: Int) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            if (isClickTab) {
                isClickTab = false
                return
            }
            val firstPosition = mManager?.findFirstVisibleItemPosition() ?: 0
            if (index != firstPosition) {
                index = firstPosition
                setChecked(index)
            }
        }
    }

    /**
     * Smooth right to select the position of the left tab
     *
     * @param position checked position
     */
    private fun setChecked(position: Int) {
        if (isClickTab) {
            isClickTab = false
        } else {
            if (vtlNavigation == null) return
            vtlNavigation?.setTabSelected(index)
        }
        index = position
    }

    private fun smoothScrollToPosition(currentPosition: Int) {
        val firstPosition = mManager?.findFirstVisibleItemPosition() ?: 0
        val lastPosition = mManager?.findLastVisibleItemPosition() ?: 0
        when {
            currentPosition <= firstPosition -> {
                rvNavigation?.smoothScrollToPosition(currentPosition)
            }
            currentPosition <= lastPosition -> {
                val top = rvNavigation?.getChildAt(currentPosition - firstPosition)?.top ?: 0
                rvNavigation?.smoothScrollBy(0, top)
            }
            else -> {
                rvNavigation?.smoothScrollToPosition(currentPosition)
                needScroll = true
            }
        }
    }

    private fun getQuickAdapter(): BaseQuickAdapter<LinkedTreeMap<String, Any>, BaseViewHolder> {
        return object : BaseQuickAdapter<LinkedTreeMap<String, Any>, BaseViewHolder>(R.layout.item_navigation, mList) {
            override fun convert(h: BaseViewHolder, map: LinkedTreeMap<String, Any>) {
                h.setText(R.id.tvNavigationTitle, JU.s(map, "name"))
                val tagFlowLayout = h.getView<TagFlowLayout>(R.id.tflNavigation)
                val list = JU.al<List<LinkedTreeMap<String, Any>>>(map, "articles")
                tagFlowLayout.adapter = object : TagAdapter<LinkedTreeMap<String, Any>>(list) {
                    override fun getView(parent: FlowLayout, position: Int, map2: LinkedTreeMap<String, Any>): View {
                        val tv = LayoutInflater.from(parent.context).inflate(R.layout.flow_layout_tv, tagFlowLayout, false) as TextView
                        tv.setPadding(ConvertUtils.dp2px(10f), ConvertUtils.dp2px(6f), ConvertUtils.dp2px(10f), ConvertUtils.dp2px(6f))
                        tv.text = JU.s(map2, "title")
                        tv.setTextColor(CU.randomColor())
                        return tv
                    }
                }
                tagFlowLayout.setOnTagClickListener { view: View, position1: Int, _: FlowLayout? ->
                    LU.gotoActivityAnim(view, Intent(mBActivity, WebViewActivity::class.java)
                        .putExtra(WebViewActivity.TYPE, WebViewActivity.TYPE_LOAD_URL)
                        .putExtra("title", JU.s(list[position1], "title"))
                        .putExtra("url", JU.s(list[position1], "link")))
                    true
                }
            }
        }
    }

    private fun getNetData() {
        doCommonGet("navi/json", null, object : ProgressObserver<DataClass>(this, true) {
            override fun onNext(dc: DataClass) {
                mList.addAll(JU.al(dc.obj, "data"))
                rvNavigation?.adapter?.notifyDataSetChanged()
                vtlNavigation?.setTabAdapter(object : TabAdapter {
                    override fun getCount(): Int {
                        return mList.size
                    }

                    override fun getBadge(i: Int): TabBadge? {
                        return null
                    }

                    override fun getIcon(i: Int): TabIcon? {
                        return null
                    }

                    override fun getTitle(i: Int): TabTitle {
                        return TabTitle.Builder()
                            .setContent(JU.s(mList!![i], "name"))
                            .setTextColor(ContextCompat.getColor(mBActivity, R.color.shallow_green),
                                ContextCompat.getColor(mBActivity, R.color.shallow_grey))
                            .build()
                    }

                    override fun getBackground(i: Int): Int {
                        return -1
                    }
                })
                leftRightLinkage()
            }
        })
    }
}
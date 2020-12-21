package com.frame.activity

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ObjectUtils
import com.frame.R
import com.frame.core.dao.HistoryData
import com.frame.core.db.DbHelper
import com.frame.dataclass.DataClass
import com.frame.observers.ProgressObserver
import com.frame.observers.RecycleObserver
import com.frame.utils.CU
import com.frame.utils.JU
import com.frame.view.dialog.CommonDialog
import com.google.gson.internal.LinkedTreeMap
import com.jakewharton.rxbinding2.widget.RxTextView
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import kotlinx.android.synthetic.main.activity_search.*
import java.util.*

/**
 *
 */
class SearchActivity : BaseTitleActivity() {

    private val mDbHelper = DbHelper.getInstance()
    private var mList: List<LinkedTreeMap<String, Any>?>? = null
    private val mHistoryList: MutableList<HistoryData?> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initControl()
    }

    private fun initControl() {
        setTitleBarHide()
        add2Disposable(RxTextView.textChanges(etSearch!!)
                .map { charSequence: CharSequence -> charSequence.toString() }
                .subscribeWith(object : RecycleObserver<CharSequence?>() {
                    override fun onNext(s: CharSequence) {
                        tvSearch?.isEnabled = ObjectUtils.isNotEmpty(s)
                        tvSearch?.setBackgroundResource(
                                if (ObjectUtils.isNotEmpty(s)) R.drawable.selector_lightred else R.drawable.shape_gray)
                    }
                }))
        etSearch?.setOnKeyListener { _: View?, keyCode: Int, event: KeyEvent ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) startSearch()
            false
        }
        setSearchHistoryAdapter(mHistoryList)
        refreshHistoryData(null, false)
        etSearch?.postDelayed({ KeyboardUtils.showSoftInput(etSearch!!) }, 100) //弹出输入法
    }

    override fun initImmersionBar() {
        CU.setImmersionBar(mBActivity, true)
    }

    override fun getStatusBarBgColor(): Int {
        return R.color.white
    }

    override fun onResume() {
        super.onResume()
        if (ObjectUtils.isEmpty(mList)) getNetData()
    }

    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.llGoBack -> finish()
            R.id.ivClearAll -> {
                val builder = CommonDialog.Builder(mBActivity, CommonDialog.DialogType.TYPE_SIMPLE)
                        .setTitle("提示")
                        .setMessage("确定清空搜索历史吗？")
                        .setOkBtn("确定") { _: View?, _: String? -> refreshHistoryData(null, true) }
                        .setCancelBtn("取消", null)
                builder.create().show()
            }
            R.id.tvSearch -> startSearch()
        }
    }

    private fun startSearch() {
        val key = etSearch?.text.toString()
        refreshHistoryData(mDbHelper.addHistoryData(key), false)
        ActivityUtils.startActivity(Intent(mBActivity, SearchListActivity::class.java)
                .putExtra("key", key))
    }

    private fun refreshHistoryData(list: List<HistoryData?>?, isClear: Boolean) {
        mHistoryList.clear()
        if (isClear) {
            mDbHelper.clearHistoryData()
        } else {
            mHistoryList.addAll(if (ObjectUtils.isEmpty(list)) mDbHelper.loadAllHistoryData() else list!!)
            mHistoryList.reverse()
        }
        tflSearchHistory?.adapter?.notifyDataChanged()
        tvNullHint?.visibility = if (mHistoryList.size > 0) View.GONE else View.VISIBLE
    }

    fun setSearchHistoryAdapter(list: List<HistoryData?>) {
        tflSearchHistory?.adapter = object : TagAdapter<HistoryData>(list) {
            override fun getView(parent: FlowLayout, position: Int, item: HistoryData): View {
                val tv = LayoutInflater.from(mBActivity).inflate(R.layout.flow_layout_tv, parent, false) as TextView
                tv.setPadding(ConvertUtils.dp2px(12f), ConvertUtils.dp2px(6f), ConvertUtils.dp2px(12f), ConvertUtils.dp2px(6f))
                tv.text = item.data
                tv.setTextColor(CU.randomColor())
                return tv
            }
        }
        tflSearchHistory?.setOnTagClickListener { _: View?, position1: Int, _: FlowLayout? ->
            ActivityUtils.startActivity(Intent(mBActivity, SearchListActivity::class.java)
                    .putExtra("key", list[position1]?.data))
            true
        }
    }

    private fun getNetData() {
        doCommonGet<DataClass>("hotkey/json", null, object : ProgressObserver<DataClass?>(this, true) {
            override fun onNext(dc: DataClass) {
                mList = JU.al<List<LinkedTreeMap<String, Any>?>>(dc.`object`, "data")
                tflSearch?.adapter = object : TagAdapter<LinkedTreeMap<String, Any>?>(mList) {
                    override fun getView(parent: FlowLayout, position: Int, map: LinkedTreeMap<String, Any>?): View {
                        val tv = LayoutInflater.from(mBActivity).inflate(R.layout.flow_layout_tv, parent, false) as TextView
                        tv.text = JU.s(map, "name")
                        tv.setBackgroundColor(CU.randomTagColor())
                        tv.setTextColor(resources.getColor(R.color.white))
                        return tv
                    }
                }
                tflSearch?.setOnTagClickListener { _: View?, position1: Int, _: FlowLayout? ->
                    ActivityUtils.startActivity(Intent(mBActivity, SearchListActivity::class.java)
                            .putExtra("key", JU.s(mList?.get(position1), "name")))
                    true
                }
            }
        })
    }
}
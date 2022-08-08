package com.frame.adapter

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.view.Gravity
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ObjectUtils
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.frame.R
import com.frame.activity.BaseActivity
import com.frame.dataclass.bean.GotoMI
import com.frame.dataclass.bean.NameValue
import com.frame.dataclass.bean.PickerItem
import com.frame.dataclass.bean.PickerValue
import com.frame.httputils.other.AdapterClickListener
import com.frame.utils.CU
import com.frame.utils.gone
import com.frame.view.dialog.PickerDialog
import java.util.*

/**
 * 通用跳转型适配器
 */
class TestMultiQuickAdapter(
    private val activity: BaseActivity,
    private var mSubmitMap: MutableMap<String, Any?>?, //编辑的数据，以及修改后提交
    initList: MutableList<GotoMI>? = null
) : BaseMultiItemQuickAdapter<GotoMI, BaseViewHolder>(initList) {

    private var mAdapterClickListener: AdapterClickListener<GotoMI, BaseViewHolder>? = null

    init {
        // 绑定 layout 对应的 type
        addItemType(GotoMI.TIME_PICKER, R.layout.multi_item_time_picker)
        addItemType(GotoMI.MULTI_PICKER, R.layout.multi_item_multi_picker)
        addItemType(GotoMI.IMAGE, R.layout.multi_item_text_text)
        addItemType(GotoMI.SPACE, R.layout.common_multi_item_space)
    }

    override fun convert(holder: BaseViewHolder, item: GotoMI) {
        // 根据返回的 type 分别设置数据
        when (holder.itemViewType) {
            GotoMI.TIME_PICKER -> {
                holder.getView<TextView>(R.id.tvSelectTimeName).run {
                    CU.setTVDrawableLeft(this, item.resId, 20, 20, 10)
                    text = item.content
                }
                holder.getView<TextView>(R.id.tvSelectTimeContent).run {
                    setOnClickListener {
                        val str: Array<String> = text?.toString()?.split("-")?.toTypedArray() ?: return@setOnClickListener

                        val yearInt: Int
                        val monthInt: Int
                        val dayInt: Int
                        if (str.size == 3) {
                            yearInt = str[0].toInt()
                            monthInt = str[1].toInt() - 1
                            dayInt = str[2].toInt()
                        } else {
                            val c = Calendar.getInstance()
                            yearInt = c[Calendar.YEAR]
                            monthInt = c[Calendar.MONTH]
                            dayInt = c[Calendar.DATE]
                        }

                        val datePickerDialog = DatePickerDialog(activity, { _: DatePicker?, year: Int, month: Int, day: Int ->
                            text = String.format("%02d-%02d-%02d", year, month + 1, day)
                        }, yearInt, monthInt, dayInt)

                        val window = datePickerDialog.window
                        window.setGravity(Gravity.BOTTOM) // 此处可以设置dialog显示的位置
                        window.setWindowAnimations(R.style.AnimationBottomInOut) // 添加动画
                        datePickerDialog.show()
                    }
                }
            }
            GotoMI.MULTI_PICKER -> {
                holder.getView<TextView>(R.id.tvSelectName).run {
                    CU.setTVDrawableLeft(this, item.resId, 20, 20, 10)
                    text = item.content
                }

                val pickerItem = item.data as? PickerItem
                if (pickerItem != null) {
                    holder.setText(R.id.tvSelectContent1, pickerItem.nv1.name)
                    if (pickerItem.nv2 == null) {
                        holder.getView<TextView>(R.id.tvSelectContent2).gone()
                    } else {
                        holder.setText(R.id.tvSelectContent2, pickerItem.nv2.name)
                    }
                    if (pickerItem.nv3 == null) {
                        holder.getView<TextView>(R.id.tvSelectContent3).gone()
                    } else {
                        holder.setText(R.id.tvSelectContent3, pickerItem.nv3.name)
                    }
                }

                val pickerValue = item.extend as PickerValue
                val pickerBuilder = PickerDialog.Builder(activity)
                pickerBuilder.setBtnOk(pickerValue, "bottom") { nv1: NameValue, nv2: NameValue, nv3: NameValue ->
                    holder.setText(R.id.tvSelectContent1, nv1.name)
                    holder.setText(R.id.tvSelectContent2, nv2.name)
                    holder.setText(R.id.tvSelectContent3, nv3.name)
                }.setPickedData(pickerItem)

                holder.getView<LinearLayout>(R.id.llSelectContent).setOnClickListener {
                    if (pickerValue.list1.size == 0) activity.showShort("暂无数据")
                    else CU.showAnimatDialog(pickerBuilder.create())
                }
            }
            GotoMI.IMAGE -> {
                holder.getView<TextView>(R.id.tvItemContent).run {
                    CU.setTVDrawableLeft(this, item.resId, 20, 20, 10)
                    text = item.content
                }

                holder.getView<TextView>(R.id.tvItemMsg).run {
                    val resId = if (item.cls != null && ObjectUtils.isEmpty(item.data)) R.drawable.ic_arrow_right else -1
                    CU.setTVDrawableRight(this, resId, 8, 12, 10)
                    if (item.data is String) text = item.data.toString()
                }

                holder.itemView.setOnClickListener {
                    if (mAdapterClickListener != null) {
                        mAdapterClickListener?.onClick(it, holder, item)
                    } else (item.cls as? Class<out Activity>)?.run {
                        ActivityUtils.startActivity(Intent(activity, this)) // 不需要特殊处理的
                    }
                }
            }
        }
    }

    fun setAdapterClickListener(listener: AdapterClickListener<GotoMI, BaseViewHolder>?): TestMultiQuickAdapter {
        mAdapterClickListener = listener
        return this
    }

}
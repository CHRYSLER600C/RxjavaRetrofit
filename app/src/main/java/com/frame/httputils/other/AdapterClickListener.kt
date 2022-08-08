package com.frame.httputils.other

import android.view.View
import com.chad.library.adapter.base.viewholder.BaseViewHolder


interface AdapterClickListener<T, VH : BaseViewHolder> {
    fun onClick(view: View, holder: VH, item: T)

}
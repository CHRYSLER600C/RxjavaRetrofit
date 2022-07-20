package com.frame.utils

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.frame.R

/**
 * 逻辑处理类
 */

object KLU {

    fun gotoActivityAnim(activity: Activity, intent: Intent, view: View?) {
        if (!Build.MANUFACTURER.contains("samsung") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val options = ActivityOptions.makeSceneTransitionAnimation(activity, view, activity.getString(R.string.share_view))
            ActivityUtils.startActivity(intent, options.toBundle())
        } else ActivityUtils.startActivity(intent)
    }
    fun gotoActivityAnim(activity: Activity, clz: Class<out Activity>, view: View?) {
        gotoActivityAnim(activity, Intent(activity, clz), view)
    }
}
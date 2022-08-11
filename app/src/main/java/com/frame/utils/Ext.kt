package com.frame.utils

import android.graphics.Rect
import android.view.View
import android.widget.EditText


/**
 * ***********************************************
 * 各种扩展函数
 * ***********************************************
 */


/**
 * Set view visible
 */
fun View.visible() {
    visibility = View.VISIBLE
}

/**
 * Set view invisible
 */
fun View.invisible() {
    visibility = View.INVISIBLE
}

/**
 * Set view gone
 */
fun View.gone() {
    visibility = View.GONE
}

/**
 * Reverse the view's visibility
 */
fun View.reverseVisibility(needGone: Boolean = true) {
    if (isVisible) {
        if (needGone) gone() else invisible()
    } else visible()
}

var View.isVisible: Boolean
    get() = visibility == View.VISIBLE
    set(value) = if (value) visible() else gone()

var View.isInvisible: Boolean
    get() = visibility == View.INVISIBLE
    set(value) = if (value) invisible() else visible()

var View.isGone: Boolean
    get() = visibility == View.GONE
    set(value) = if (value) gone() else visible()


/**
 * Causes the Runnable which contains action() to be added to the message queue, to be run
 * after the specified amount of time elapses.
 * The runnable will be run on the user interface thread
 *
 * @param action Will be invoked in the Runnable
 * @param delayInMillis The delay (in milliseconds) until the action() will be invoked
 */
inline fun View.postDelayed(delayInMillis: Long, crossinline action: () -> Unit): Runnable {
    val runnable = Runnable { action() }
    postDelayed(runnable, delayInMillis)
    return runnable
}

/**
 * 检测制定View是否被遮住显示不全
 * @return
 */
fun View.isCover(): Boolean {
    val rect = Rect()
    val cover = this.getGlobalVisibleRect(rect)
    if (cover) {
        if (rect.width() >= this.measuredWidth && rect.height() >= this.measuredHeight) {
            return !cover
        }
    }
    return true
}

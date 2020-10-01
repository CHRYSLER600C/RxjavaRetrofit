package com.frame.utils

import android.util.Log

import com.frame.common.CommonData


/**
 * 日志打印
 */

const val isPrintLog = CommonData.DEBUG

fun logi(msg: String) {
    if (isPrintLog) println(getThreadName(msg))
}

fun logi(tag: String, msg: String) {
    if (isPrintLog) Log.i(tag, getThreadName(msg))
}

fun logv(tag: String, msg: String) {
    if (isPrintLog) Log.v(tag, getThreadName(msg))
}

fun logd(tag: String, msg: String) {
    if (isPrintLog) Log.d(tag, getThreadName(msg))
}

fun logw(tag: String, msg: String) {
    if (isPrintLog) Log.w(tag, getThreadName(msg))
}

fun loge(tag: String, msg: String) {
    if (isPrintLog) Log.e(tag, getThreadName(msg))
}

fun loge(tag: String, msg: String, tr: Throwable) {
    if (isPrintLog) Log.e(tag, msg, tr)
}

fun loge(msg: String) {
    if (isPrintLog) Log.e("ifmvo", getThreadName(msg))
}

fun logm(tag: String, msg: String) { // 带方法名
    Log.v(tag, Exception().stackTrace[1].methodName + ": " + getThreadName(msg))
}

fun getThreadName(msg: String): String {
    return "[" + Thread.currentThread().name + "]:" + msg
}

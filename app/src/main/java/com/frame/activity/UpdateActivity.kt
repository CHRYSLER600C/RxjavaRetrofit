package com.frame.activity

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.View
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ObjectUtils
import com.frame.R
import com.frame.common.CommonData
import com.frame.httputils.OkHttpUtil2.IRequestFileCallback
import com.frame.utils.gone
import com.frame.utils.visible
import kotlinx.android.synthetic.main.activity_update.*
import java.io.IOException

/**
 * 应用升级模块,需要获取intent传递过来的updateUrl参数用以下载
 */
class UpdateActivity : BaseTitleActivity() {

    private var mUpdateURL: String? = null
    private val mHandler = Handler { msg ->
        when (msg.what) {
            REFRESH_PROGRESS -> {
                tvNotifyUpdate?.text = "正在下载文件, 请稍候..."
                btnReDownLoad?.gone()
                pbDownload?.progress = (msg.obj as Int)
            }
            DOWNLOAD_ERROR -> {
                tvNotifyUpdate?.text = "下载失败,请重试"
                btnReDownLoad?.visible()
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)
        initControl()
        downLoadAPK(mUpdateURL)
    }

    private fun initControl() {
        setTitleBgColor(Color.parseColor("#00000000"))
        mUpdateURL = intent.getStringExtra("updateUrl")
        if (TextUtils.isEmpty(mUpdateURL)) finish()
    }

    private fun downLoadAPK(url: String?) {
        downLoadFile(url, CommonData.FILE_DIR_SD, "$packageName.apk", object : IRequestFileCallback {
            override fun <T> ObjResponse(isSuccess: Boolean, path: T, e: IOException) {
                if (!isSuccess || ObjectUtils.isEmpty(path)) {
                    mHandler.sendEmptyMessage(DOWNLOAD_ERROR)
                    return
                }
                AppUtils.installApp(path as String)
                finish()
            }

            override fun ProgressResponse(progress: Long, total: Long) {
                val progressStep = progress * 100 / total
                mHandler.sendMessage(Message.obtain(mHandler, REFRESH_PROGRESS, progressStep.toInt()))
            }
        })
    }

    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.btnReDownLoad -> downLoadAPK(mUpdateURL)
        }
    }

    companion object {
        private const val REFRESH_PROGRESS = 0x0000
        private const val DOWNLOAD_ERROR = 0x0001
    }
}
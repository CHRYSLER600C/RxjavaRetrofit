package com.frame.view.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import com.frame.R
import com.frame.common.CommonData
import kotlinx.android.synthetic.main.dialog_photo.*
import java.io.File

/**
 * 图片选择dialog
 */
class PhotoDialog(var mContext: Activity) : Dialog(mContext, R.style.PhotoDialog), View.OnClickListener {

    var mStoreFile: String = ""

    init {
        // android 7.0系统解决拍照的问题
        if (Build.VERSION.SDK_INT > 17) {
            val builder = VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
            builder.detectFileUriExposure()
        }
    }

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_photo)
        setCanceledOnTouchOutside(true)
        btnCloseDlg?.setOnClickListener(this)
        btnCameraPick?.setOnClickListener(this)
        btnGalleryPick?.setOnClickListener(this)
    }

    fun setCaptureFile(storeFile: String) {
        mStoreFile = storeFile
    }

    fun getCaptureFilePath(): String = if (TextUtils.isEmpty(mStoreFile)) mFilePath else mStoreFile

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnCloseDlg -> dismiss()
            R.id.btnCameraPick -> {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (TextUtils.isEmpty(mStoreFile)) {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(File(mFilePath)))
                } else {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(File(mStoreFile)))
                }
                mContext.startActivityForResult(cameraIntent, CommonData.PHOTO_CAMERA)
                dismiss()
            }
            R.id.btnGalleryPick -> {
                val galleryIntent = Intent(Intent.ACTION_PICK, null)
                galleryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    CommonData.IMAGE_UNSPECIFIED)
                mContext.startActivityForResult(galleryIntent, CommonData.PHOTO_GALLERY)
                dismiss()
            }
            else -> {
            }
        }
    }

    companion object {
        var mFilePath = CommonData.IMAGE_DIR_SD + "temp.jpg" // 图片存储路径

        /**
         * 拍照后裁剪
         *
         * @param input  原始图片
         * @param output 裁剪后图片
         */
        fun cropImage(activity: Activity, input: Uri?, output: Uri?) {
            val intentCamera = Intent("com.android.camera.action.CROP")
            intentCamera.setDataAndType(input, "image/*") // 源文件地址
            intentCamera.putExtra("crop", true)
            // intentCamera.putExtra("scale", false);
            // intentCamera.putExtra("noFaceDetection", true);//不需要人脸识别功能
            // intentCamera.putExtra("circleCrop", true);//设定此方法选定区域会是圆形区域
            // aspectX aspectY是宽高比例
            intentCamera.putExtra("aspectX", 1)
            intentCamera.putExtra("aspectY", 1)
            // outputX outputY 是裁剪图片的宽高
            intentCamera.putExtra("outputX", 1000)
            intentCamera.putExtra("outputY", 1000)
            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, output) // 输出地址
            intentCamera.putExtra("return-data", false)
            activity.startActivityForResult(intentCamera, CommonData.PHOTO_CROP)
        }
    }
}
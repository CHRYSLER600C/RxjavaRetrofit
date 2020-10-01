package com.frame.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.frame.R;
import com.frame.common.CommonData;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 图片选择dialog
 */
public class PhotoDialog extends Dialog implements View.OnClickListener {

    @BindView(R.id.btnCloseDlg)
    Button mBtnCloseDlg;
    @BindView(R.id.btnCameraPick)
    Button mBtnCameraPick;
    @BindView(R.id.btnGalleryPick)
    Button mBtnGalleryPick;

    protected Activity mContext;
    public static String mFilePath = CommonData.IMAGE_DIR_SD + "temp.jpg";// 图片存储路径
    public String mStoreFile;

    public PhotoDialog(Activity context) {
        super(context, R.style.PhotoDialog);
        this.mContext = context;

        // android 7.0系统解决拍照的问题
        if (Build.VERSION.SDK_INT > 17) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_photo);
        ButterKnife.bind(this);

        setCanceledOnTouchOutside(true);
        mBtnCloseDlg.setOnClickListener(this);
        mBtnCameraPick.setOnClickListener(this);
        mBtnGalleryPick.setOnClickListener(this);
    }

    public void setCaptureFile(String storeFile) {
        this.mStoreFile = storeFile;
    }

    public String getCaptureFilePath() {
        return TextUtils.isEmpty(mStoreFile) ? mFilePath : this.mStoreFile;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCloseDlg:
                dismiss();
                break;
            case R.id.btnCameraPick:
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (TextUtils.isEmpty(mStoreFile)) {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mFilePath)));
                } else {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mStoreFile)));
                }
                mContext.startActivityForResult(cameraIntent, CommonData.PHOTO_CAMERA);
                dismiss();
                break;
            case R.id.btnGalleryPick:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, null);
                galleryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        CommonData.IMAGE_UNSPECIFIED);
                mContext.startActivityForResult(galleryIntent, CommonData.PHOTO_GALLERY);
                dismiss();
                break;
            default:
                break;
        }
    }


    /**
     * 拍照后裁剪
     *
     * @param input  原始图片
     * @param output 裁剪后图片
     */
    public static void cropImage(Activity activity, Uri input, Uri output) {
        Intent intentCamera = new Intent("com.android.camera.action.CROP");
        intentCamera.setDataAndType(input, "image/*");// 源文件地址
        intentCamera.putExtra("crop", true);
        // intentCamera.putExtra("scale", false);
        // intentCamera.putExtra("noFaceDetection", true);//不需要人脸识别功能
        // intentCamera.putExtra("circleCrop", true);//设定此方法选定区域会是圆形区域
        // aspectX aspectY是宽高比例
        intentCamera.putExtra("aspectX", 1);
        intentCamera.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片的宽高
        intentCamera.putExtra("outputX", 1000);
        intentCamera.putExtra("outputY", 1000);
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, output);// 输出地址
        intentCamera.putExtra("return-data", false);
        activity.startActivityForResult(intentCamera, CommonData.PHOTO_CROP);
    }
}

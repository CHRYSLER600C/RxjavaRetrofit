package com.born.frame.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.born.frame.R;
import com.born.frame.common.CommonData;
import com.born.frame.utils.FileUtil;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 图片选择dialog
 */
public class PhotoDialog extends Dialog implements View.OnClickListener {

    @Bind(R.id.btnCloseDlg)
    Button mBtnCloseDlg;
    @Bind(R.id.btnCameraPick)
    Button mBtnCameraPick;
    @Bind(R.id.btnGalleryPick)
    Button mBtnGalleryPick;

    protected Activity mContext;
    public static String mFilePath = CommonData.IMAMGE_DIR_SD + "/";// 图片存储路径
    public String mStoreFile;

    public PhotoDialog(Activity context) {
        super(context, R.style.PhotoDialog);
        this.mContext = context;
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
        return this.mStoreFile;
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
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(new File(mFilePath, FileUtil.generateFileName() + ".jpg")));
                } else {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mStoreFile)));
                }
                mContext.startActivityForResult(cameraIntent, CommonData.PHOTO_CAMERA);
                dismiss();
                break;
            case R.id.btnGalleryPick:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, null);
                galleryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, CommonData.IMAGE_UNSPECIFIED);
                mContext.startActivityForResult(galleryIntent, CommonData.PHOTO_GALLERY);
                dismiss();
                break;
            default:
                break;
        }

    }
}

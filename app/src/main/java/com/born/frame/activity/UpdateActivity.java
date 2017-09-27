package com.born.frame.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.born.frame.R;
import com.born.frame.common.CommonData;
import com.born.frame.httputils.OkHttpUtil2.IRequestFileCallback;
import com.born.frame.utils.FileUtil;
import com.born.frame.utils.JudgeUtil;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 应用升级模块,需要获取intent传递过来的updateUrl参数用以下载
 */
public class UpdateActivity extends BaseTitleActivity {

    private static final int REFRESH_PROGRESS = 0x0000;
    private static final int DOWNLOAD_ERROR = 0x0001;

    @Bind(R.id.tvNotifyUpdate)
    TextView mTvNotifyUpdate;
    @Bind(R.id.btnReDownLoad)
    Button mBtnReDownLoad;
    @Bind(R.id.pbDownload)
    ProgressBar mPbDownload;

    private String mUpdateDir;
    private String mApkFileName;
    private String mUpdateURL;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_PROGRESS:
                    mTvNotifyUpdate.setText("正在下载文件, 请稍候...");
                    mBtnReDownLoad.setVisibility(View.GONE);
                    mPbDownload.setProgress((Integer) msg.obj);
                    break;
                case DOWNLOAD_ERROR:
                    mTvNotifyUpdate.setText("下载失败,请重试");
                    mBtnReDownLoad.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        getIntentParams();
        initControl();
        downLoadAPK(mUpdateURL);
    }

    private void getIntentParams() {
        mUpdateURL = getIntent().getStringExtra("updateUrl");
        if (TextUtils.isEmpty(mUpdateURL)) {
            finish();
        }
    }

    private void initControl() {
        setLeftBackClick();
        setTitleBgColor(Color.parseColor("#00000000"));
        createDownloadFile();
    }

    private void createDownloadFile() {
        mUpdateDir = FileUtil.hasSDCard() ? CommonData.APK_DIR_SD : CommonData.APK_DIR_RAM;
        String name = getString(R.string.app_name);
        if (TextUtils.isEmpty(name)) {
            name = FileUtil.generateFileName();
        }
        mApkFileName = name + ".apk";

        // 修改文件夹及安装包的权限,供第三方应用访问
        try {
            Runtime.getRuntime().exec("chmod 705 " + mUpdateDir);
            Runtime.getRuntime().exec("chmod 604 " + mUpdateDir + "/" + mApkFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downLoadAPK(String url) {

        downLoadFile(url, mUpdateDir, mApkFileName, new IRequestFileCallback() {

            @Override
            public void FileResponse(Boolean isSuccess, IOException e, String path) {
                if (!isSuccess || JudgeUtil.isEmpty(path)) {
                    mHandler.sendEmptyMessage(DOWNLOAD_ERROR);
                    return;
                }

                File file = new File(path);
                if (file != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void ProgressResponse(long progress, long total) {
                long progressStep = progress * 100 / total;
                mHandler.sendMessage(Message.obtain(mHandler, REFRESH_PROGRESS, (int) progressStep));
            }
        });

    }

    @OnClick({R.id.btnReDownLoad})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnReDownLoad:
                downLoadAPK(mUpdateURL);
                break;
        }
    }
}
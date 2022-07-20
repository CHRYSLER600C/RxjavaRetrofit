package com.frame.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.frame.R;
import com.frame.common.CommonData;
import com.frame.httputils.OkHttpUtil2.IRequestFileCallback;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 应用升级模块,需要获取intent传递过来的updateUrl参数用以下载
 */
public class UpdateActivity extends BaseTitleActivity {

    private static final int REFRESH_PROGRESS = 0x0000;
    private static final int DOWNLOAD_ERROR = 0x0001;

    @BindView(R.id.tvNotifyUpdate)
    TextView mTvNotifyUpdate;
    @BindView(R.id.btnReDownLoad)
    Button mBtnReDownLoad;
    @BindView(R.id.pbDownload)
    ProgressBar mPbDownload;

    private String mUpdateURL;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
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
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        initControl();
        downLoadAPK(mUpdateURL);
    }

    private void initControl() {
        setTitleBgColor(Color.parseColor("#00000000"));
        mUpdateURL = getIntent().getStringExtra("updateUrl");
        if (TextUtils.isEmpty(mUpdateURL)) {
            finish();
        }
    }

    private void downLoadAPK(String url) {
        downLoadFile(url, CommonData.FILE_DIR_SD, getPackageName() + ".apk", new IRequestFileCallback() {

            @Override
            public <T> void ObjResponse(Boolean isSuccess, T path, IOException e) {
                if (!isSuccess || ObjectUtils.isEmpty(path)) {
                    mHandler.sendEmptyMessage(DOWNLOAD_ERROR);
                    return;
                }
                AppUtils.installApp((String) path);
                finish();
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
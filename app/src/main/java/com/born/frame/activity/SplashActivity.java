package com.born.frame.activity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.born.frame.R;
import com.born.frame.dataclass.UpdateInfoDataClass;
import com.born.frame.dataclass.UpdateInfoDataClass.UpdateInfo;
import com.born.frame.subscribers.ProgressSubscriber;
import com.born.frame.utils.DeviceUtil;
import com.born.frame.view.dialog.SimpleDialog;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 启动页，此页还负责获取升级信息
 */
public class SplashActivity extends BaseActivity {

    @Bind(R.id.llContainer)
    LinearLayout mLlContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_ll);

        mLlContainer.setBackgroundResource(R.drawable.splash);
        checkUpdateTask();
    }

    private void checkUpdateTask() {
        Map<String, Object> map = new HashMap<>();
        map.put("verCode", "" + DeviceUtil.getVersionCode(mContext));
        map.put("verName", DeviceUtil.getVersionName(mContext));
        map.put("channelCode", DeviceUtil.getAppChanel(mContext));
        map.put("type", "ANDROID");
        doRequestImpl("getUpdateInfo", map, new ProgressSubscriber<UpdateInfoDataClass>(this, false) {

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                pauseAndEnter();
            }

            @Override
            public void onNext(UpdateInfoDataClass dataClass) {
                if (dataClass.updateInfo == null) {
                    pauseAndEnter();
                    return;
                }

                final UpdateInfo info = dataClass.updateInfo;
                SimpleDialog.Builder builder = new SimpleDialog.Builder(mContext)
                        .setTitle("有新版本")
                        .setMessage(info.updateInfo)
                        .setLeftBtn("立即升级", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                goToUpdate(info);
                            }
                        });

                if (DeviceUtil.getVersionCode(mContext) < info.forceUpdateCode) {// 强制更新
                    SimpleDialog dialog = builder.create();
                    dialog.setOnCancelListener(new OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            finish();
                        }
                    });
                    dialog.show();
                } else if (DeviceUtil.getVersionCode(mContext) < info.optionalUpdateCode) {// 可选更新
                    SimpleDialog dialog = builder
                            .setRightBtn("下次再说", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    goToGroup();
                                }
                            }).create();
                    dialog.setCancelable(false);
                    dialog.show();
                } else {
                    pauseAndEnter();
                }
            }
        });
    }

    private void goToGroup() {
        Intent intent = new Intent(this, GroupActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToUpdate(UpdateInfo info) {
        Intent intent = new Intent(this, UpdateActivity.class);
        intent.putExtra("updateUrl", info.updateUrl);
        startActivity(intent);
        finish();
    }

    private void pauseAndEnter() {
        Observable.timer(20, TimeUnit.MILLISECONDS)
                .compose(this.<Long>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        goToGroup();
                    }
                });
    }
}

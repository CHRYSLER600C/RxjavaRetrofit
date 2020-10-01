package com.frame.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.frame.R;
import com.frame.dataclass.DataClass;
import com.frame.httputils.OkHttpUtil;
import com.frame.observers.ProgressObserver;
import com.frame.utils.CU;
import com.frame.utils.JU;
import com.frame.view.dialog.CommonDialog;
import com.google.gson.internal.LinkedTreeMap;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 启动页，此页还负责获取升级信息
 */
public class SplashActivity extends BaseActivity {

    @BindView(R.id.llContainer)
    LinearLayout mLlContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_ll);

        mLlContainer.setBackgroundResource(R.drawable.splash);
        goToGroup();  //pauseAndEnter();  //
    }

    private void goToGroup() {
        ActivityUtils.startActivity(GroupActivity.class);
        finish();
    }

    private void goToUpdate(String updateUrl) {
        ActivityUtils.startActivity(new Intent(this, UpdateActivity.class)
                .putExtra("updateUrl", updateUrl));
        finish();
    }

    private void pauseAndEnter() { //zip操作符 避免网络请求很慢时还需要再等2秒钟
        Map<String, Object> map = new HashMap<>();
        map.put("verCode", "" + AppUtils.getAppVersionCode());
        map.put("verName", AppUtils.getAppVersionName());
        map.put("channelCode", CU.getAppChanel());
        map.put("type", "ANDROID");
        Observable<DataClass> observable = OkHttpUtil.getInstance().mRequestService
                .commonGet("https://www.yiqiyiqi.cn/app/appUpdateInfo.htm", map).subscribeOn(Schedulers.io());

        ProgressObserver<DataClass>  progressObserver = new ProgressObserver<DataClass>(mBActivity, true) {
            @Override
            public void onNext(DataClass dc) {
                final LinkedTreeMap<String, Object> map = JU.m(dc.object, "updateInfo");
                CommonDialog.Builder builder = new CommonDialog.Builder(mBActivity, CommonDialog.DialogType.TYPE_SIMPLE)
                        .setTitle("有新版本")
                        .setMessage(JU.s(map, "updateInfo"))
                        .setCancelBtn("立即升级", (View view) -> goToUpdate(JU.s(map, "updateUrl")));

                if (AppUtils.getAppVersionCode() < JU.d(map, "forceUpdateCode")) {// 强制更新
                    CommonDialog dialog = builder.create();
                    dialog.setOnCancelListener((DialogInterface dialogInterface) -> finish());
                    dialog.show();
                } else if (AppUtils.getAppVersionCode() < JU.d(map, "optionalUpdateCode")) {// 可选更新
                    CommonDialog dialog = builder.setOkBtn("下次再说", (View v, String value) -> goToGroup()).create();
                    dialog.setCancelable(false);
                    dialog.show();
                } else {
                    pauseAndEnter();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                goToGroup();
            }
        };

        add2Disposable(Observable.zip(Observable.timer(2000, TimeUnit.MILLISECONDS), observable,
                (Long aLong, DataClass dc) -> dc)
                .doOnSubscribe((c) -> progressObserver.showProgressDialogObserver())
                .subscribeOn(AndroidSchedulers.mainThread()) // 指定doOnSubscribe运行在主线程
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(progressObserver));
    }
}

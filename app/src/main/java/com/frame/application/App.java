package com.frame.application;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.Utils;
import com.frame.common.CommonData;
import com.frame.core.dao.DaoMaster;
import com.frame.core.dao.DaoSession;
import com.frame.injector.component.AppComponent;
import com.frame.injector.component.ComponentHolder;
import com.frame.injector.component.DaggerAppComponent;
import com.frame.injector.module.AppModule;
import com.frame.observers.RecycleObserver;

import androidx.multidex.MultiDex;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static com.frame.common.CommonData.TAG;
import static com.frame.utils.LogUtilKt.logd;
import static com.frame.utils.LogUtilKt.logi;


public class App extends Application {

    private static App mInstance;
    private DaoSession mDaoSession;
    private long mStartAppTime;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mStartAppTime = System.currentTimeMillis();

        initToolsMainThread();
        initToolsSubThread();
    }

    public static synchronized App getInstance() {
        return mInstance;
    }

    /**
     * =========================================================== Main Thread ===========================================================
     */
    private void initToolsMainThread() {
        initComponent();           // Dagger2

        Utils.init(this);          // AndroidUtilCode
        MultiDex.install(this);    // 方法超过65k需要个多个dex，也就是multidex

        logi(TAG, "onCreate Main Thread Time: " + (System.currentTimeMillis() - mStartAppTime));
    }

    private void initComponent() {
        AppComponent appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
        ComponentHolder.setAppComponent(appComponent);
    }

    /**
     * =========================================================== Sub Thread ===========================================================
     */
    private void initToolsSubThread() {
        Observable.create((ObservableOnSubscribe<String>) emitter ->
                initToolsSubThreadImpl()).subscribeOn(Schedulers.io()).subscribe(new RecycleObserver<>());
    }

    private void initToolsSubThreadImpl() {
        CrashUtils.init();         // need android.permission.WRITE_EXTERNAL_STORAGE
        RxJavaPlugins.setErrorHandler(Throwable::printStackTrace);  //这里处理所有的Rxjava异常
        initGreenDao();            // GreenDao

        logd(TAG, "onCreate Sub Thread Time: " + (System.currentTimeMillis() - mStartAppTime));
    }

    private void initGreenDao() {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(this, CommonData.DB_NAME);
        SQLiteDatabase database = devOpenHelper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(database);
        mDaoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

}

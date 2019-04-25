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
import com.squareup.leakcanary.LeakCanary;

public class AndApp extends Application {

    private static AndApp mInstance;
    private DaoSession mDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        initComponent();           // Dagger2
        initGreenDao();            // GreenDao
        LeakCanary.install(this);  // LeakCanary
        Utils.init(this);          // AndroidUtilCode
        CrashUtils.init();         // need android.permission.WRITE_EXTERNAL_STORAGE
    }

    public static synchronized AndApp getInstance() {
        return mInstance;
    }

    private void initComponent() {
        AppComponent appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
        ComponentHolder.setAppComponent(appComponent);
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

package com.born.frame.application;

import android.app.Application;

import com.born.frame.injector.component.AppComponent;
import com.born.frame.injector.component.ComponentHolder;
import com.born.frame.injector.component.DaggerAppComponent;
import com.born.frame.injector.module.AppModule;
import com.squareup.leakcanary.LeakCanary;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initCrashHandler();
        initComponent();           // Dagger2
        LeakCanary.install(this);  // LeakCanary
    }

    private void initCrashHandler() {
        AppCrashHandler crashHandler = AppCrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }

    private void initComponent() {
        AppComponent appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        ComponentHolder.setAppComponent(appComponent);
    }
}

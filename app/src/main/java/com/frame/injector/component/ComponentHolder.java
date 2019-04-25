package com.frame.injector.component;

/**
 * Created by min on 2016/12/15.
 */

public class ComponentHolder {

    private static AppComponent mAppComponent;

    public static void setAppComponent(AppComponent appComponent) {
        mAppComponent = appComponent;
    }

    public static AppComponent getAppComponent() {
        return mAppComponent;
    }
}

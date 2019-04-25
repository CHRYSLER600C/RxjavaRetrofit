package com.frame.injector.component;

import android.content.Context;

import com.frame.httputils.OkHttpUtil;
import com.frame.httputils.OkHttpUtil2;
import com.frame.injector.module.AppModule;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    Context getContext();

    void inject(OkHttpUtil okHttpUtil); //Rxjava+Retrofit的OkHttpUtil

    void inject(OkHttpUtil2 okHttpUtil2); //原生态的OkHttpUtil
}

package com.born.frame.injector.component;

import android.content.Context;

import com.born.frame.httputils.OkHttpUtil;
import com.born.frame.httputils.OkHttpUtil2;
import com.born.frame.injector.module.AppModule;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    Context getContext();

    void inject(OkHttpUtil okHttpUtil); //Rxjava+Retrofit的OkHttpUtil

    void inject(OkHttpUtil2 okHttpUtil2); //原生态的OkHttpUtil
}

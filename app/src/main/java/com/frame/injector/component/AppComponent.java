package com.frame.injector.component;

import android.content.Context;

import com.frame.httputils.OkHttpUtil;
import com.frame.httputils.OkHttpUtil2;
import com.frame.injector.module.AppModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * 注意：dependencies使用时
 *     1、不同的Component 上，不能写同样的scope
 *     2.没有scope的Component不能去依赖有scope的Component
 *
 *  subComponent也可以实现dependencies同样的作用，只是前者生成的文件在同一个类
 */

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    Context getContext();

    void inject(OkHttpUtil okHttpUtil); //Rxjava+Retrofit的OkHttpUtil

    void inject(OkHttpUtil2 okHttpUtil2); //原生态的OkHttpUtil
}

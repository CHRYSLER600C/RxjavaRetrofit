package com.frame.injector.component;

import android.content.Context;

import com.frame.httputils.OkHttpUtil;
import com.frame.httputils.OkHttpUtil2;
import com.frame.injector.module.AppModule;
import com.frame.injector.module.SubComponentModule;
import com.frame.injector.module.TestInterfaceModule;
import com.frame.injector.scope.AppScope;

import dagger.Component;

/**
 * 注意：dependencies使用时
 * 1、不同的Component 上，不能写同样的scope
 * 2.没有scope的Component不能去依赖有scope的Component
 * <p>
 * subComponent也可以实现dependencies同样的作用，只是前者生成的文件在同一个类
 */

/**
 * @Singleton是Dagger提供的一种作用域实现 作用域就是用来管理Component来获取对象实例的生命周期的
 * eg.如果你在某个Activity里面初始化的Component，那么单例的作用域就仅限于那个Activity，局部单例 VS 全局单例
 * <p>
 * module的作用域必须和Component的作用域保持一致
 */

@AppScope
@Component(modules = {AppModule.class, SubComponentModule.class, TestInterfaceModule.class})
public interface AppComponent {

    Context getContext();

    void inject(OkHttpUtil okHttpUtil); //Rxjava+Retrofit的OkHttpUtil

    void inject(OkHttpUtil2 okHttpUtil2); //原生态的OkHttpUtil

    TestComponent.Factory testComponent();
}

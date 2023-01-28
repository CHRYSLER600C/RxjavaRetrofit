package com.frame.injector.module;


import com.frame.injector.testInterface.AInterface;
import com.frame.injector.testInterface.AInterfaceImpl01;
import com.frame.injector.testInterface.AInterfaceImpl2;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public abstract class TestInterfaceModule {

    //绑定接口
    @Binds
    abstract AInterface bindAInterface(AInterfaceImpl01 impl);

    @Provides
    static AInterfaceImpl01 provideAInterfaceImpl1() {
        return new AInterfaceImpl01();
    }

    @Provides
    static AInterfaceImpl2 provideAInterfaceImpl02() {
        return new AInterfaceImpl2();
    }
}

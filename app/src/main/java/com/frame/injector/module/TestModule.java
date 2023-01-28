package com.frame.injector.module;


import com.frame.dataclass.bean.NameValue;

import dagger.Module;
import dagger.Provides;

@Module
public class TestModule {

    @Provides
    NameValue provideNameValue(){
        return new NameValue("testName", "testValue");
    }
}

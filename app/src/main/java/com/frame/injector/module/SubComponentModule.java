package com.frame.injector.module;


import com.frame.injector.component.TestComponent;

import dagger.Module;

@Module(subcomponents = TestComponent.class)
public class SubComponentModule {
}

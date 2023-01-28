package com.frame.injector.component;


import com.frame.activity.GroupActivity;
import com.frame.injector.module.TestModule;

import dagger.Subcomponent;

@Subcomponent(modules = {TestModule.class})
public interface TestComponent {

    @Subcomponent.Factory
    interface Factory {
        TestComponent create();
    }

    void inject(GroupActivity groupActivity);
}

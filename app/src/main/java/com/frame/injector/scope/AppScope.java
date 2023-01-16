package com.frame.injector.scope;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 单例
 */

@Scope
@Documented
@Retention(RUNTIME)
public @interface AppScope {}

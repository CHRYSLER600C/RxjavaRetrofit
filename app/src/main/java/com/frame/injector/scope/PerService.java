package com.frame.injector.scope;

import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Scope 提供生命周期范围内单例的功能.
 */
@Scope
@Retention(RUNTIME)
public @interface PerService {
}

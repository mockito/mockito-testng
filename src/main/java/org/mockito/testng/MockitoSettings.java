/*
 * Copyright (c) 2020 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.testng;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mockito.quality.Strictness;

/**
 * Annotation that can configure Mockito settings. Used by {@link MockitoTestNGListener}
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface MockitoSettings {

    /**
     * Configure the strictness used in this test.
     *
     * @return The strictness to configure, by default {@link Strictness#STRICT_STUBS}
     */
    Strictness strictness() default Strictness.STRICT_STUBS;
}

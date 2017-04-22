package com.zerohouse.tester.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface Example {
    String value();

    Class<?> jsonType() default void.class;

    Class<?> type() default void.class;

    String description() default "";

    String title() default "";
}

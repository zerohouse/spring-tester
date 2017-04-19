package com.zerohouse.tester.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface Subclass {
    Class<?> value();

    String name() default "List element";

    String description() default "";

    String type() default "";
}

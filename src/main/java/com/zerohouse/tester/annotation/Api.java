package com.zerohouse.tester.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Api {

    String value();

    String description() default "";

    Example[] responses() default {};

    ExceptionDescription[] exceptions() default {};

    Subclass[] subClasses() default @Subclass(void.class);
}

package com.zerohouse.tester.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Api {

    String value();

    String description() default "";

    String parameter() default "";

    Class parameterType() default Map.class;

}

package com.zerohouse;


import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiDescription {

    String name() default "";

    String apiFor() default "";

    String description() default "";

    String parameterDescription() default "";
}

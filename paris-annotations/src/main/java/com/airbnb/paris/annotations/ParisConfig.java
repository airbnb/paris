package com.airbnb.paris.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.PACKAGE)
public @interface ParisConfig {

    String defaultStyleNameFormat() default "";

    Class<?> rClass() default Void.class;

    /**
     * If false the Paris class won't be generated for this module (default is true). Currently
     * the Paris class can only be generated in a single module
     */
    boolean generateParisClass() default true;
}

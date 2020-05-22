package com.airbnb.paris.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.PACKAGE)
public @interface ParisConfig {

    String defaultStyleNameFormat() default "";

    Class<?> rClass() default Void.class;

    /**
     * This is an experimental gradle flag (android.namespacedRClass=true). Setting to true allows Paris to generate code compatible
     * with R files that only have resources from the module the resource was declared in.
     */
    boolean namespacedResourcesEnabled() default false;
}

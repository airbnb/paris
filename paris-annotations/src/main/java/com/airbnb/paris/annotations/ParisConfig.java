package com.airbnb.paris.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Note that when using KAPT with incremental annotation processing it is recommended to only use this annotation on class or interface elements,
 * not on package elements in package-info.java. This is because there is a bug where package-info is not properly reprocessed in incremental builds.
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.PACKAGE, ElementType.TYPE})
public @interface ParisConfig {

    String defaultStyleNameFormat() default "";

    Class<?> rClass() default Void.class;

    /**
     * This is an experimental gradle flag (android.namespacedRClass=true). Setting to true allows Paris to generate code compatible
     * with R files that only have resources from the module the resource was declared in.
     */
    boolean namespacedResourcesEnabled() default false;
}

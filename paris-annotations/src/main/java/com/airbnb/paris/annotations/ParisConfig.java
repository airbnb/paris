package com.airbnb.paris.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Place this annotation on a single class or interface within your module to specify configuration options for that module.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface ParisConfig {

    String defaultStyleNameFormat() default "";

    Class<?> rClass() default Void.class;

    /**
     * This is an experimental gradle flag (android.namespacedRClass=true). Setting to true allows Paris to generate code compatible with R files that
     * only have resources from the module the resource was declared in.
     */
    boolean namespacedResourcesEnabled() default false;

    /**
     * By default no Paris class is generated if a module contains no @Styleable classes.
     * However, if this is set to true a Paris class will still be generated in that case, using only
     * the @Styleables that are discovered on the class path.
     */
    boolean aggregateStyleablesOnClassPath() default false;
}

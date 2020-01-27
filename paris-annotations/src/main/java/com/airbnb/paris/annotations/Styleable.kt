package com.airbnb.paris.annotations

/**
 * @param value The name of the styleable resource.
 * @param emptyDefaultStyle Set to true if the view does not have a default style.
 * Will only be used if [ParisConfig.namespacedResourcesEnabled] is true.
 */
@Target(AnnotationTarget.CLASS)
annotation class Styleable(val value: String = "", val emptyDefaultStyle: Boolean = false)

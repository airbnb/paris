package com.airbnb.paris.annotations

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_SETTER)
annotation class Attr(
    val value: Int,
    val defaultValue: Int = -1
)

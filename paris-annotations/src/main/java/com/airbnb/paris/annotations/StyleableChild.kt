package com.airbnb.paris.annotations

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class StyleableChild(
    val value: Int,
    val defaultValue: Int = -1
)

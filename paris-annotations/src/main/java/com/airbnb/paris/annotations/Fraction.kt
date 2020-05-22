package com.airbnb.paris.annotations

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Fraction(
    val base: Int = 1,
    val pbase: Int = 1
)

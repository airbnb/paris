package com.airbnb.paris.annotations

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Fraction(
        val base: Int = 1,
        val pbase: Int = 1)

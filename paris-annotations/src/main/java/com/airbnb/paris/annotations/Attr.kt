package com.airbnb.paris.annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Attr(
        val value: Int,
        val defaultValue: Int = -1)
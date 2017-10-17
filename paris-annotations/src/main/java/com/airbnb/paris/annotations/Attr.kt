package com.airbnb.paris.annotations

@Target(AnnotationTarget.FUNCTION)
annotation class Attr(
        val value: Int,
        val defaultValue: Int = -1)
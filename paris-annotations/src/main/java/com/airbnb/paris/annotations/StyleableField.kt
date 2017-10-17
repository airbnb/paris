package com.airbnb.paris.annotations

@Target(AnnotationTarget.FIELD)
annotation class StyleableField(
        val value: Int,
        val defaultValue: Int = -1)

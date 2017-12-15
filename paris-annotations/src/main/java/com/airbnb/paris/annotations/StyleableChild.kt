package com.airbnb.paris.annotations

@Target(AnnotationTarget.FIELD)
annotation class StyleableChild(
        val value: Int,
        val defaultValue: Int = -1)

package com.airbnb.paris.annotations

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class StyleableField(
        val value: Int,
        val defaultValue: Int = -1)
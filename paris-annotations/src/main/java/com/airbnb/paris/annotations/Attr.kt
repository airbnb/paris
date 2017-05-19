package com.airbnb.paris.annotations

@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Attr(val value: Int, val format: Format = Format.DEFAULT, val defaultValue: Int = -1)
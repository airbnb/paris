package com.airbnb.paris.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Styleable(val value: String = "")

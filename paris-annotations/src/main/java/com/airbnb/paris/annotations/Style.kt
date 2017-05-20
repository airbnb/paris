package com.airbnb.paris.annotations

@Target
@Retention(AnnotationRetention.SOURCE)
annotation class Style(val name: String, val id: Int)
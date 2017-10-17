package com.airbnb.paris.annotations

@Target(AnnotationTarget.CLASS)
annotation class Styleable(val value: String = "")

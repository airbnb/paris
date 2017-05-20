package com.airbnb.paris.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Styleable(
        val value: String = "",
        val dependencies: Array<KClass<*>> = emptyArray(),
        val styles: Array<Style> = emptyArray())
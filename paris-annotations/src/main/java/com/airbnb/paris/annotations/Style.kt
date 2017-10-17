package com.airbnb.paris.annotations

import kotlin.annotation.AnnotationTarget.*

@Target(FIELD, FUNCTION)
annotation class Style(val isDefault: Boolean = false)

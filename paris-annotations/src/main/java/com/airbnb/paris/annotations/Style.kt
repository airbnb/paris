package com.airbnb.paris.annotations

import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.annotation.AnnotationTarget.FUNCTION

@Target(FIELD, FUNCTION)
annotation class Style(val isDefault: Boolean = false)

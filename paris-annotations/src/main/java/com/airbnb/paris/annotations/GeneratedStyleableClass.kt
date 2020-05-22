package com.airbnb.paris.annotations

import kotlin.reflect.KClass

/**
 * DO NOT USE. This annotation is meant to be used by generated classes only
 */
@Target(AnnotationTarget.CLASS)
annotation class GeneratedStyleableClass(val value: KClass<*>)

package com.airbnb.paris.processor.android_resource_scanner

import androidx.room.compiler.processing.XElement
import kotlin.reflect.KClass

interface ResourceScanner {
    /**
     * Returns the [AndroidResourceId] that is used as an annotation value of the given [XElement]
     */
    fun getId(
        annotation: KClass<out Annotation>,
        element: XElement,
        value: Int
    ): AndroidResourceId?
}
package com.airbnb.paris.processor.framework.models

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XMethodElement
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.isMethod
import com.airbnb.paris.processor.BaseProcessor

abstract class SkyMethodModel private constructor(
    val enclosingElement: XTypeElement,
    val element: XMethodElement,
) : SkyModel {
    val name: String get() = element.name

    protected constructor(element: XMethodElement) : this(
        element.enclosingElement as XTypeElement,
        element
    )
}

typealias SkyStaticMethodModel = SkyMethodModel

abstract class SkyMethodModelFactory<T : SkyMethodModel>(
    processor: BaseProcessor,
    annotationClass: Class<out Annotation>
) : JavaSkyModelFactory<T, XMethodElement>(processor, annotationClass) {

    override fun filter(element: XElement): Boolean = element.isMethod()
}

typealias SkyStaticMethodModelFactory<T> = SkyMethodModelFactory<T>
